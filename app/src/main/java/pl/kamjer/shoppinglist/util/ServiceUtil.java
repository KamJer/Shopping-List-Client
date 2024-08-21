package pl.kamjer.shoppinglist.util;

import java.util.List;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
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
                .localAmountTypeId(amountType.getLocalAmountTypeId())
                .deleted(amountType.isDeleted())
                .build();
    }

    public static List<AmountTypeDto> amountTypesToAmountTypeDtos(List<AmountType> amountTypes) {
        return amountTypes.stream().map(ServiceUtil::amountTypeToAmountTypeDto).collect(Collectors.toList());
    }

    public static AmountType amountTypeDtoToAmountType(AmountTypeDto amountType) {
        return AmountType.builder()
                .typeName(amountType.getTypeName())
                .amountTypeId(amountType.getAmountTypeId())
                .localAmountTypeId(amountType.getLocalAmountTypeId())
                .build();
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .localCategoryId(category.getLocalCategoryId())
                .categoryName(category.getCategoryName())
                .deleted(category.isDeleted())
                .build();
    }

    public static List<CategoryDto> categoriesToCategoryDtos(List<Category> categories) {
        return categories.stream().map(ServiceUtil::categoryToCategoryDto).collect(Collectors.toList());
    }

    public static Category categoryDtoToCategory(CategoryDto category) {
        return Category.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .localCategoryId(category.getLocalCategoryId())
                .build();
    }

    public static ShoppingItemDto shoppingItemToShoppingItemDto(ShoppingItem shoppingItem) {
        return ShoppingItemDto.builder()
                .shoppingItemId(shoppingItem.getShoppingItemId())
                .localShoppingItemId(shoppingItem.getLocalShoppingItemId())
                .itemAmountTypeId(shoppingItem.getItemAmountTypeId())
                .localAmountTypeId(shoppingItem.getLocalItemAmountTypeId())
                .itemCategoryId(shoppingItem.getItemCategoryId())
                .localCategoryId(shoppingItem.getLocalItemCategoryId())
                .itemName(shoppingItem.getItemName())
                .amount(shoppingItem.getAmount())
                .bought(shoppingItem.isBought())
                .deleted(shoppingItem.isDeleted())
                .build();
    }

    public static ShoppingItem shoppingItemDtoToShoppingItem(ShoppingItemDto shoppingItem) {
        return ShoppingItem.builder()
                .shoppingItemId(shoppingItem.getShoppingItemId())
                .localShoppingItemId(shoppingItem.getLocalShoppingItemId())
                .localItemAmountTypeId(shoppingItem.getLocalAmountTypeId())
                .itemAmountTypeId(shoppingItem.getItemAmountTypeId())
                .itemCategoryId(shoppingItem.getItemCategoryId())
                .localItemCategoryId(shoppingItem.getLocalCategoryId())
                .itemName(shoppingItem.getItemName())
                .amount(shoppingItem.getAmount())
                .bought(shoppingItem.isBought())
                .build();
    }

    public static List<ShoppingItemDto> shoppingItemsToShoppingItemDtos(List<ShoppingItem> shoppingItems) {
        return shoppingItems.stream().map(ServiceUtil::shoppingItemToShoppingItemDto).collect(Collectors.toList());
    }



}
