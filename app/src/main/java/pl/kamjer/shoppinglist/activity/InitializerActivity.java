package pl.kamjer.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.ShoppingListActivity;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.viewmodel.InitializerViewModel;

@Log

public class InitializerActivity extends AppCompatActivity {

    private InitializerViewModel initializerViewModel;

    private final OnFailureAction connectionFailedAction =
            (t) -> {
                log.log(Level.WARNING, Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error"));
                createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error"));
                startShoppingListActivity();
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(this));

        initializerViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(InitializerViewModel.initializer)
        ).get(InitializerViewModel.class);

//        initialize all of a necessary components of an app
        ShoppingRepository.getShoppingRepository().initialize(this);
        SharedRepository.getSharedRepository().initialize(this);
        ShoppingServiceRepository.getShoppingServiceRepository().initialize(this);

        initializerViewModel.loadUser();
        initializerViewModel.setUserLiveDataObserver(this, user -> {
//            if user is null this means no user data was saved, so it needs to be created and inserted,
            if (user != null) {
                if (initializerViewModel.getOldLoggedUser() == null || (!initializerViewModel.getOldLoggedUser().equals(user))) {
                    ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this, user);
                    initializerViewModel.logUser(user, () -> {
                                initializerViewModel.synchronizeData(connectionFailedAction);
                                startShoppingListActivity();
                            }, () -> {
                                createToast("Such user does not exists, log to existing account");
                                startLogDialog();
                            },
                            connectionFailedAction);
                }
                initializerViewModel.setOldLoggedUser(user);
            } else {
                startLogDialog();
            }
        });
    }

    private void createToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void startLogDialog() {
        Intent loginDialogIntent = new Intent(this, LoginDialog.class);
        this.startActivity(loginDialogIntent);
    }

    private void startShoppingListActivity() {
        Intent shoppingListActivity = new Intent(this, ShoppingListActivity.class);
        this.startActivity(shoppingListActivity);
    }
}
