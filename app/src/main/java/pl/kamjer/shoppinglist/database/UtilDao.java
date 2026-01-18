package pl.kamjer.shoppinglist.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;

@Dao
public interface UtilDao {

    Lock lock = new ReentrantLock();

    @Transaction
    @Query("SELECT * FROM AMOUNT_TYPE")
    List<AmountType> loadAllAmountType();

    @Transaction
    @Query("SELECT * FROM CATEGORY")
    List<Category> loadAllCategories();

    @Transaction
    @Query("SELECT * FROM SHOPPING_ITEM")
    List<ShoppingItem> loadAllShoppingItems();

    @Insert
    Long insertAmountType(AmountType amountType);

    @Insert
    Long insertCategory(Category category);

    @Insert
    Long insertShoppingItem(ShoppingItem shoppingItem);

    @Delete
    void deleteListAmountType(List<AmountType> amountType);

    @Delete
    void deleteListCategory(List<Category> category);

    @Delete
    void deleteListShoppingItem(List<ShoppingItem> shoppingItem);

    @Update
    void updateShoppingItem(ShoppingItem shoppingItems);

    @Update
    void updateAmountType(AmountType amountTypes);

    @Update
    void updateCategory(Category categories);

    @Update
    void updateUser(User user);

    @Transaction
    default void insertAllElements(List<AmountType> amountTypes, List<Category> categories, List<ShoppingItem> shoppingItems) {
        lock.lock();
        try {
            List<AmountType> amountTypesFromDb = loadAllAmountType();
            List<Category> categoriesFromDb = loadAllCategories();
            List<ShoppingItem> shoppingItemsFromDb = loadAllShoppingItems();

            amountTypes.forEach(amountType -> {
                if (!amountTypesFromDb.stream().map(AmountType::getAmountTypeId).collect(Collectors.toList()).contains(amountType.getAmountTypeId())) {
                    amountType.setLocalAmountTypeId(insertAmountType(amountType));
                    amountTypesFromDb.add(amountType);
                }
            });
            categories.forEach(category -> {
                if (!categoriesFromDb.stream().map(Category::getCategoryId).collect(Collectors.toList()).contains(category.getCategoryId())) {
                    category.setLocalCategoryId(insertCategory(category));
                    categoriesFromDb.add(category);
                }
            });

            shoppingItems
                    .forEach(shoppingItem -> {
                        shoppingItem.setLocalItemAmountTypeId(getLocalAmountTypeIdForShoppingItem(amountTypesFromDb, shoppingItem));
                        shoppingItem.setLocalItemCategoryId(getLocalCategoryIdForShoppingItem(categoriesFromDb, shoppingItem));
                        if (!shoppingItemsFromDb.stream().map(ShoppingItem::getShoppingItemId).collect(Collectors.toList()).contains(shoppingItem.getShoppingItemId())) {
                            shoppingItem.setLocalShoppingItemId(insertShoppingItem(shoppingItem));
                        }
                    });
        } finally {
            lock.unlock();
        }
    }

    @Transaction
    default void deleteAllElements(List<AmountType> amountTypes, List<Category> categories, List<ShoppingItem> shoppingItems) {
        lock.lock();
        try {
            List<AmountType> amountTypesFromDb = loadAllAmountType();
            List<Category> categoriesFromDb = loadAllCategories();
            List<ShoppingItem> shoppingItemsFromDb = loadAllShoppingItems();

            shoppingItems.forEach(shoppingItem -> {
                shoppingItem.setLocalItemAmountTypeId(getLocalAmountTypeIdForShoppingItem(amountTypesFromDb, shoppingItem));
                shoppingItem.setLocalItemCategoryId(getLocalCategoryIdForShoppingItem(categoriesFromDb, shoppingItem));
                shoppingItem.setLocalShoppingItemId(getLocalShoppingItemIdForShoppingItem(shoppingItemsFromDb, shoppingItem));
            });
            deleteListShoppingItem(shoppingItems);
            amountTypes.forEach(amountType ->
                    amountType.setLocalAmountTypeId(getLocalAmountTypeId(amountTypesFromDb, amountType)));
            deleteListAmountType(amountTypes);
            categories.forEach(category ->
                    category.setLocalCategoryId(getLocalCategoryId(categoriesFromDb, category)));
            deleteListCategory(categories);
        } finally {
            lock.unlock();
        }
    }

    @Transaction
    default void deleteAll(User user) {
        deleteAmountTypeByUserName(user.getUserName());
        deleteCategoryByUserName(user.getUserName());
        deleteShoppingItemByUserName(user.getUserName());
    }

    @Transaction
    @Query("DELETE FROM AMOUNT_TYPE WHERE user_name = :userName")
    void deleteAmountTypeByUserName(String userName);

    @Transaction
    @Query("DELETE FROM CATEGORY WHERE user_name = :userName")
    void deleteCategoryByUserName(String userName);

    @Transaction
    @Query("DELETE FROM SHOPPING_ITEM WHERE user_name = :userName")
    void deleteShoppingItemByUserName(String userName);

