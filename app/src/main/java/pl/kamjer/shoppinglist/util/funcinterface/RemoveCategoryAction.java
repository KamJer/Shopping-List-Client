package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.Category;

@FunctionalInterface
public interface RemoveCategoryAction {
    void action(Category category);
}
