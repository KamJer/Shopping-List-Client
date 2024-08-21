package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@Dao
public interface ShoppingItemDao {

    @Insert
    Long insertShoppingItem(ShoppingItem item);

    @Update
    void updateShoppingItem(ShoppingItem item);

    @Update
    void updateShoppingItems(List<ShoppingItem> shoppingItems);

    @Delete
    void deleteShoppingItem(ShoppingItem item);

    @Transaction
    default void deleteShoppingItemSoftDelete(ShoppingItem shoppingItem) {
        shoppingItem.setDeleted(true);
        updateShoppingItem(shoppingItem);
    }

    @Transaction
    default void updateShoppingItemFlag(ShoppingItem shoppingItem) {
        shoppingItem.setUpdated(true);
        updateShoppingItem(shoppingItem);
    }

    @Transaction
    @Query("SELECT * FROM SHOPPING_ITEM WHERE deleted=0")
    LiveData<List<ShoppingItemWithAmountTypeAndCategory>> findAllShoppingItemsWithAmountTypeAndCategory();

    @Query("SELECT * FROM SHOPPING_ITEM")
    List<ShoppingItem> findAllShoppingItemsForUser();
}
