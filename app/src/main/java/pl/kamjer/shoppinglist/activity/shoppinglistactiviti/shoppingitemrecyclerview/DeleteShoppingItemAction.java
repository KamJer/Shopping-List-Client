package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@FunctionalInterface
public interface DeleteShoppingItemAction {
    void action(ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory);
}
