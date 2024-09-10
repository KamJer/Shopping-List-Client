package pl.kamjer.shoppinglist.activity.logindialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.logindialog.usersrecyclerview.UsersRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteUserAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.viewmodel.LoginDialogViewModel;

@Log
public class LoginDialogOptionalLogin extends GenericActivity {

    protected final OnFailureAction connectionFailedAction =
            (t) -> {
                String message = Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error");
                log.log(Level.WARNING, message);
                createToast(message);
            };

    protected LoginDialogViewModel loginDialogViewModel;

    protected EditText userNameEditText;
    protected EditText passwordEditText;
    protected ShoppingListActionBar shoppingListActionBar;

    protected RecyclerView usersRecyclerView;
    protected UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    protected final View.OnClickListener onLoginButtonAction = (v) -> {
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();

        logUserInAndInitialize(user);
    };

    protected final View.OnClickListener onRegisterButtonAction = (v) -> {
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();
        loginDialogViewModel.insertUser(
                user,
                () -> ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(this, user),
                connectionFailedAction);
    };

    protected final DeleteUserAction deleteUserAction = user ->
            loginDialogViewModel.deleteUser(user);
    protected final DeleteUserAction logUserAction = this::logUserInAndInitialize;

    protected void logUserInAndInitialize(User user) {
        if (ShoppingServiceRepository.getShoppingServiceRepository().isInitializedWithUser()) {
            ShoppingServiceRepository.getShoppingServiceRepository().initialize(getApplicationContext());
        }
        loginDialogViewModel.logUser(user,
                () -> {
                    loginDialogViewModel.insertUser(user);
                    ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(getApplicationContext(), user);
                },
                () -> createToast("No such User try again"),
                (t) -> {
                    connectionFailedAction.action(t);
                    if (!(t instanceof NotOkHttpResponseException)) {
                        loginDialogViewModel.insertUser(user);
                        ShoppingServiceRepository.getShoppingServiceRepository().reInitializeWithUser(getApplicationContext(), user);
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_layout);

        loginDialogViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(LoginDialogViewModel.initializer)
        ).get(LoginDialogViewModel.class);

        loginDialogViewModel.loadAllUsers();

        shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));

        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usersRecyclerView = findViewById(R.id.usersRecyclerView);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(onLoginButtonAction);
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(onRegisterButtonAction);

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(
                new ArrayList<>(),
                deleteUserAction,
                logUserAction
                ));

        loginDialogViewModel.setOnUsersObserver(this, users -> usersRecyclerViewAdapter.setUsers(users));
    }
}
