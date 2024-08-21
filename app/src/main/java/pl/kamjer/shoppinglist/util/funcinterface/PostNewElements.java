package pl.kamjer.shoppinglist.util.funcinterface;

import java.util.List;

import pl.kamjer.shoppinglist.database.AmountTypeDao;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;

@FunctionalInterface
public interface PostNewElements {
    void action(List<AmountType> amountTypeList, List<Category> categoryList, List<ShoppingItem> shoppingItemList);
}
