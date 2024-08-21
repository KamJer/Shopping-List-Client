package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AllIdDto;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnSuccessAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class LoginDialogViewModel extends ViewModel {

    private static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

    private final ShoppingServiceRepository shoppingServiceRepository;
    private final ShoppingRepository shoppingRepository;
    private final SharedRepository sharedRepository;

    private LiveData<User> userLiveData;

    public static final ViewModelInitializer<LoginDialogViewModel> initializer = new ViewModelInitializer<>(LoginDialogViewModel.class, creationExtras ->
            new LoginDialogViewModel(ShoppingServiceRepository.getShoppingServiceRepository(),
                    ShoppingRepository.getShoppingRepository(),
                    SharedRepository.getSharedRepository()));


    public void logUser(User user, OnSuccessAction successAction, OnSuccessAction failureAction, OnFailureAction onConecctionFailureAction) {
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
                    onConecctionFailureAction.action(new NotOkHttpResponseException(CONNECTION_FAILED_MESSAGE + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                onConecctionFailureAction.action(t);
            }
        });
    }

    public void insertUser(User user, OnSuccessAction onSuccessAction, OnFailureAction action) {
        shoppingServiceRepository.insertUser(user, new Callback<LocalDateTime>() {
            @Override
            public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                if (response.isSuccessful()) {
                    user.setSavedTime(response.body());
                    insertUser(user);
                    onSuccessAction.action();
                } else if(response.code() == 400) {
                    action.action(new NotOkHttpResponseException("User name taken, try with different name"));
                } else {
                    action.action(new NotOkHttpResponseException(CONNECTION_FAILED_MESSAGE + response.code()));
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

    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }
}
