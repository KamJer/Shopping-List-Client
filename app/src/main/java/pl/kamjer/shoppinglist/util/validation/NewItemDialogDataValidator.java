package pl.kamjer.shoppinglist.util.validation;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;

public class NewItemDialogDataValidator {

    public static boolean isShoppingItemNameValid(String shoppingItemName) {
        return !shoppingItemName.isEmpty();
    }

    public static boolean isShoppingItemAmountValid(String shoppingItemAmount) {
        return !shoppingItemAmount.isEmpty();
    }

    public static boolean isShoppingItemAmountTypeValid(AmountType amountType) {
        return amountType != null;
    }

    public static boolean isShoppingItemCategoryValid(Category category) {
        return category != null;
    }
}
