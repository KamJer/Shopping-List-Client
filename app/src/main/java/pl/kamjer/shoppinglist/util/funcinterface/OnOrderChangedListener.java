package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.Category;

@FunctionalInterface
public interface OnOrderChangedListener {
    void onOrderChanged(Category category, Category oldCategory);
}
