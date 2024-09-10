package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.Category;

@FunctionalInterface
public interface AddShoppingItemAction {

    void action(Category category);
}
