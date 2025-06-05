package pl.kamjer.shoppinglist.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

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
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ModifyState;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.exception.handler.DatabaseAndServiceOperationExceptionHandler;
import pl.kamjer.shoppinglist.util.funcinterface.LoadToServerAction;
import pl.kamjer.shoppinglist.util.funcinterface.PostNewElements;

@RequiredArgsConstructor
@Log
public class ShoppingRepository {

    public static final int NUMBER_OF_THREADS = 10;

    private static ShoppingRepository shoppingRepository;

    private ShoppingItemDao shoppingItemDao;
    private CategoryDao categoryDao;
    private AmountTypeDao amountTypeDao;
    private UtilDao utilDao;
    private UserDao userDao;

    private final MutableLiveData<User> userLiveData;

    private boolean userTest = true;

    @Setter
    @Getter
    private ExecutorService executorService;

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
     * Method for initializing shopping database repository
     *
     * @param appContext                - context of an app
     * @param shoppingServiceRepository - initialized repository for a server, necessary for sending exceptions to the server
     */
    public void initialize(Context appContext, ShoppingServiceRepository shoppingServiceRepository) {
        ShoppingDatabase shoppingDatabase = Room.databaseBuilder(appContext,
                        ShoppingDatabase.class, ShoppingDatabase.DATABASE_NAME)
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

    //
    public void insertUser(User user) {
        executorService.execute(() -> {
            Optional<User> optionalUser = Optional.ofNullable(userDao.findUserByUserNameBlock(user.getUserName()));
            if (!optionalUser.isPresent()) {
                userDao.insertUser(user);
            }
            setLoggedUser(user);
        });
    }

    public void setLoggedUser(User user) {
        userLiveData.postValue(user);
    }

    //    ShoppingItem
    public LiveData<List<ShoppingItemWithAmountTypeAndCategory>> loadAllShoppingItemsWithAmountTypeAndCategory(User user) {
        return shoppingItemDao.findAllShoppingItemsWithAmountTypeAndCategory(user.getUserName());
    }

    public void insertShoppingItem(User user, ShoppingItem shoppingItem, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItem.setUserName(user.getUserName());
            shoppingItem.setLocalShoppingItemId(shoppingItemDao.insertShoppingItem(shoppingItem));
            action.action();
        });
    }

    public void updateShoppingItemFlag(ShoppingItem shoppingItem, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItemDao.updateShoppingItemFlag(shoppingItem);
            action.action();
        });
    }

    public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
        executorService.execute(() -> shoppingItemDao.updateShoppingItems(shoppingItems));
    }

    public void deleteShoppingItemSoft(ShoppingItem shoppingItem, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItemDao.deleteShoppingItemSoft(shoppingItem);
            action.action();
        });
    }

    public void deleteShoppingItemsSoftDeleteAndDeleteAmountType(AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountTypeDao.deleteAmountTypeSoft(amountType);
            action.action();
        });
    }

    public void updateShoppingItemsAmountTypeAndDeleteAmountType(AmountType amountTypeToDelete, AmountType amountTypeToChange, LoadToServerAction action) {
        executorService.execute(() -> {
            shoppingItemDao.updateShoppingItemsAmountTypeAndDeleteAmountType(amountTypeToDelete, amountTypeToChange);
            action.action();
        });
    }

    public void updateShoppingItemFinal(ShoppingItemDto shoppingItemDto, User user) {
        executorService.execute(() ->
                shoppingItemDao.updateShoppingItemAndSavedTime(ServiceUtil.shoppingItemDtoToShoppingItem(user, shoppingItemDto), shoppingItemDto.getSavedTime()));
    }

    public void deleteShoppingItemFinal(ShoppingItemDto shoppingItemDto, User user) {
        executorService.execute(() ->
                shoppingItemDao.deleteShoppingItemAndSavedTime(ServiceUtil.shoppingItemDtoToShoppingItem(user, shoppingItemDto), shoppingItemDto.getSavedTime()));
    }

    //    category
    public LiveData<List<Category>> loadAllCategory(User user) {
        return categoryDao.findAllCategory(user.getUserName());
    }

    public void insertCategory(User user, Category category, LoadToServerAction action) {
        executorService.execute(() -> {
            category.setUserName(user.getUserName());
            category.setLocalCategoryId(categoryDao.insertCategory(category));
            action.action();
        });
    }

    public void deleteCategorySoft(Category category, LoadToServerAction action) {
        executorService.execute(() -> {
            categoryDao.deleteCategorySoft(category);
            action.action();
        });
    }

    public void updateCategoryFlag(Category category, LoadToServerAction action) {
        executorService.execute(() -> {
            categoryDao.updateCategoryFlag(category);
            action.action();
        });
    }

    /**
     * Updates category in database with out setting up a flag
     *
     * @param categoryDto dto of a amount type to updates
     * @param user          - user logged in
     */
    public void updateCategoryFinal(CategoryDto categoryDto, User user) {
        executorService.execute(() ->
                categoryDao.updateCategoryAndSavedTime(ServiceUtil.categoryDtoToCategory(user, categoryDto), categoryDto.getSavedTime()));
    }

    public void deleteCategoryFinal(CategoryDto categoryDto, User user) {
        executorService.execute(() ->
                categoryDao.deleteCategoryAndSavedTime(ServiceUtil.categoryDtoToCategory(user, categoryDto), categoryDto.getSavedTime()));

    }

    //    amountType
    public LiveData<List<AmountType>> loadAllAmountType(User user) {
        return amountTypeDao.findAllAmountType(user.getUserName());
    }

    public void insertAmountType(User user, AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountType.setUserName(user.getUserName());
            amountType.setLocalAmountTypeId(amountTypeDao.insertAmountType(amountType));
            action.action();
        });
    }

    public void deleteAmountTypeSoft(AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountTypeDao.deleteAmountTypeSoft(amountType);
            action.action();
        });
    }

    public void updateAmountTypeSoft(AmountType amountType, LoadToServerAction action) {
        executorService.execute(() -> {
            amountTypeDao.updateAmountTypeFlag(amountType);
            action.action();
        });
    }

    /**
     * Updates amount type in database with out setting up a flag
     *
     * @param amountTypeDto dto of a amount type to updates
     * @param user          - user logged in
     */
    public void updateAmountTypeFinal(AmountTypeDto amountTypeDto, User user) {
        executorService.execute(() ->
                amountTypeDao.updateAmountTypeAndSavedTime(ServiceUtil.amountTypeDtoToAmountType(user, amountTypeDto), amountTypeDto.getSavedTime()));
    }

    /**
     * Deletes amount type in database with out setting up a flag
     *
     * @param amountTypeDto - dto of a amount type to delete
     * @param user          - user logged in
     */
    public void deleteAmountTypeFinal(AmountTypeDto amountTypeDto, User user) {
        executorService.execute(() ->
                amountTypeDao.deleteAmountTypeAndSavedTime(ServiceUtil.amountTypeDtoToAmountType(user, amountTypeDto), amountTypeDto.getSavedTime()));
    }

    //util
    public void getAllDataAndAct(User user, PostNewElements action) {
        executorService.execute(() ->
                action.action(
                        amountTypeDao.findAllAmountTypeForUserToBeUpdated(user.getUserName()),
                        categoryDao.findAllCategoryForUserToBeUpdated(user.getUserName()),
                        shoppingItemDao.findAllShoppingItemsForUser(user.getUserName())));
    }

    public void synchronizeData(Map<ModifyState, List<AmountType>> amountTypes,
                                Map<ModifyState, List<Category>> categories,
                                Map<ModifyState, List<ShoppingItem>> shoppingItems,
                                User user,
                                LocalDateTime savedTime) {
        executorService.execute(() -> utilDao.synchronizeData(amountTypes, categories, shoppingItems, user, savedTime));
    }

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
                responseAllDto.getSavedTime());
    }

    public LiveData<List<User>> loadAllUsers() {
        return userDao.findAllUsers();
    }

    public void deleteUser(User user) {
        executorService.execute(() -> {
            userDao.deleteUser(user);
            setLoggedUser(null);
        });
    }

    public LiveData<List<ShoppingItem>> loadAllShoppingItemsForAmountType(User user, AmountType amountType) {
        return shoppingItemDao.loadShoppingItemByAmountTypeIdToBeUpdated(user.getUserName(), amountType.getLocalAmountTypeId());
    }

    public void updateUserSavedTime(LocalDateTime savedTime, User user) {
      userDao.updateUsersSavedTime(savedTime, user.getUserName());
    };

}
