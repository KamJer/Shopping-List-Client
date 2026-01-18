package pl.kamjer.shoppinglist.util;

import java.util.List;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ExceptionDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import pl.kamjer.shoppinglist.model.dto.UserDto;

public class ServiceUtil {

    public static UserDto userToUserDto(User user) {
        return UserDto.builder()
                .userName(user.getUserName())
                .password(user.getPassword())
                .build();
    }

    public static AmountTypeDto amountTypeToAmountTypeDto(AmountType amountType, ModifyState modifyState) {
        return AmountTypeDto.builder()
                .typeName(amountType.getTypeName())
                .amountTypeId(amountType.getAmountTypeId())
                .deleted(amountType.isDeleted())
                .localId(amountType.getLocalAmountTypeId())
                .modifyState(modifyState)
                .build();
    }

    public static AmountType amountTypeDtoToAmountType(User user, AmountTypeDto amountType) {
        return AmountType.builder()
                .typeName(amountType.getTypeName())
                .amountTypeId(amountType.getAmountTypeId())
                .userName(user.getUserName())
                .localAmountTypeId(amountType.getLocalId())
                .deleted(amountType.isDeleted())
                .build();
    }

    public static CategoryDto categoryToCategoryDto(Category category, ModifyState modifyState) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .deleted(category.isDeleted())
                .localId(category.getLocalCategoryId())
                .modifyState(modifyState)
                .build();
    }

    public static Category categoryDtoToCategory(User user, CategoryDto category) {
        return Category.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .userName(user.getUserName())
                .localCategoryId(category.getLocalId())
                .deleted(category.isDeleted())
                .build();
    }

    public static ShoppingItemDto shoppingItemToShoppingItemDto(ShoppingItem shoppingItem, ModifyState modifyState) {
        return ShoppingItemDto.builder()
                .shoppingItemId(shoppingItem.getShoppingItemId())
                .itemAmountTypeId(shoppingItem.getItemAmountTypeId())
                .itemCategoryId(shoppingItem.getItemCategoryId())
                .itemName(shoppingItem.getItemName())
                .amount(shoppingItem.getAmount())
                .bought(shoppingItem.isBought())
                .deleted(shoppingItem.isDeleted())
                .localId(shoppingItem.getLocalShoppingItemId())
                .localAmountTypeId(shoppingItem.getLocalItemAmountTypeId())
                .localCategoryId(shoppingItem.getLocalItemCategoryId())
                .modifyState(modifyState)
                .build();
    }

    public static ShoppingItem shoppingItemDtoToShoppingItem(User user, ShoppingItemDto shoppingItem) {
        return ShoppingItem.builder()
                .shoppingItemId(shoppingItem.getShoppingItemId())
                .itemAmountTypeId(shoppingItem.getItemAmountTypeId())
                .itemCategoryId(shoppingItem.getItemCategoryId())
                .itemName(shoppingItem.getItemName())
                .amount(shoppingItem.getAmount())
                .bought(shoppingItem.isBought())
                .userName(user.getUserName())
                .deleted(shoppingItem.isDeleted())
                .localShoppingItemId(shoppingItem.getLocalId())
                .localItemAmountTypeId(shoppingItem.getLocalAmountTypeId())
                .localItemCategoryId(shoppingItem.getLocalCategoryId())
                .build();
    }

    public static ExceptionDto toExceptionDto(Throwable e) {
        return ExceptionDto.builder().massage(e.getMessage()).stackTrace(e.getStackTrace()).build();
    }

    public static AllDto collectEntitiyToAllDto(User user, List<AmountType> amountTypes, List<Category> categories, List<ShoppingItem> shoppingItems) {
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
}
