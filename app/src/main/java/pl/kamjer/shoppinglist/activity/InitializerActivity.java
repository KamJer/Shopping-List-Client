package pl.kamjer.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.logindialog.LoginDialogForcedLogin;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.ShoppingListActivity;
import pl.kamjer.shoppinglist.model.dto.TokenDto;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.viewmodel.InitializerViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class InitializerActivity extends GenericActivity {

    /**
     * ViewModel for managing the initialization process of the application.
     * Handles user authentication, data synchronization, and connection setup.
     */
    private InitializerViewModel initializerViewModel;

    /**
     * TextView component that displays the initialization status messages to the user.
     */
    private TextView initializertextView;

    /**
     * Observer for updating the initialization label text.
     * Updates the UI with the current initialization status message.
     */
    private final Observer<String> initializerLabelObserver = s -> initializertextView.setText(s);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initializer_layout);

        // Initialize ViewModel for the initialization process
        initializerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(InitializerViewModel.initializer)
        ).get(InitializerViewModel.class);

        // Initialize all necessary components of the app
        try {
            initializerViewModel.initialize(getApplicationContext());
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                 NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        initializerViewModel.loadUser();

        // Find and initialize the status text view
        initializertextView = findViewById(R.id.initializerLabel);

        // Set observer for the logged user
        initializerViewModel.setUserLiveDataObserver(user -> {
            // If connection already exists, disconnect it
            initializerViewModel.websocketDisconnect();
            initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_connection_to_server_label));

            // If user is null, this means no user data was saved, so it needs to be created and inserted
            if (user != null) {
                initializeUserConnection(user);
                initializerViewModel.refreshUser(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<TokenDto> call, @NonNull Response<TokenDto> response) {
                        Optional.ofNullable(response.body())
                                .ifPresent(token -> {
                                    if (token.getRefreshToken() != null && token.getAccessToken() != null) {
                                        user.setPassword(token.getRefreshToken());
                                        user.setAccessToken(token.getAccessToken());
                                        logUserInAndInitialize(user);
                                    } else {
                                        createToast(getString(R.string.no_such_user_exists_message));
                                    }
                                });
                    }

                    @Override
                    public void onFailure(@NonNull Call<TokenDto> call, @NonNull Throwable t) {
                        createToast(t.getMessage());
                    }
                });

                // Initialize WebSocket connection with user
                initializerViewModel.initializeOnMessageAction(user,
                        (webSocket, object) ->
                                createToast(object),
                        (webSocket, t, response) -> {
                            if (response != null) {
                                if (response.code() == 401) {
                                    // If logged user does not exist for whatever reason, inform user about that and log them out
                                    initializerViewModel.logUserOff(user);
                                    createToast(getString(R.string.no_such_user_exists_message));
                                } else {
                                    // Inform user about error
                                    createToast(t.getMessage());
                                }
                            } else {
                                createToast(t.getMessage());
                            }
                        });

                actOnSuccessOrOffline(user);
            } else {
                // Force user to log in if no user data exists
                startLogDialog();
            }
        });

        // Initialize observer for the label on the screen
        initializerViewModel.setInitializerLabelLiveDataObserver(this, initializerLabelObserver);
    }

    /**
     * Starts the login dialog activity to force user authentication.
     * This is called when no existing user data is found.
     */
    private void startLogDialog() {
        Intent loginDialogIntent = new Intent(this, LoginDialogForcedLogin.class);
        this.startActivity(loginDialogIntent);
    }

    /**
     * Starts the shopping list activity after successful initialization.
     * Reinitializes the shopping service repository with the user and synchronizes data.
     */
    private void startShoppingListActivity() {
        Intent shoppingListActivity = new Intent(this, ShoppingListActivity.class);
        this.startActivity(shoppingListActivity);
    }

    /**
     * Handles successful initialization or offline scenarios.
     */
    private void actOnSuccessOrOffline(User user) {
        logUserInDevice(user);
        startShoppingListActivity();
    }

    protected void logUserInAndInitialize(User user) {
        initializeWebsocketConnection(user);
        initializerViewModel.synchronizeData(user);
        initializerViewModel.updateRefreshToken(user);
    }

    protected void logUserInDevice(User user) {
        initializerViewModel.insertUser(user);
    }

    protected void initializeUserConnection(User user) {
        ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(user);
    }

    protected void initializeWebsocketConnection(User user) {
        initializerViewModel.initializeWebsocketConnection(this.getApplicationContext(), user);
    }
}
