package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.ShoppingItem;

@Dao
public interface AmountTypeDao {

    @Insert
    Long insertAmountType(AmountType amountType);

    @Update
    void updateShoppingItem(ShoppingItem shoppingItems);

    @Update
    void updateAmountType(AmountType amountType);

    @Delete
    void deleteAmountType(AmountType amountType);

    @Query("SELECT * FROM SHOPPING_ITEM WHERE local_item_amount_type_id=:localAmountTypeId")
    List<ShoppingItem> findAllShoppingItemsForAmountType(Long localAmountTypeId);

    @Transaction
    default void deleteAmountTypeSoft(AmountType amountType) {
        findAllShoppingItemsForAmountType(amountType.getLocalAmountTypeId()).forEach(shoppingItem -> {
            shoppingItem.setDeleted(true);
            updateShoppingItem(shoppingItem);
        });
//        if amount type is not on a server database (amount type id is 0) delete it hard
        if (amountType.getAmountTypeId() != 0) {
            amountType.setDeleted(true);
            updateAmountType(amountType);
        } else {
            deleteAmountType(amountType);
        }
    }

    @Transaction
    default void updateAmountTypeFlag(AmountType amountType) {
        if (amountType.getAmountTypeId() != 0) {
            amountType.setUpdated(true);
        }
        updateAmountType(amountType);
    }

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE WHERE deleted=0 AND user_name=:userName")
    LiveData<List<AmountType>> findAllAmountType(String userName);

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE WHERE user_name=:userName")
    List<AmountType> findAllAmountTypeForUserToBeUpdated(String userName);


}
