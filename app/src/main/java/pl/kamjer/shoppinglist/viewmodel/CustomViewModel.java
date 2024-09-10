package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

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
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
public class CustomViewModel extends ViewModel {

    protected final ShoppingRepository shoppingRepository;
    protected final ShoppingServiceRepository shoppingServiceRepository;
    protected final SharedRepository sharedRepository;

    protected LiveData<User> userLiveData;


    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }

    public User getUserValue() throws NoUserFoundException {
        return Optional.ofNullable(userLiveData.getValue()).orElseThrow(() -> new NoUserFoundException("No user is logged"));
    }

    public void synchronizeData(OnFailureAction connectionFailedAction) {
        shoppingRepository.getAllDataAndAct(getUserValue(), (amountTypes, categories, shoppingItems) -> {
            List<AmountType> amountTypesToSend = amountTypes
                    .stream()
                    .filter(amountType -> amountType.isDeleted() || amountType.isUpdated() || amountType.getAmountTypeId() == 0)
                    .collect(Collectors.toList());
            List<Category> categoriesToSend = categories
                    .stream()
                    .filter(category -> category.isDeleted() || category.isUpdated() || category.getCategoryId() == 0)
                    .collect(Collectors.toList());
            List<ShoppingItem> shoppingItemsToSend = shoppingItems
                    .stream()
                    .filter(shoppingItem -> shoppingItem.isDeleted() || shoppingItem.isUpdated() || shoppingItem.getShoppingItemId() == 0)
                    .collect(Collectors.toList());
            AllDto allDto = AllDto.builder()
                    .amountTypeDtoList(ServiceUtil.amountTypesToAmountTypeDtos(amountTypesToSend))
                    .categoryDtoList(ServiceUtil.categoriesToCategoryDtos(categoriesToSend))
                    .shoppingItemDtoList(ServiceUtil.shoppingItemsToShoppingItemDtos(shoppingItemsToSend))
                    .savedTime(getUserValue().getSavedTime())
                    .build();
            shoppingServiceRepository.synchronizeData(allDto,
                    new Callback<AllDto>() {

                        @Override
                        public void onResponse(@NonNull Call<AllDto> call, @NonNull Response<AllDto> response) {
                            if (response.isSuccessful()) {
                                AllDto responseAllDto = Optional.ofNullable(response.body()).orElse(AllDto.builder().build());

                                Map<ModifyState, List<AmountType>> amountTypeListFiltered = Optional.ofNullable(responseAllDto.getAmountTypeDtoList()).orElse(new ArrayList<>())
                                        .stream()
                                        .collect(Collectors.groupingBy(
                                                AmountTypeDto::getModifyState,
                                                Collectors.mapping(dto -> {
                                                    switch (dto.getModifyState()) {
                                                        case INSERT:
                                                        case UPDATE:
                                                        case DELETE: {
                                                            return ServiceUtil.amountTypeDtoToAmountType(getUserValue(), dto);
                                                        }
                                                        default:
                                                            throw new IllegalArgumentException("Unknown ModifyState: " + dto.getModifyState());

                                                    }
                                                }, Collectors.toList())));

                                Map<ModifyState, List<Category>> categoryListFiltered = Optional.ofNullable(responseAllDto.getCategoryDtoList()).orElse(new ArrayList<>())
                                        .stream()
                                        .collect(Collectors.groupingBy(
                                                CategoryDto::getModifyState,
                                                Collectors.mapping(dto -> {
                                                    switch (dto.getModifyState()) {
                                                        case INSERT:
                                                        case UPDATE:
                                                        case DELETE: {
                                                            return ServiceUtil.categoryDtoToCategory(getUserValue(), dto);
                                                        }
                                                        default:
                                                            throw new IllegalArgumentException("Unknown ModifyState: " + dto.getModifyState());

                                                    }
                                                }, Collectors.toList())));

                                Map<ModifyState, List<ShoppingItem>> shoppingItemListFiltered = Optional.ofNullable(responseAllDto.getShoppingItemDtoList()).orElse(new ArrayList<>())
                                        .stream()
                                        .collect(Collectors.groupingBy(
                                                ShoppingItemDto::getModifyState,
                                                Collectors.mapping(dto -> {
                                                    switch (dto.getModifyState()) {
                                                        case INSERT:
                                                        case UPDATE:
                                                        case DELETE: {
                                                            return ServiceUtil.shoppingItemDtoToShoppingItem(getUserValue(), dto);
                                                        }
                                                        default:
                                                            throw new IllegalArgumentException("Unknown ModifyState: " + dto.getModifyState());

                                                    }
                                                }, Collectors.toList())));

                                shoppingRepository.synchronizeData(amountTypeListFiltered, categoryListFiltered, shoppingItemListFiltered);

                                User user = getUserValue();
                                user.setSavedTime(responseAllDto.getSavedTime());
                                shoppingRepository.updateUser(user);
                            } else {
                                connectionFailedAction.action(new NotOkHttpResponseException(ShoppingServiceRepository.CONNECTION_FAILED_MESSAGE + response.code()));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<AllDto> call, @NonNull Throwable t) {
                            connectionFailedAction.action(t);
                        }
                    });
        });
    }
}
