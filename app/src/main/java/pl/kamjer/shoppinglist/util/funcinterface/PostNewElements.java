package pl.kamjer.shoppinglist.util.funcinterface;

import java.util.List;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;

@FunctionalInterface
public interface PostNewElements {
    void action(List<AmountType> amountTypeList, List<Category> categoryList, List<ShoppingItem> shoppingItemList);
}
