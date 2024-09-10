package pl.kamjer.shoppinglist.util;

import java.util.List;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
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

    public static AmountTypeDto amountTypeToAmountTypeDto(AmountType amountType) {
        return AmountTypeDto.builder()
                .typeName(amountType.getTypeName())
                .amountTypeId(amountType.getAmountTypeId())
                .deleted(amountType.isDeleted())
                .localId(amountType.getLocalAmountTypeId())
                .build();
    }

    public static List<AmountTypeDto> amountTypesToAmountTypeDtos(List<AmountType> amountTypes) {
        return amountTypes.stream().map(ServiceUtil::amountTypeToAmountTypeDto).collect(Collectors.toList());
    }

    public static AmountType amountTypeDtoToAmountType(User user, AmountTypeDto amountType) {
        return AmountType.builder()
                .typeName(amountType.getTypeName())
                .amountTypeId(amountType.getAmountTypeId())
                .userName(user.getUserName())
                .localAmountTypeId(amountType.getLocalId())
                .build();
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .deleted(category.isDeleted())
                .localId(category.getLocalCategoryId())
                .build();
    }

    public static List<CategoryDto> categoriesToCategoryDtos(List<Category> categories) {
        return categories.stream().map(ServiceUtil::categoryToCategoryDto).collect(Collectors.toList());
    }

    public static Category categoryDtoToCategory(User user, CategoryDto category) {
        return Category.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .userName(user.getUserName())
                .localCategoryId(category.getLocalId())
                .build();
    }

    public static ShoppingItemDto shoppingItemToShoppingItemDto(ShoppingItem shoppingItem) {
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

    public static List<ShoppingItemDto> shoppingItemsToShoppingItemDtos(List<ShoppingItem> shoppingItems) {
        return shoppingItems.stream().map(ServiceUtil::shoppingItemToShoppingItemDto).collect(Collectors.toList());
    }

    public static ExceptionDto toExceptionDto(Throwable e) {
        return ExceptionDto.builder().massage(e.getMessage()).stackTrace(e.getStackTrace()).build();
    }
}
