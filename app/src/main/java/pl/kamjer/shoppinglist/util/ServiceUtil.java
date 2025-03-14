package pl.kamjer.shoppinglist.util;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ModifyState;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.User;
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
                .localShoppingItemId(shoppingItem.getLocalId())
                .localItemAmountTypeId(shoppingItem.getLocalAmountTypeId())
                .localItemCategoryId(shoppingItem.getLocalCategoryId())
                .build();
    }

    public static ExceptionDto toExceptionDto(Throwable e) {
        return ExceptionDto.builder().massage(e.getMessage()).stackTrace(e.getStackTrace()).build();
    }
}
