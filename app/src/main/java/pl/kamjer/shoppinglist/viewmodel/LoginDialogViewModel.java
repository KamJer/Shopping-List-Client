package pl.kamjer.shoppinglist.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.User;
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

    public void insertUser(User user, OnFailureAction action) {
        shoppingServiceRepository.insertUser(user, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                if (response.isSuccessful()) {
                    user.setSavedTime(response.body());
                    insertUser(user);
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

    public void initialzeshoppingservicerepository(Context applicationContext) {
        shoppingServiceRepository.initialize(applicationContext);
    }
}
