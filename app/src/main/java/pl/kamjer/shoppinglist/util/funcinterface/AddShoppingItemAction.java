package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.shopping_list.Category;

@FunctionalInterface
public interface AddShoppingItemAction {

    void action(Category category);
}
