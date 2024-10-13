package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;

@Dao
public interface AmountTypeDao {

    @Insert
    Long insertAmountType(AmountType amountType);

    @Transaction
    default void deleteAmountTypeSoft(AmountType amountType) {
        amountType.setDeleted(true);
        updateAmountType(amountType);
    }

    @Transaction
    default void updateAmountTypeFlag(AmountType amountType) {
        if (amountType.getAmountTypeId() != 0) {
            amountType.setUpdated(true);
        }
        updateAmountType(amountType);
    }
    @Update
    void updateAmountType(AmountType amountType);

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE WHERE deleted=0 AND user_name=:userName")
    LiveData<List<AmountType>> findAllAmountType(String userName);

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE WHERE user_name=:userName")
    List<AmountType> findAllAmountTypeForUser(String userName);


}
