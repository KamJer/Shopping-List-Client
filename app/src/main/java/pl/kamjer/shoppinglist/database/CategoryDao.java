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

import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;

@Dao
public interface CategoryDao {

    @Insert
    Long insertCategory(Category category);

    @Query("SELECT * FROM SHOPPING_ITEM WHERE local_item_category_id=:localCategoryId")
    List<ShoppingItem> findAllShoppingItemsForCategory(long localCategoryId);

    @Update
    void updateShoppingItem(ShoppingItem shoppingItems);

    @Transaction
    default void updateCategoryAndSavedTime(Category category, LocalDateTime savedTime) {
        updateCategory(category);
        updateUsersSavedTime(savedTime, category.getUserName());
    }

    @Query("Update USER SET saved_time = :savedTime WHERE user_name=:userName")
    void updateUsersSavedTime(LocalDateTime savedTime, String userName);

    @Delete
    void deleteCategory(Category category);

    @Transaction
    default void deleteCategoryAndSavedTime(Category category, LocalDateTime savedTime) {
        deleteCategory(category);
        updateUsersSavedTime(savedTime, category.getUserName());
    }

    @Transaction
    default void deleteCategorySoft(Category category) {
        findAllShoppingItemsForCategory(category.getLocalCategoryId()).forEach(shoppingItem -> {
            shoppingItem.setDeleted(true);
            updateShoppingItem(shoppingItem);
        });
        if (category.getCategoryId() != 0) {
            category.setDeleted(true);
            updateCategory(category);
        } else {
            deleteCategory(category);
        }
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
    @Query("SELECT * FROM CATEGORY WHERE user_name=:userName")
    List<Category> findAllCategoryForUserToBeUpdated(String userName);
}
