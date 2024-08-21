package pl.kamjer.shoppinglist.util;

import pl.kamjer.shoppinglist.util.exception.ShoppingItemNameException;

public class NewItemDialogDataValidator {

    public static boolean isShoppingItemNameValid(String shoppingItemName) {
        return !shoppingItemName.isEmpty();
    }

    public static boolean isShoppingItemWeightValid(String shoppingItemWeight) {
        return !shoppingItemWeight.isEmpty();
    }
}
