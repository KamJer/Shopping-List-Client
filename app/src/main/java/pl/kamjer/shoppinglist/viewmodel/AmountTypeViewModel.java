package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AddDto;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.InvalidServerResponseBodyException;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class AmountTypeViewModel extends ViewModel {

    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;
    private final SharedRepository sharedRepository;

    private LiveData<List<AmountType>> allAmountTypeLiveData;
    private LiveData<User> userLiveData;

    public static final ViewModelInitializer<AmountTypeViewModel> initializer = new ViewModelInitializer<>(
            AmountTypeViewModel.class,
            creationExtras ->
                    new AmountTypeViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }

    public User getUserValue() throws NoUserFoundException {
        return Optional.ofNullable(userLiveData.getValue()).orElseThrow(() -> new NoUserFoundException("No user is logged"));
    }

    public void setUserLiveDataObserver(LifecycleOwner owner, Observer<User> observer) {
        userLiveData.observe(owner, observer);
    }

    public void loadAllAmountType() {
        allAmountTypeLiveData = shoppingRepository.loadAllAmountType();
    }

    public void setAllAmountTypeLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        allAmountTypeLiveData.observe(owner, observer);
    }

    public void insertAmountType(AmountType amountType, OnFailureAction action) {
        shoppingRepository.insertAmountType(amountType, () ->
                shoppingServiceRepository.insertAmountType(amountType, new Callback<AddDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AddDto> call, @NonNull Response<AddDto> response) {
                        if (response.isSuccessful()) {
                            AddDto addDto = Optional.ofNullable(response.body()).orElseThrow(InvalidServerResponseBodyException::new);
                            Long newId = Optional.ofNullable(addDto.getNewId()).orElseThrow(InvalidServerResponseBodyException::new);
                            LocalDateTime savedTime = Optional.ofNullable(addDto.getSavedTime()).orElseThrow(InvalidServerResponseBodyException::new);
                            amountType.setAmountTypeId(newId);
                            User user = getUserValue();
                            user.setSavedTime(savedTime);
                            shoppingRepository.updateUser(user);
                            shoppingRepository.updateAmountType(amountType);
                        } else {
                            action.action(new NotOkHttpResponseException("connection failed: Http code:" + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddDto> call, @NonNull Throwable t) {
                        action.action(t);
                    }
                })
        );

    }

    public void deleteAmountType(AmountType amountType, OnFailureAction action) {
        shoppingRepository.deleteAmountTypeSoft(amountType, () ->
                shoppingServiceRepository.deleteAmountType(getUserValue(), amountType, new Callback<LocalDateTime>() {
                    @Override
                    public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                        if (response.isSuccessful()) {
                            User user = getUserValue();
                            user.setSavedTime(response.body());
                            shoppingRepository.updateUser(user);
                            shoppingRepository.deleteAmountType(amountType);
                        } else {
                            action.action(new NotOkHttpResponseException(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LocalDateTime> call, @NonNull Throwable t) {
                        action.action(t);
                    }
                })
        );
    }

    public void updateAmountType(AmountType amountType, OnFailureAction action) {
        shoppingRepository.updateAmountType(amountType, () ->
                shoppingServiceRepository.updateAmountType(amountType, new Callback<LocalDateTime>() {
                    @Override
                    public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                        if (response.isSuccessful()) {
                            User user = getUserValue();
                            user.setSavedTime(response.body());
                            shoppingRepository.updateUser(user);
                        } else {
                            action.action(new NotOkHttpResponseException(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LocalDateTime> call, @NonNull Throwable t) {
                        action.action(t);
                    }
                })

        );
    }
}
