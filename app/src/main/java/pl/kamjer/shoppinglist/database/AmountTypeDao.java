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
import pl.kamjer.shoppinglist.model.Category;

@Dao
public interface AmountTypeDao {

    @Insert
    Long insertAmountType(AmountType amountType);

    @Delete
    void deleteAmountType(AmountType amountType);

    @Transaction
    default void deleteAmountTypeSoft(AmountType amountType) {
        amountType.setDeleted(true);
        updateAmountType(amountType);
    }

    @Transaction
    default void updateAmountTypeFlag(AmountType amountType) {
        amountType.setUpdated(true);
        updateAmountType(amountType);
    }
    @Update
    void updateAmountType(AmountType amountType);

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE WHERE deleted=0")
    LiveData<List<AmountType>> findAllAmountType();

    @Transaction
    @Insert
    long[] insertAmountTypes(List<AmountType> amountTypes);

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE")
    List<AmountType> findAllAmountTypeForUser();
}
