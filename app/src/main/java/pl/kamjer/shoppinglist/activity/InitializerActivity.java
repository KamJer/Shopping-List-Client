package pl.kamjer.shoppinglist.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.logindialog.LoginDialogForcedLogin;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.ShoppingListActivity;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.viewmodel.InitializerViewModel;

import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

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
        initializerViewModel.initialize(getApplicationContext());

        initializertextView = findViewById(R.id.initializerLabel);

        initializerViewModel.loadUser();
        initializerViewModel.setUserLiveDataObserver(user -> {
            initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_connection_to_server_label));
//            if user is null this means no user data was saved, so it needs to be created and inserted,
            if (user != null) {
                initializerViewModel.initializeOnMessageAction(user,
                        (webSocket, object) -> createToast(object),
                        (t) -> {
                            createToast(t.getMessage());
//                            startLogDialog();
                        });
                ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this.getApplicationContext(), user);
                initializerViewModel.synchronizeData(user);
                startShoppingListActivity();
            } else {
                startLogDialog();
            }
        });
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
}
