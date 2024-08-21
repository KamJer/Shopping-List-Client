package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview;

import pl.kamjer.shoppinglist.model.Category;

@FunctionalInterface
public interface AddShoppingItemAction {

    void action(Category category);
}
