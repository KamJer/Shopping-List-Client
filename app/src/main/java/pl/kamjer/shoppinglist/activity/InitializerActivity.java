package pl.kamjer.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.logindialog.LoginDialogForcedLogin;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.ShoppingListActivity;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.viewmodel.InitializerViewModel;

@Log
public class InitializerActivity extends GenericActivity {

    private InitializerViewModel initializerViewModel;

    private TextView initializertextView;

    private final Observer<String> initializerLabelObserver = s -> initializertextView.setText(s);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initializer_layout);

        initializerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(InitializerViewModel.initializer)
        ).get(InitializerViewModel.class);

//        initialize all of a necessary components of an app
        try {
            initializerViewModel.initialize(getApplicationContext());
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                 NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        initializerViewModel.loadUser();

        initializertextView = findViewById(R.id.initializerLabel);
//        set observer for a logged user
        initializerViewModel.setUserLiveDataObserver(user -> {
//            if connection already exist disconnect it
            initializerViewModel.websocketDisconnect();
            initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_connection_to_server_label));
//            if user is null this means no user data was saved, so it needs to be created and inserted,
            if (user != null) {
                initializerViewModel.initializeOnMessageAction(user,
                        (webSocket, object) ->
                                createToast(object),
                        (webSocket, t, response) -> {
                            if (response != null) {
                                if (response.code() == 401) {
//                                if logged user does not exists for whatever reason inform user about that and logged them out
                                    initializerViewModel.logUserOff(user);
                                    createToast(getString(R.string.no_such_user_exists_message));
                                } else {
//                                inform user about error
                                    createToast(t.getMessage());
                                }
                            } else {
                                createToast(t.getMessage());
                            }
                        });
//                if everything went well start shopping list activity (
                actOnSuccessOrOffline(user);
            } else {
//                force user to log in
                startLogDialog();
            }
        });
//        initialize observer for a label on a screen
        initializerViewModel.setInitializerLabelLiveDataObserver(this, initializerLabelObserver);
    }

    private void startLogDialog() {
        Intent loginDialogIntent = new Intent(this, LoginDialogForcedLogin.class);
        this.startActivity(loginDialogIntent);
    }

    private void startShoppingListActivity() {
        Intent shoppingListActivity = new Intent(this, ShoppingListActivity.class);
        this.startActivity(shoppingListActivity);
    }

    private void actOnSuccessOrOffline(User user) {
        ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this.getApplicationContext(), user);
        initializerViewModel.synchronizeData(user);
        startShoppingListActivity();
    }
}
