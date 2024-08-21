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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
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
@Getter
@Log
public class ShoppingListViewModel extends ViewModel {

    private static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;
    private final SharedRepository sharedRepository;

    private LiveData<List<ShoppingItemWithAmountTypeAndCategory>> allShoppingItemWithAmountTypeAndCategoryLiveData;
    private LiveData<List<Category>> allCategoryLiveData;
    private LiveData<List<AmountType>> allAmountTypeLiveData;
    private LiveData<User> userLiveData;

    public static final ViewModelInitializer<ShoppingListViewModel> initializer = new ViewModelInitializer<>(ShoppingListViewModel.class, creationExtras ->
            new ShoppingListViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    public void loadAllShoppingItemWithAmountTypeAndCategory() {
        allShoppingItemWithAmountTypeAndCategoryLiveData = shoppingRepository
                .loadAllShoppingItemsWithAmountTypeAndCategory(getUserValue());
    }

    public void setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItemWithAmountTypeAndCategory>> observer) {
        allShoppingItemWithAmountTypeAndCategoryLiveData.observe(owner, observer);
    }

    public List<ShoppingItemWithAmountTypeAndCategory> getAllShoppingItemWithAmountTypeAndCategoryValue() {
        return Optional.ofNullable(allShoppingItemWithAmountTypeAndCategoryLiveData.getValue()).orElse(new ArrayList<>());
    }

    public void insertShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.insertShoppingItem(getUserValue(), shoppingItem, () ->
                shoppingServiceRepository.insertShoppingItem(shoppingItem, new Callback<AddDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AddDto> call, @NonNull Response<AddDto> response) {
                        if (response.isSuccessful()) {
                            AddDto addDto = Optional.ofNullable(response.body()).orElseThrow(InvalidServerResponseBodyException::new);
                            Long newId = Optional.ofNullable(addDto.getNewId()).orElseThrow(InvalidServerResponseBodyException::new);
                            LocalDateTime savedTime = Optional.ofNullable(addDto.getSavedTime()).orElseThrow(InvalidServerResponseBodyException::new);
                            shoppingItem.setShoppingItemId(newId);
                            User user = getUserValue();
                            user.setSavedTime(savedTime);
                            shoppingRepository.updateUser(user);
                            shoppingRepository.updateShoppingItem(shoppingItem);
                        } else {
                            action.action(new NotOkHttpResponseException(CONNECTION_FAILED_MESSAGE + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddDto> call, @NonNull Throwable t) {
                        action.action(t);
                    }
                }));
    }

    public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingRepository.updateShoppingItems(shoppingItems);
    }

    public void updateShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, () ->
                shoppingServiceRepository.updateShoppingItem(shoppingItem, new Callback<LocalDateTime>() {
                    @Override
                    public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                        if (response.isSuccessful()) {
                            User user = getUserValue();
                            user.setSavedTime(response.body());
//                            this data is current so it is no longer updated
                            shoppingItem.setUpdated(false);
                            shoppingRepository.updateShoppingItem(shoppingItem);
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
        allCategoryLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    public ArrayList<Category> getAllCategoryValue() {
        return (ArrayList<Category>) Optional.ofNullable(allCategoryLiveData.getValue()).orElse(new ArrayList<>());
    }

    public void setAllCategoryObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }

    public void deleteCategory(Category category, OnFailureAction action) {
        shoppingRepository.deleteCategorySoft(category, () ->
                shoppingServiceRepository.deleteCategory(category, new Callback<LocalDateTime>() {
                    @Override
                    public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                        if (response.isSuccessful()) {
                            User user = getUserValue();
                            user.setSavedTime(response.body());
                            shoppingRepository.updateUser(user);
                            shoppingRepository.deleteCategory(category);
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

    public void insertCategory(Category category, OnFailureAction action) {
        shoppingRepository.insertCategory(getUserValue(), category, () ->
                shoppingServiceRepository.insertCategory(category, new Callback<AddDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AddDto> call, @NonNull Response<AddDto> response) {
                        if (response.isSuccessful()) {
                            AddDto addDto = Optional.ofNullable(response.body()).orElseThrow(InvalidServerResponseBodyException::new);
                            Long newId = Optional.ofNullable(addDto.getNewId()).orElseThrow(InvalidServerResponseBodyException::new);
                            LocalDateTime savedTime = Optional.ofNullable(addDto.getSavedTime()).orElseThrow(InvalidServerResponseBodyException::new);

                            category.setCategoryId(newId);
                            User user = getUserValue();
                            user.setSavedTime(savedTime);
                            shoppingRepository.updateUser(user);
                            shoppingRepository.updateCategory(category);
                        } else {
                            action.action(new NotOkHttpResponseException(CONNECTION_FAILED_MESSAGE + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AddDto> call, @NonNull Throwable t) {
                        action.action(t);
                    }
                }));
    }

    public void updateCategory(Category category, OnFailureAction action) {
        shoppingRepository.updateCategoryFlag(category, () ->
                shoppingServiceRepository.updateCategory(category, new Callback<LocalDateTime>() {
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

    public void loadAllAmountTypes() {
        allAmountTypeLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    public void setAllAmountTypeLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        allAmountTypeLiveData.observe(owner, observer);
    }

    public ArrayList<AmountType> getAmountTypesValue() {
        return (ArrayList<AmountType>) Optional.ofNullable(allAmountTypeLiveData.getValue()).orElse(new ArrayList<>());
    }

    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }

    public User getUserValue() throws NoUserFoundException {
        return Optional.ofNullable(userLiveData.getValue()).orElseThrow(() -> new NoUserFoundException("No user is logged"));
    }
}
