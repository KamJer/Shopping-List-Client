package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.ErrorMessage;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnConnectAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginDialogViewModel extends CustomViewModel {

    private LiveData<List<User>> usersLiveData;

    public LoginDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<LoginDialogViewModel> initializer = new ViewModelInitializer<>(LoginDialogViewModel.class, creationExtras ->
            new LoginDialogViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));


    public void logUser(User user, OnConnectAction successAction, OnConnectAction failureAction, OnFailureAction onConecctionFailureAction) {
        shoppingServiceRepository.logUser(user, new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean result = Boolean.TRUE.equals(response.body());
                    if (result) {
                        successAction.action();
                    } else {
                        failureAction.action();
                    }
                } else {
                    onConecctionFailureAction.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                onConecctionFailureAction.action(t);
            }
        });
    }

    public void insertUser(User user, OnConnectAction onSuccessAction, OnFailureAction action) {
        shoppingServiceRepository.insertUser(user, new Callback<LocalDateTime>() {
            @Override
            public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                if (response.isSuccessful()) {
                    user.setSavedTime(response.body());
                    insertUser(user);
                    onSuccessAction.action();
                } else if(response.code() == 400) {
                    action.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                } else {
                    action.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocalDateTime> call, @NonNull Throwable t) {
                action.action(t);
            }
        });
    }

    public void insertUser(User user) {
        sharedRepository.insertUser(user);
        shoppingRepository.insertUser(user);

    }

    public void loadAllUsers() {
        usersLiveData = shoppingRepository.loadAllUsers();
    }

    public void setOnUsersObserver(LifecycleOwner owner, Observer<List<User>> observer) {
        usersLiveData.observe(owner, observer);
    }

    public void deleteUser(User user) {
        sharedRepository.deleteUser();
        shoppingRepository.deleteUser(user);
    }
}
