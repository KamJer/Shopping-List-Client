package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview;

import pl.kamjer.shoppinglist.model.Category;

@FunctionalInterface
public interface RemoveCategoryAction {
    void action(Category category);
}
