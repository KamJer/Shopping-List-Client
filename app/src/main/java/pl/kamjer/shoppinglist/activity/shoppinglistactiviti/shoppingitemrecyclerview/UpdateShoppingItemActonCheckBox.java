package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@FunctionalInterface
public interface UpdateShoppingItemActonCheckBox {
    void action(boolean isChecked, ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory);
}
