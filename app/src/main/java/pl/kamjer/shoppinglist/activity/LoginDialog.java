package pl.kamjer.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.ShoppingListActivity;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.viewmodel.LoginDialogViewModel;

@Log
public class LoginDialog extends AppCompatActivity {

    private final OnFailureAction connectionFailedAction =
            (t) -> {
                String message = Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error");
                log.log(Level.WARNING, message);
                createToast(message);
            };

    private LoginDialogViewModel loginDialogViewModel;

    private EditText userNameEditText;
    private EditText passwordEditText;

    private final View.OnClickListener onLoginButtonAction = (v) -> {
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();
        ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this, user);
        loginDialogViewModel.logUser(user,
                () -> {
                    loginDialogViewModel.insertUser(user);
                    this.startShoppingListActivity();
                },
                () -> {
                    createToast("No such User try again");
                },
                (t) -> {
                    connectionFailedAction.action(t);
                    loginDialogViewModel.insertUser(user);
                    this.startShoppingListActivity();
                });
    };

    private final View.OnClickListener onRegisterButtonAction = (v) -> {
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();
        loginDialogViewModel.insertUser(
                user,
                () -> {
                    ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this, user);
                    this.startShoppingListActivity();
                },
                connectionFailedAction);
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_layout);
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(this));

        loginDialogViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(LoginDialogViewModel.initializer)
        ).get(LoginDialogViewModel.class);

        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(onLoginButtonAction);
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(onRegisterButtonAction);
    }

    private void createToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void startShoppingListActivity() {
        Intent shoppingListActivity = new Intent(this, ShoppingListActivity.class);
        this.startActivity(shoppingListActivity);
    }
}