    @Transaction
    default void updateAllElements(List<AmountType> amountTypes, List<Category> categories, List<ShoppingItem> shoppingItems) {
        lock.lock();
        try {
            List<AmountType> amountTypesFromDb = loadAllAmountType();
            List<Category> categoriesFromDb = loadAllCategories();
            List<ShoppingItem> shoppingItemsFromDb = loadAllShoppingItems();

            amountTypes.forEach(amountType -> {
                if (amountType.getLocalAmountTypeId() == 0L) {
                    amountType.setLocalAmountTypeId(getLocalAmountTypeId(amountTypesFromDb, amountType));
                }
                amountType.setUpdated(false);
                updateAmountType(amountType);
            });
            categories.forEach(category -> {
                if (category.getLocalCategoryId() == 0L) {
                    category.setLocalCategoryId(getLocalCategoryId(categoriesFromDb, category));
                }
                category.setUpdated(false);
                updateCategory(category);
            });

            shoppingItems.forEach(shoppingItem -> {
//            this item necessary has this amountTypes in a database if it does not it need to throw exception
                shoppingItem.setLocalItemAmountTypeId(getLocalAmountTypeIdForShoppingItem(amountTypesFromDb, shoppingItem));
                shoppingItem.setLocalItemCategoryId(getLocalCategoryIdForShoppingItem(categoriesFromDb, shoppingItem));
                if (shoppingItem.getLocalShoppingItemId() == 0L) {
                    shoppingItem.setLocalShoppingItemId(getLocalShoppingItemIdForShoppingItem(shoppingItemsFromDb, shoppingItem));
                }
                shoppingItem.setUpdated(false);
                updateShoppingItem(shoppingItem);
            });
        } finally {
            lock.unlock();
        }
    }

    @Transaction
    default void synchronizeData(Map<ModifyState, List<AmountType>> amountTypes,
                                 Map<ModifyState, List<Category>> categories,
                                 Map<ModifyState, List<ShoppingItem>> shoppingItems,
                                 User user,
                                 LocalDateTime savedTime,
                                 boolean dirty) {
        if (dirty) {
            deleteAll(user);
            insertAllElements(
                    amountTypes.values().stream().flatMap(List::stream).collect(Collectors.toList()),
                    categories.values().stream().flatMap(List::stream).collect(Collectors.toList()),
                    shoppingItems.values().stream().flatMap(List::stream).collect(Collectors.toList()));
        } else {
            insertAllElements(
                    Optional.ofNullable(amountTypes.get(ModifyState.INSERT)).orElseGet(ArrayList::new),
                    Optional.ofNullable(categories.get(ModifyState.INSERT)).orElseGet(ArrayList::new),
                    Optional.ofNullable(shoppingItems.get(ModifyState.INSERT)).orElseGet(ArrayList::new));
            updateAllElements(
                    Optional.ofNullable(amountTypes.get(ModifyState.UPDATE)).orElseGet(ArrayList::new),
                    Optional.ofNullable(categories.get(ModifyState.UPDATE)).orElseGet(ArrayList::new),
                    Optional.ofNullable(shoppingItems.get(ModifyState.UPDATE)).orElseGet(ArrayList::new));
            deleteAllElements(
                    Optional.ofNullable(amountTypes.get(ModifyState.DELETE)).orElseGet(ArrayList::new),
                    Optional.ofNullable(categories.get(ModifyState.DELETE)).orElseGet(ArrayList::new),
                    Optional.ofNullable(shoppingItems.get(ModifyState.DELETE)).orElseGet(ArrayList::new));
            user.setSavedTime(savedTime);
            updateUser(user);
        }
    }

    default Long getLocalAmountTypeIdForShoppingItem(List<AmountType> amountTypes, ShoppingItem shoppingItem) {
        return amountTypes
                .stream()
                .filter(amountType -> shoppingItem.getItemAmountTypeId() == amountType.getAmountTypeId())
                .findFirst()
                .map(AmountType::getLocalAmountTypeId)
                .orElseThrow(() -> new NoUserFoundException("No AmountType found for that id: " + shoppingItem.getItemAmountTypeId()));
    }

    default Long getLocalCategoryIdForShoppingItem(List<Category> category, ShoppingItem shoppingItem) {
        return category
                .stream()
                .filter(amountType -> shoppingItem.getItemCategoryId() == amountType.getCategoryId())
                .findFirst()
                .map(Category::getLocalCategoryId)
                .orElseThrow(() -> new NoUserFoundException("No Category found for that id: " + shoppingItem.getItemCategoryId()));
    }

    default Long getLocalShoppingItemIdForShoppingItem(List<ShoppingItem> shoppingItems, ShoppingItem shoppingItem) {
        return shoppingItems
                .stream()
                .filter(shoppingItemFromDb -> shoppingItemFromDb.getShoppingItemId() == shoppingItem.getShoppingItemId())
                .findFirst()
                .map(ShoppingItem::getLocalShoppingItemId)
                .orElseThrow(() -> new NoUserFoundException("No ShoppingItem found for that id: " + shoppingItem.getShoppingItemId()));
    }

    default Long getLocalAmountTypeId(List<AmountType> amountTypes, AmountType amountType) {
        return amountTypes
                .stream()
                .filter(amountTypeFromDb -> amountTypeFromDb.getAmountTypeId() == amountType.getAmountTypeId())
                .findFirst()
                .map(AmountType::getLocalAmountTypeId)
//                if element does not exist in database it will insert it to it androidx return it local id
                .orElseGet(() -> insertAmountType(amountType));
    }

    default Long getLocalCategoryId(List<Category> categories, Category category) {
        return categories
                .stream()
                .filter(categoryFromDb -> categoryFromDb.getCategoryId() == category.getCategoryId())
                .findFirst()
                .map(Category::getLocalCategoryId)
                .orElse(0L);
    }
}
