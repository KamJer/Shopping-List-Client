package pl.kamjer.shoppinglist.repository;

import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.database.AmountTypeDao;
import pl.kamjer.shoppinglist.database.CategoryDao;
import pl.kamjer.shoppinglist.database.ShoppingDatabase;
import pl.kamjer.shoppinglist.database.ShoppingItemDao;
import pl.kamjer.shoppinglist.database.UserDao;
import pl.kamjer.shoppinglist.database.UtilDao;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ModifyState;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.util.funcinterface.LoadToServerAction;
import pl.kamjer.shoppinglist.util.funcinterface.PostNewElements;

@RequiredArgsConstructor
public class ShoppingRepository {

    private static ShoppingRepository shoppingRepository;

    private ShoppingItemDao shoppingItemDao;
    private CategoryDao categoryDao;
    private AmountTypeDao amountTypeDao;
    private UtilDao utilDao;
    private UserDao userDao;

    private final MutableLiveData<User> userLiveData;

    @Getter
    @Setter
    private User oldloggedUser;

    public static ShoppingRepository getShoppingRepository() {
        ShoppingRepository result = shoppingRepository;
        if (result != null) {
            return result;
        }
        synchronized (ShoppingRepository.class) {
            if (shoppingRepository == null) {
                shoppingRepository = new ShoppingRepository(new MutableLiveData<>());
            }
            return shoppingRepository;
        }
    }

    //    initializer for a database
    public void initialize(Context appContext) {
        ShoppingDatabase shoppingDatabase = Room.databaseBuilder(appContext,
                        ShoppingDatabase.class, ShoppingDatabase.DATABASE_NAME)
                .build();
        shoppingItemDao = shoppingDatabase.getShoppingItemDao();
        categoryDao = shoppingDatabase.getCategoryDao();
        amountTypeDao = shoppingDatabase.getAmountTypeDao();
        utilDao = shoppingDatabase.getUtilDao();
        userDao = shoppingDatabase.getUserDao();
    }

    public LiveData<User> loadUser(@NonNull String userName) {
//        loading user data from database based on what was passed from caller
        if (userLiveData.getValue() == null || !userName.equals(userLiveData.getValue().getUserName())) {
            LiveData<User> userRoomLifeData = userDao.findUserByUserName(userName);
//        creating observer, if found user is the same as one logged (saved in userLifeData) don't load user from database,
//        if new user was passed set new user to be logged
            Observer<User> userObserver = new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    userLiveData.postValue(user);
                    userRoomLifeData.removeObserver(this);
                }
            };
            userRoomLifeData.observeForever(userObserver);
        }
        return userLiveData;
    }

    public void insertUser(User user) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            userDao.insertUser(user);
            userLiveData.postValue(user);
        });
    }

    public void updateUser(User user) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            userDao.updateUser(user);
        });
    }

    //    ShoppingItem
    public LiveData<List<ShoppingItemWithAmountTypeAndCategory>> loadAllShoppingItemsWithAmountTypeAndCategory(User user) {
        return shoppingItemDao.findAllShoppingItemsWithAmountTypeAndCategory(user.getUserName());
    }

    public void insertShoppingItem(User user, ShoppingItem shoppingItem, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            shoppingItem.setUserName(user.getUserName());
            shoppingItem.setLocalShoppingItemId(shoppingItemDao.insertShoppingItem(shoppingItem));
            action.action();
        });
    }

    public void updateShoppingItemFlag(ShoppingItem shoppingItem, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            shoppingItem.setUpdated(true);
            shoppingItemDao.updateShoppingItem(shoppingItem);
            action.action();
        });
    }

    public void updateShoppingItem(ShoppingItem shoppingItem) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            shoppingItemDao.updateShoppingItem(shoppingItem);
        });
    }

    public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> shoppingItemDao.updateShoppingItems(shoppingItems));
    }

    public void deleteShoppingItemSoftDelete(ShoppingItem shoppingItem, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            shoppingItemDao.deleteShoppingItemSoftDelete(shoppingItem);
            action.action();
        });
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> shoppingItemDao.deleteShoppingItem(shoppingItem));
    }

    //    category
    public LiveData<List<Category>> loadAllCategory(User user) {
        return categoryDao.findAllCategory(user.getUserName());
    }

    public void insertCategory(User user, Category category, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            category.setUserName(user.getUserName());
            category.setLocalCategoryId(categoryDao.insertCategory(category));
            action.action();
        });
    }

    public void deleteCategory(Category category) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            categoryDao.deleteCategory(category);
        });
    }

    public void deleteCategorySoft(Category category, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            categoryDao.deleteCategorySoft(category);
            action.action();
        });
    }

    public void updateCategoryFlag(Category category, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            categoryDao.updateCategoryFlag(category);
            action.action();
        });
    }

    public void updateCategory(Category category) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            categoryDao.updateCategory(category);
        });
    }

    //    amountType
    public LiveData<List<AmountType>> loadAllAmountType(User user) {
        return amountTypeDao.findAllAmountType(user.getUserName());
    }

    public void insertAmountType(User user, AmountType amountType, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            amountType.setUserName(user.getUserName());
            amountType.setLocalAmountTypeId(amountTypeDao.insertAmountType(amountType));
            action.action();
        });
    }

    public void deleteAmountType(AmountType amountType) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            amountTypeDao.deleteAmountType(amountType);
        });
    }

    public void deleteAmountTypeSoft(AmountType amountType, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            amountTypeDao.deleteAmountTypeSoft(amountType);
            action.action();
        });
    }

    public void updateAmountType(AmountType amountType, LoadToServerAction action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            amountTypeDao.updateAmountType(amountType);
            action.action();
        });
    }

    public void updateAmountType(AmountType amountType) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() -> {
            amountTypeDao.updateAmountType(amountType);
        });
    }

    public void getAllDataAndAct(User user, PostNewElements action) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() ->
                action.action(
                        amountTypeDao.findAllAmountTypeForUser(user.getUserName()),
                        categoryDao.findAllCategoryForUser(user.getUserName()),
                        shoppingItemDao.findAllShoppingItemsForUser(user.getUserName())));
    }

    public void synchronizeData(Map<ModifyState, List<AmountType>> amountTypes,
                                Map<ModifyState, List<Category>> categories,
                                Map<ModifyState, List<ShoppingItem>> shoppingItems) {
        ShoppingDatabase.EXECUTOR_SERVICE.execute(() ->
                utilDao.synchronizeData(amountTypes, categories, shoppingItems));
    }
}
