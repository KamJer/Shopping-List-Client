package pl.kamjer.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.logindialog.LoginDialogForcedLogin;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.ShoppingListActivity;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.viewmodel.InitializerViewModel;

@Log
public class InitializerActivity extends GenericActivity {

    private InitializerViewModel initializerViewModel;

    private TextView initializertextView;

    private final OnFailureAction connectionFailedAction =
            (t) -> {
                log.log(Level.WARNING, Optional.ofNullable(t).map(Throwable::getMessage).orElse(getString(R.string.could_not_find_reason_error_massage)));
                createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse(getString(R.string.could_not_find_reason_error_massage)));
                startShoppingListActivity();
            };

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
        initializeApp();
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(
                getApplicationContext(),
                ShoppingServiceRepository.getShoppingServiceRepository(),
                ShoppingRepository.getShoppingRepository().getExecutorService()));

        initializertextView = findViewById(R.id.initializerLabel);

        initializerViewModel.loadUser();
        initializerViewModel.setUserLiveDataObserver(user -> {
            initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_connection_to_server_label));
//            if user is null this means no user data was saved, so it needs to be created and inserted,
            if (user != null) {
                ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this, user);
                initializerViewModel.synchronizeData(user,
                        this::startShoppingListActivity
                        , () -> {
                            createToast(getString(R.string.such_user_does_not_exist_label));
//                            if user
                            startLogDialog();
                        },
                        connectionFailedAction);
            } else {
                startLogDialog();
            }
        });

        initializerViewModel.setInitializerLabelLiveDataObserver(this, initializerLabelObserver);
    }

    private void initializeApp() {
        initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_connection_to_server_label));
        ShoppingServiceRepository.getShoppingServiceRepository().initialize(getApplicationContext());
        initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_inner_files_label));
        SharedRepository.getSharedRepository().initialize(getApplicationContext());
        initializerViewModel.setInitializerLabelLiveDataValue(getString(R.string.initializing_database_label));
        ShoppingRepository.getShoppingRepository().initialize(
                getApplicationContext(),
                ShoppingServiceRepository.getShoppingServiceRepository());
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
