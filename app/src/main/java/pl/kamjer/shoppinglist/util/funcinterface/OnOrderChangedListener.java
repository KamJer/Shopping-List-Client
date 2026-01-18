package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.shopping_list.Category;

@FunctionalInterface
public interface OnOrderChangedListener {
    void onOrderChanged(Category category, Category oldCategory);
}
