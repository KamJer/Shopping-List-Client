package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@Dao
public interface ShoppingItemDao {

    @Update
    void updateAmountType(AmountType amountType);

    @Insert
    Long insertShoppingItem(ShoppingItem item);

    @Update
    void updateShoppingItem(ShoppingItem item);

    @Transaction
    default void updateShoppingItemAndSavedTime(ShoppingItem shoppingItem, LocalDateTime savedTime) {
        updateShoppingItem(shoppingItem);
        updateUsersSavedTime(savedTime, shoppingItem.getUserName());
    }

    @Update
    void updateShoppingItems(List<ShoppingItem> shoppingItems);

    @Delete
    void deleteShoppingItem(ShoppingItem shoppingItem);

    @Transaction
    default void deleteShoppingItemAndSavedTime(ShoppingItem shoppingItem, LocalDateTime savedTime) {
        deleteShoppingItem(shoppingItem);
        updateUsersSavedTime(savedTime, shoppingItem.getUserName());
    }

    @Transaction
    default void deleteShoppingItemSoft(ShoppingItem shoppingItem) {
        shoppingItem.setDeleted(true);
        updateShoppingItem(shoppingItem);
    }

    @Transaction
    default void deleteShoppingItemsSoft(List<ShoppingItem> shoppingItems) {
        shoppingItems.forEach(shoppingItem -> {
            shoppingItem.setDeleted(true);
            updateShoppingItem(shoppingItem);
        });
    }

    @Transaction
    default void updateShoppingItemFlag(ShoppingItem shoppingItem) {
        if (shoppingItem.getShoppingItemId() != 0) {
            shoppingItem.setUpdated(true);
        }
        updateShoppingItem(shoppingItem);
    }

    @Transaction
    default void updateShoppingItemsAmountTypeAndDeleteAmountType(AmountType amountTypeToDelete, AmountType amountTypeToChange) {
        findAllShoppingItemsForUser(amountTypeToDelete.getLocalAmountTypeId()).forEach(shoppingItem -> {
            shoppingItem.setLocalItemAmountTypeId(amountTypeToChange.getLocalAmountTypeId());
            updateShoppingItem(shoppingItem);
        });
        amountTypeToDelete.setDeleted(true);
        updateAmountType(amountTypeToDelete);
    }


    @Transaction
    @Query("SELECT * FROM SHOPPING_ITEM WHERE deleted=0 AND user_name=:userName")
    LiveData<List<ShoppingItemWithAmountTypeAndCategory>> findAllShoppingItemsWithAmountTypeAndCategory(String userName);

    @Query("SELECT * FROM SHOPPING_ITEM WHERE user_name=:userName")
    List<ShoppingItem> findAllShoppingItemsForUser(String userName);

    @Transaction
    @Query("SELECT * FROM SHOPPING_ITEM WHERE deleted=0 AND user_name=:userName AND local_item_amount_type_id=:localAmountTypeId")
    LiveData<List<ShoppingItem>> loadShoppingItemByAmountTypeIdToBeUpdated(String userName, long localAmountTypeId);

    @Query("SELECT * FROM SHOPPING_ITEM WHERE local_item_amount_type_id=:localAmountTypeId")
    List<ShoppingItem> findAllShoppingItemsForUser(Long localAmountTypeId);

    @Query("Update USER SET saved_time = :savedTime WHERE user_name=:userName")
    void updateUsersSavedTime(LocalDateTime savedTime, String userName);
}
