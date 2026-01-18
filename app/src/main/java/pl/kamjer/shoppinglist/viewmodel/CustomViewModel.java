package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
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

    /**
     * Synchronizes data for currently logged user, if user is not logged will throw NoUserFoundException,
     * to avoid this, in a situation call synchronizeData(User user)
     */
    public void synchronizeData() {
        synchronizeData(getUserValue());
    }

    /**
     * Method for synchronization for passed user
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
//        Map<ModifyState, List<AmountType>> amountTypeListFiltered = Optional.ofNullable(responseAllDto.getAmountTypeDtoList()).orElse(new ArrayList<>())
//                .stream()
//                .collect(Collectors.groupingBy(
//                        AmountTypeDto::getModifyState,
//                        Collectors.mapping(dto -> ServiceUtil.amountTypeDtoToAmountType(user, dto), Collectors.toList())));
//
//        Map<ModifyState, List<Category>> categoryListFiltered = Optional.ofNullable(responseAllDto.getCategoryDtoList()).orElse(new ArrayList<>())
//                .stream()
//                .collect(Collectors.groupingBy(
//                        CategoryDto::getModifyState,
//                        Collectors.mapping(dto -> ServiceUtil.categoryDtoToCategory(user, dto), Collectors.toList())));
//
//        Map<ModifyState, List<ShoppingItem>> shoppingItemListFiltered = Optional.ofNullable(responseAllDto.getShoppingItemDtoList()).orElse(new ArrayList<>())
//                .stream()
//                .collect(Collectors.groupingBy(
//                        ShoppingItemDto::getModifyState,
//                        Collectors.mapping(dto -> ServiceUtil.shoppingItemDtoToShoppingItem(user, dto), Collectors.toList())));

        shoppingRepository.synchronizeData(
//                amountTypeListFiltered,
//                categoryListFiltered,
//                shoppingItemListFiltered,
                user,
                responseAllDto);
//                responseAllDto.getSavedTime(),
//                responseAllDto.getDirty());
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


}
