package pl.kamjer.shoppinglist.util.funcinterface;

import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;

@FunctionalInterface
public interface PostNewElements {
    void action(List<AmountType> amountTypeList, List<Category> categoryList, List<ShoppingItem> shoppingItemList);
}
