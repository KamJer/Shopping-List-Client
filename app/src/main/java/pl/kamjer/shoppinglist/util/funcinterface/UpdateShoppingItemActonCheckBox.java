package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@FunctionalInterface
public interface UpdateShoppingItemActonCheckBox {
    void action(boolean isChecked, ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory);
}
