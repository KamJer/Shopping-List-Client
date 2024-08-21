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
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class BoughtShoppingItemsListViewModel extends ViewModel {

    private static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

    private final SharedRepository sharedRepository;
    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;

    private LiveData<List<ShoppingItemWithAmountTypeAndCategory>> allShoppingItemWithAmountTypeAndCategoryLiveData;
    private LiveData<List<Category>> allCategoryLiveData;

    private LiveData<User> userLiveData;

    public static final ViewModelInitializer<BoughtShoppingItemsListViewModel> initializer = new ViewModelInitializer<>(
            BoughtShoppingItemsListViewModel.class,
            creationExtras ->
                    new BoughtShoppingItemsListViewModel(SharedRepository.getSharedRepository(),
                            ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository())
    );

    public void loadAllShoppingItemWithAmountTypeAndCategoryLiveData() {
        allShoppingItemWithAmountTypeAndCategoryLiveData = shoppingRepository.loadAllShoppingItemsWithAmountTypeAndCategory();
    }

    public void setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItemWithAmountTypeAndCategory>> observer) {
        this.allShoppingItemWithAmountTypeAndCategoryLiveData.observe(owner, observer);
    }

    public void updateShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, () ->
                shoppingServiceRepository.updateShoppingItem(shoppingItem, new Callback<LocalDateTime>() {
            @Override
            public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                if (response.isSuccessful()) {
                    User user = getUserValue();
                    user.setSavedTime(response.body());
                    shoppingRepository.updateUser(user);
                } else {
                    action.action(new NotOkHttpResponseException(CONNECTION_FAILED_MESSAGE + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocalDateTime> call, @NonNull Throwable t) {
                action.action(t);
            }
        }));
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.deleteShoppingItemSoftDelete(shoppingItem, () -> shoppingServiceRepository.deleteShoppingItem(shoppingItem, new Callback<LocalDateTime>() {
            @Override
            public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                if (response.isSuccessful()) {
                    User user = getUserValue();
                    user.setSavedTime(response.body());
                    shoppingRepository.updateUser(user);
                    shoppingRepository.deleteShoppingItem(shoppingItem);
                } else {
                    action.action(new NotOkHttpResponseException(CONNECTION_FAILED_MESSAGE + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocalDateTime> call, @NonNull Throwable t) {
                action.action(t);
            }
        }));
    }

    public void loadAllCategory() {
        allCategoryLiveData = shoppingRepository.loadAllCategory();
    }

    public void setAllCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }

    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }

    public User getUserValue() throws NoUserFoundException {
        return Optional.ofNullable(userLiveData.getValue()).orElseThrow(() -> new NoUserFoundException("No user is logged"));
    }
}
