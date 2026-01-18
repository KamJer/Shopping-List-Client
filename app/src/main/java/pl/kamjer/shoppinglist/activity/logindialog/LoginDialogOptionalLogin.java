package pl.kamjer.shoppinglist.activity.logindialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Optional;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.logindialog.usersrecyclerview.UsersRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteUserAction;
import pl.kamjer.shoppinglist.util.validation.UserValidator;
import pl.kamjer.shoppinglist.viewmodel.LoginDialogViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Log
public class LoginDialogOptionalLogin extends GenericActivity {

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
        if (!UserValidator.isUserValid(user)) {
            createToast(getString(R.string.user_name_can_not_be_empty_message));
            return;
        }

        loginDialogViewModel.isUserCorrect(user, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                Optional.of(response).ifPresent(booleanResponse -> {
                    if (Boolean.TRUE.equals(booleanResponse.body())) {
                        logUserInAndInitialize(user);
                    } else {
                        actOnErrorLogin();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                actOnErrorLogin();
            }
        });
    };

    protected final View.OnClickListener onRegisterButtonAction = (v) -> {
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();
        if (!UserValidator.isUserValid(user)) {
            createToast(getString(R.string.user_name_can_not_be_empty_message));
            return;
        }
        loginDialogViewModel.initializeShoppingServiceRepository(getApplicationContext());
        loginDialogViewModel.insertUser(
                user,
                connectionFailedAction);
    };

    protected final DeleteUserAction deleteUserAction = user ->
            loginDialogViewModel.deleteUser(user);
    protected final DeleteUserAction logUserAction = this::logUserInAndInitialize;

    protected void logUserInAndInitialize(User user) {
        loginDialogViewModel.insertUser(user);
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

//        setting action bar
        shoppingListActionBar = findViewById(R.id.appBar);
        shoppingListActionBar.create(this);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayShowTitleEnabled(false));

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

    private void actOnErrorLogin() {
        createToast(getString(R.string.no_such_user_exists_message));
    }
}
