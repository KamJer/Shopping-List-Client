package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kamjer.shoppinglist.model.Category;

@Dao
public interface CategoryDao {

    @Insert
    Long insertCategory(Category category);
    @Transaction
    default void deleteCategorySoft(Category category) {
        category.setDeleted(true);
        updateCategory(category);
    }

    @Transaction
    default void updateCategoryFlag(Category category) {
        if (category.getCategoryId() != 0) {
            category.setUpdated(true);
        }
        updateCategory(category);
    }

    @Update
    void updateCategory(Category category);

    @Transaction
    @Query("SELECT * FROM CATEGORY WHERE deleted=0 AND user_name=:userName")
    LiveData<List<Category>> findAllCategory(String userName);

    @Transaction
    @Insert
    long[] insertCategories(List<Category> categories);

    @Transaction
    @Query("SELECT * FROM CATEGORY WHERE user_name=:userName")
    List<Category> findAllCategoryForUserToBeUpdated(String userName);
}
