package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@FunctionalInterface
public interface ModifyShoppingItemAction {
    void action(ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory);
}
