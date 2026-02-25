package pl.kamjer.shoppinglist.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.database.AmountTypeDao;
import pl.kamjer.shoppinglist.database.CategoryDao;
import pl.kamjer.shoppinglist.database.ShoppingDatabase;
import pl.kamjer.shoppinglist.database.ShoppingItemDao;
import pl.kamjer.shoppinglist.database.UserDao;
import pl.kamjer.shoppinglist.database.UtilDao;
import pl.kamjer.shoppinglist.database.threadfactory.ShoppingListDataBaseThreadFactory;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.exception.handler.DatabaseAndServiceOperationExceptionHandler;
import pl.kamjer.shoppinglist.util.funcinterface.LoadToServerAction;
import pl.kamjer.shoppinglist.util.funcinterface.PostNewElements;
import pl.kamjer.shoppinglist.util.sqlCipher.SqlCipherKeyManager;

/**
 * Repository class for managing shopping list data persistence and synchronization.
 * This class handles database operations for shopping items, categories, amount types, and users.
 * It uses Room database with SQLCipher encryption for secure data storage.
 */
@RequiredArgsConstructor
@Log
public class ShoppingRepository {

    /**
     * Number of threads to use for database operations.
     */
    public static final int NUMBER_OF_THREADS = 10;

    /**
     * Singleton instance of ShoppingRepository.
     */
    private static ShoppingRepository shoppingRepository;

    /**
     * DAO for shopping items.
     */
    private ShoppingItemDao shoppingItemDao;

    /**
     * DAO for categories.
     */
    private CategoryDao categoryDao;

    /**
     * DAO for amount types.
     */
    private AmountTypeDao amountTypeDao;

    /**
     * DAO for utility operations.
     */
    private UtilDao utilDao;

    /**
     * DAO for users.
     */
    private UserDao userDao;

    /**
     * LiveData for tracking the currently logged-in user.
     */
    private final MutableLiveData<User> userLiveData;

    /**
     * Flag for testing user loading.
     */
    private boolean userTest = true;

    /**
     * Executor service for background database operations.
     */
    @Setter
    @Getter
    private ExecutorService executorService;

    /**
     * Gets the singleton instance of ShoppingRepository.
     *
     * @return The singleton instance
     */
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

    /**
     * Initializes the shopping database repository.
     *
     * @param appContext                - context of an app
     * @param shoppingServiceRepository - initialized repository for a server, necessary for sending exceptions to the server
     * @throws NoSuchAlgorithmException           if the algorithm is not available
     * @throws NoSuchProviderException            if the provider is not available
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are invalid
     */
    public void initialize(Context appContext, ShoppingServiceRepository shoppingServiceRepository) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        System.loadLibrary("sqlcipher");

        SqlCipherKeyManager sqlCipherKeyManager = new SqlCipherKeyManager(SharedRepository.getSharedRepository().getSharedPref());

