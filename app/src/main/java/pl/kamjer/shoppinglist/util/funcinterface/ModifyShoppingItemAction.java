package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;

@FunctionalInterface
public interface ModifyShoppingItemAction {
    void action(ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory);
}
