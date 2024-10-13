package pl.kamjer.shoppinglist.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ModifyState;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.exception.WebSocketErrorConnection;
import pl.kamjer.shoppinglist.util.funcinterface.OnConnectAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class CustomViewModel extends ViewModel {

    protected final ShoppingRepository shoppingRepository;
    protected final ShoppingServiceRepository shoppingServiceRepository;
    protected final SharedRepository sharedRepository;

    protected LiveData<User> userLiveData;

    protected Callback<AllDto> synchronizeDataCallback(User user, OnFailureAction connectionFailedAction, OnConnectAction successAction, OnConnectAction failureAction) {
        return new Callback<AllDto>() {
            @Override
            public void onResponse(@NonNull Call<AllDto> call, @NonNull Response<AllDto> response) {
                if (response.isSuccessful()) {
                    synchronizeData(user, Optional.ofNullable(response.body()).orElse(AllDto.builder().build()));
                    successAction.action();
                } else {
                    failureAction.action();
                    connectionFailedAction.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AllDto> call, @NonNull Throwable t) {
                connectionFailedAction.action(t);
            }
        };
    }

    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }

    public User getUserValue() throws NoUserFoundException {
        return Optional.ofNullable(userLiveData.getValue()).orElseThrow(() -> new NoUserFoundException("No user is logged"));
    }

    /**
     * Synchronizes data for currently logged user, if user is not logged will throw NoUserFoundException,
     * to avoid this, in a situation call synchronizeData(User user, ...)
     */
    public void synchronizeData() {
        synchronizeData(getUserValue());
    }

    /**
     * Method for synchronization for passed user, and act on success or failure
     *
     * @param user - to synchronize for
     */
    public void synchronizeData(User user) {
        shoppingRepository.getAllDataAndAct(user, (amountTypes, categories, shoppingItems) -> {
            AllDto allDto = collectEntitiyToAllDto(user, amountTypes, categories, shoppingItems);
            shoppingServiceRepository.websocketSynchronize(allDto, user);
        });
    }

    protected AllDto collectEntitiyToAllDto(User user, List<AmountType> amountTypes, List<Category> categories, List<ShoppingItem> shoppingItems) {
        List<AmountTypeDto> amountTypesToSend = amountTypes
                .stream()
                .map(amountType -> {
                    if (amountType.isUpdated()) {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.UPDATE);
                    } else if (amountType.isDeleted()) {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.DELETE);
                    } else if (amountType.getAmountTypeId() == 0) {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.INSERT);
                    } else {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.NONE);
                    }
                })
                .collect(Collectors.toList());

        List<CategoryDto> categoriesToSend = categories
                .stream()
                .map(category -> {
                    if (category.isUpdated()) {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.UPDATE);
                    } else if (category.isDeleted()) {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.DELETE);
                    } else if (category.getCategoryId() == 0) {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.INSERT);
                    } else {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.NONE);
                    }
                })
                .collect(Collectors.toList());

        List<ShoppingItemDto> shoppingItemsToSend = shoppingItems
                .stream()
                .map(shoppingItem -> {
                    if (shoppingItem.isUpdated()) {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.UPDATE);
                    } else if (shoppingItem.isDeleted()) {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.DELETE);
                    } else if (shoppingItem.getShoppingItemId() == 0) {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.INSERT);
                    } else {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.NONE);
                    }
                })
                .collect(Collectors.toList());

        return AllDto.builder()
                .amountTypeDtoList(amountTypesToSend)
                .categoryDtoList(categoriesToSend)
                .shoppingItemDtoList(shoppingItemsToSend)
                .savedTime(user.getSavedTime())
                .build();
    }

    protected void synchronizeData(User user, AllDto responseAllDto) {
        Map<ModifyState, List<AmountType>> amountTypeListFiltered = Optional.ofNullable(responseAllDto.getAmountTypeDtoList()).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(
                        AmountTypeDto::getModifyState,
                        Collectors.mapping(dto -> ServiceUtil.amountTypeDtoToAmountType(user, dto), Collectors.toList())));

        Map<ModifyState, List<Category>> categoryListFiltered = Optional.ofNullable(responseAllDto.getCategoryDtoList()).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(
                        CategoryDto::getModifyState,
                        Collectors.mapping(dto -> ServiceUtil.categoryDtoToCategory(user, dto), Collectors.toList())));

        Map<ModifyState, List<ShoppingItem>> shoppingItemListFiltered = Optional.ofNullable(responseAllDto.getShoppingItemDtoList()).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(
                        ShoppingItemDto::getModifyState,
                        Collectors.mapping(dto -> ServiceUtil.shoppingItemDtoToShoppingItem(user, dto), Collectors.toList())));

        shoppingRepository.synchronizeData(
                amountTypeListFiltered,
                categoryListFiltered,
                shoppingItemListFiltered,
                user,
                responseAllDto.getSavedTime());
    }

    protected String decodeErrorMassage(Response<?> response) {
        return Optional.ofNullable(response.errorBody()).map(responseBody -> {
                    try {
                        return responseBody.string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(s -> !s.isEmpty())
                .orElse(ShoppingServiceRepository.CONNECTION_FAILED_MESSAGE + response.code());
    }

    public void initializeOnMessageAction(User user, OnMessageAction<String> onErrorAction, OnFailureAction failure) {
        shoppingServiceRepository.setOnMessageActionSynchronize((webSocket, allDto) ->
                synchronizeData(user, allDto));

        shoppingServiceRepository.setOnMessageActionPip((webSocket, pip) ->
                shoppingRepository.getAllDataAndAct(user, (amountTypeList, categoryList, shoppingItemList) ->
                        shoppingServiceRepository.websocketSynchronize(collectEntitiyToAllDto(user, amountTypeList, categoryList, shoppingItemList), user)));

        shoppingServiceRepository.setOnErrorAction((webSocket, object) -> new Handler(Looper.getMainLooper()).post(() -> onErrorAction.action(webSocket, object)));

        shoppingServiceRepository.setOnFailureAction((webSocket, t) ->
                failure.action(new WebSocketErrorConnection(t.getMessage())));
    }
}