        ShoppingDatabase shoppingDatabase = Room.databaseBuilder(appContext,
                        ShoppingDatabase.class,
                        ShoppingDatabase.DATABASE_NAME)
                .addMigrations(ShoppingDatabase.MIGRATION_1_2)
                .openHelperFactory(sqlCipherKeyManager.getSupportFactory())
                .build();
        shoppingItemDao = shoppingDatabase.getShoppingItemDao();
        categoryDao = shoppingDatabase.getCategoryDao();
        amountTypeDao = shoppingDatabase.getAmountTypeDao();
        utilDao = shoppingDatabase.getUtilDao();
        userDao = shoppingDatabase.getUserDao();
        DatabaseAndServiceOperationExceptionHandler handler = new DatabaseAndServiceOperationExceptionHandler(appContext, shoppingServiceRepository);
        executorService = Executors.newFixedThreadPool(
                NUMBER_OF_THREADS,
                new ShoppingListDataBaseThreadFactory(handler));
    }

    /**
     * Loads user data from database based on username.
     *
     * @param userName The username to search for
     * @return LiveData containing the user data
     */
    public LiveData<User> loadUser(@NonNull String userName) {
//        loading user data from database based on what was passed from caller
        if (userLiveData.getValue() == null || !userName.equals(userLiveData.getValue().getUserName())) {
            LiveData<User> userRoomLifeData = userDao.findUserByUserName(userName);
//        creating observer, if found user is the same as one logged (saved in userLifeData) don't load user from database,
//        if new user was passed set new user to be logged
            Observer<User> userObserver = new Observer<>() {
                @Override
                public void onChanged(User user) {
                    if (user != null || userLiveData.getValue() != null) {
                        userTest = true;
                    }
                    if (userTest) {
                        setLoggedUser(user);
                        userTest = false;
                    }
                    userRoomLifeData.removeObserver(this);
                }
            };
            userRoomLifeData.observeForever(userObserver);
        }
        return userLiveData;
    }

    /**
     * Inserts a user into the database.
     *
     * @param user The user to insert
     */
    public void insertUser(User user) {
        executorService.execute(() -> {
            Optional<User> optionalUser = Optional.ofNullable(userDao.findUserByUserNameBlock(user.getUserName()));
            if (!optionalUser.isPresent()) {
                userDao.insertUser(user);
            }
            setLoggedUser(user);
        });
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user The user to set as logged in
     */
    public void setLoggedUser(User user) {
        userLiveData.postValue(user);
    }

    /**
     * Loads all shopping items with their associated amount types and categories.
     *
     * @param user The user for whom to load items
     * @return LiveData containing the shopping items with related data
     */
    public LiveData<List<ShoppingItemWithAmountTypeAndCategory>> loadAllShoppingItemsWithAmountTypeAndCategory(User user) {
        return shoppingItemDao.findAllShoppingItemsWithAmountTypeAndCategory(user.getUserName());
    }

    public LiveData<List<ShoppingItem>> loadAllShoppingItemForUser(User user) {
        return shoppingItemDao.findAllShoppingItemsForUserLiveData(user.getUserName());
    }

    /**
     * Inserts a shopping item into the database.
     *
     * @param user   The user who owns the item
     * @param shoppingItem The shopping item to insert
     * @param action The action to perform after insertion
     */
    public void insertShoppingItem(User user, ShoppingItem shoppingItem, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItem.setUserName(user.getUserName());
            shoppingItem.setLocalShoppingItemId(shoppingItemDao.insertShoppingItem(shoppingItem));
            action.action();
        });
    }

    /**
     * Updates the flag of a shopping item.
     *
     * @param shoppingItem The shopping item to update
     * @param action       The action to perform after update
     */
    public void updateShoppingItemFlag(ShoppingItem shoppingItem, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItemDao.updateShoppingItemFlag(shoppingItem);
            action.action();
        });
    }

    /**
     * Updates multiple shopping items.
     *
     * @param shoppingItems The list of shopping items to update
     */
    public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
        executorService.execute(() -> shoppingItemDao.updateShoppingItems(shoppingItems));
    }

    /**
     * Soft deletes a shopping item.
     *
     * @param shoppingItem The shopping item to delete
     * @param action       The action to perform after deletion
     */
    public void deleteShoppingItemSoft(ShoppingItem shoppingItem, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItemDao.deleteShoppingItemSoft(shoppingItem);
            action.action();
        });
    }

    /**
     * Deletes an amount type and related shopping items.
     *
     * @param amountType The amount type to delete
     * @param action     The action to perform after deletion
     */
    public void deleteShoppingItemsSoftDeleteAndDeleteAmountType(AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountTypeDao.deleteAmountTypeSoft(amountType);
            action.action();
        });
    }

    /**
     * Updates shopping items' amount type and deletes the old amount type.
     *
     * @param amountTypeToDelete The amount type to delete
     * @param amountTypeToChange The amount type to change to
     * @param action             The action to perform after update
     */
    public void updateShoppingItemsAmountTypeAndDeleteAmountType(AmountType amountTypeToDelete, AmountType amountTypeToChange, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItemDao.updateShoppingItemsAmountTypeAndDeleteAmountType(amountTypeToDelete, amountTypeToChange);
            action.action();
        });
    }

    /**
     * Updates a shopping item from a DTO.
     *
     * @param shoppingItemDto The DTO containing the updated item data
     * @param user            The user who owns the item
     */
    public void updateShoppingItemFinal(ShoppingItemDto shoppingItemDto, User user) {
        executorService.execute(() ->
                shoppingItemDao.updateShoppingItemAndSavedTime(ServiceUtil.shoppingItemDtoToShoppingItem(user, shoppingItemDto), shoppingItemDto.getSavedTime()));
    }

    /**
     * Deletes a shopping item from a DTO.
     *
     * @param shoppingItemDto The DTO containing the item data to delete
     * @param user            The user who owns the item
     */
    public void deleteShoppingItemFinal(ShoppingItemDto shoppingItemDto, User user) {
        executorService.execute(() ->
                shoppingItemDao.deleteShoppingItemAndSavedTime(ServiceUtil.shoppingItemDtoToShoppingItem(user, shoppingItemDto), shoppingItemDto.getSavedTime()));
    }

    //    category
    /**
     * Loads all categories for a user.
     *
     * @param user The user for whom to load categories
     * @return LiveData containing the categories
     */
    public LiveData<List<Category>> loadAllCategory(User user) {
        return categoryDao.findAllCategory(user.getUserName());
    }

    /**
     * Inserts a category into the database.
     *
     * @param user   The user who owns the category
     * @param category The category to insert
     * @param action The action to perform after insertion
     */
    public void insertCategory(User user, Category category, LoadToServerAction action) {
        executorService.execute(() -> {
            category.setUserName(user.getUserName());
            category.setLocalCategoryId(categoryDao.insertCategory(category));
            action.action();
        });
    }

    /**
     * Soft deletes a category.
     *
     * @param category The category to delete
     * @param action   The action to perform after deletion
     */
    public void deleteCategorySoft(Category category, LoadToServerAction action) {
        executorService.execute(() -> {
            categoryDao.deleteCategorySoft(category);
            action.action();
        });
    }

    /**
     * Updates the flag of a category.
     *
     * @param category The category to update
     * @param action   The action to perform after update
     */
    public void updateCategoryFlag(Category category, LoadToServerAction action) {
        executorService.execute(() -> {
            categoryDao.updateCategoryFlag(category);
            action.action();
        });
    }

    /**
     * Updates a category in the database without setting a flag but updating saved time.
     *
     * @param category The category to update
     */
    public void updateCategoryLocal(Category category) {
        executorService.execute(() -> categoryDao.updateCategory(category));
    }

    /**
     * Updates a category from a DTO.
     *
     * @param categoryDto The DTO containing the updated category data
     * @param user        The user who owns the category
     */
    public void updateCategoryFinal(CategoryDto categoryDto, User user) {
        executorService.execute(() ->
                categoryDao.updateCategoryAndSavedTime(ServiceUtil.categoryDtoToCategory(user, categoryDto), categoryDto.getSavedTime()));
    }

    /**
     * Deletes a category from a DTO.
     *
     * @param categoryDto The DTO containing the category data to delete
     * @param user        The user who owns the category
     */
    public void deleteCategoryFinal(CategoryDto categoryDto, User user) {
        executorService.execute(() ->
                categoryDao.deleteCategoryAndSavedTime(ServiceUtil.categoryDtoToCategory(user, categoryDto), categoryDto.getSavedTime()));

    }

    //    amountType
    /**
     * Loads all amount types for a user.
     *
     * @param user The user for whom to load amount types
     * @return LiveData containing the amount types
     */
    public LiveData<List<AmountType>> loadAllAmountType(User user) {
        return amountTypeDao.findAllAmountType(user.getUserName());
    }

    /**
     * Inserts an amount type into the database.
     *
     * @param user      The user who owns the amount type
     * @param amountType The amount type to insert
     * @param action     The action to perform after insertion
     */
    public void insertAmountType(User user, AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountType.setUserName(user.getUserName());
            amountType.setLocalAmountTypeId(amountTypeDao.insertAmountType(amountType));
            action.action();
        });
    }

    /**
     * Soft deletes an amount type.
     *
     * @param amountType The amount type to delete
     * @param action     The action to perform after deletion
     */
    public void deleteAmountTypeSoft(AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountTypeDao.deleteAmountTypeSoft(amountType);
            action.action();
        });
    }

    /**
     * Updates an amount type flag.
     *
     * @param amountType The amount type to update
     * @param action     The action to perform after update
     */
    public void updateAmountTypeSoft(AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountTypeDao.updateAmountTypeFlag(amountType);
            action.action();
        });
    }

    /**
     * Updates an amount type from a DTO without setting a flag.
     *
     * @param amountTypeDto The DTO containing the updated amount type data
     * @param user          The user who owns the amount type
     */
    public void updateAmountTypeFinal(AmountTypeDto amountTypeDto, User user) {
        executorService.execute(() ->
                amountTypeDao.updateAmountTypeAndSavedTime(ServiceUtil.amountTypeDtoToAmountType(user, amountTypeDto), amountTypeDto.getSavedTime()));
    }

    /**
     * Deletes an amount type from a DTO without setting a flag.
     *
     * @param amountTypeDto The DTO containing the amount type data to delete
     * @param user          The user who owns the amount type
     */
    public void deleteAmountTypeFinal(AmountTypeDto amountTypeDto, User user) {
        executorService.execute(() ->
                amountTypeDao.deleteAmountTypeAndSavedTime(ServiceUtil.amountTypeDtoToAmountType(user, amountTypeDto), amountTypeDto.getSavedTime()));
    }

    //util
    /**
     * Gets all data and performs an action with it.
     *
     * @param user   The user for whom to get data
     * @param action The action to perform with the data
     */
    public void getAllDataAndAct(User user, PostNewElements action) {
        executorService.execute(() ->
                action.action(
                        amountTypeDao.findAllAmountTypeForUserToBeUpdated(user.getUserName()),
                        categoryDao.findAllCategoryForUserToBeUpdated(user.getUserName()),
                        shoppingItemDao.findAllShoppingItemsForUser(user.getUserName())));
    }

    /**
     * Synchronizes data with the database.
     *
     * @param amountTypes The amount types to synchronize
     * @param categories  The categories to synchronize
     * @param shoppingItems The shopping items to synchronize
     * @param user        The user for whom to synchronize
     * @param savedTime   The saved time of the data
     * @param dirty       Whether the data is dirty
     */
    public void synchronizeData(Map<ModifyState, List<AmountType>> amountTypes,
                                Map<ModifyState, List<Category>> categories,
                                Map<ModifyState, List<ShoppingItem>> shoppingItems,
                                User user,
                                LocalDateTime savedTime,
                                boolean dirty) {
        executorService.execute(() -> utilDao.synchronizeData(amountTypes, categories, shoppingItems, user, savedTime, dirty));
    }

    /**
     * Synchronizes data from a DTO response.
     *
     * @param user   The user for whom to synchronize
     * @param responseAllDto The DTO containing the synchronization data
     */
    public void synchronizeData(User user, AllDto responseAllDto) {
        Map<ModifyState, List<AmountType>> amountTypeListFiltered = Optional.ofNullable(responseAllDto.getAmountTypeDtoList()).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(
                        AmountTypeDto::getModifyState,
                        Collectors.mapping(dto -> ServiceUtil.amountTypeDtoToAmountType(user, dto), Collectors.toList())));

        Map<ModifyState, List<Category>> categoryListFiltered = Optional.ofNullable(responseAllDto.getCategoryDtoList()).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(
                        CategoryDto::getModifyState,
                        Collectors.mapping(dto -> ServiceUtil.categoryDtoToCategory(user, dto), Collectors.toList())));

        Map<ModifyState, List<ShoppingItem>> shoppingItemListFiltered = Optional.ofNullable(responseAllDto.getShoppingItemDtoList()).orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(
                        ShoppingItemDto::getModifyState,
                        Collectors.mapping(dto -> ServiceUtil.shoppingItemDtoToShoppingItem(user, dto), Collectors.toList())));

        synchronizeData(
                amountTypeListFiltered,
                categoryListFiltered,
                shoppingItemListFiltered,
                user,
                responseAllDto.getSavedTime(),
                responseAllDto.getDirty());
    }

    /**
     * Loads all users from the database.
     *
     * @return LiveData containing all users
     */
    public LiveData<List<User>> loadAllUsers() {
        return userDao.findAllUsers();
    }

    /**
     * Deletes a user from the database.
     *
     * @param user The user to delete
     */
    public void deleteUser(User user) {
        executorService.execute(() -> {
            userDao.deleteUser(user);
            setLoggedUser(null);
        });
    }

    /**
     * Loads shopping items for a specific amount type.
     *
     * @param user      The user for whom to load items
     * @param amountType The amount type to filter by
     * @return LiveData containing the shopping items
     */
    public LiveData<List<ShoppingItem>> loadAllShoppingItemsForAmountType(User user, AmountType amountType) {
        return shoppingItemDao.loadShoppingItemByAmountTypeIdToBeUpdated(user.getUserName(), amountType.getLocalAmountTypeId());
    }

    /**
     * Loads bought shopping items for a user.
     *
     * @param user The user for whom to load items
     * @return LiveData containing the bought shopping items
     */
    public LiveData<List<ShoppingItem>> loadBoughtShoppingItem(User user) {
        return shoppingItemDao.findBoughtShoppingItems(user.getUserName());
    }
}
