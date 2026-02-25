package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

/**
 * ViewModel for managing the shopping list functionality.
 * Handles loading, updating, and deleting shopping items and categories,
 * as well as synchronization with the server and tutorial management.
 */
@Getter
@Log
public class ShoppingListViewModel extends CustomViewModel {

    /**
     * Constant message for connection failure errors.
     */
    private static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

    /**
     * LiveData containing all shopping items with their associated amount types and categories.
     * Used to observe changes in the shopping items data.
     */
    private LiveData<List<ShoppingItemWithAmountTypeAndCategory>> allShoppingItemWithAmountTypeAndCategoryLiveData;

    /**
     * LiveData containing all categories for the current user.
     * Used to observe changes in the categories data.
     */
    private LiveData<List<Category>> allCategoryLiveData;

    /**
     * Constructor for ShoppingListViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public ShoppingListViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of ShoppingListViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<ShoppingListViewModel> initializer = new ViewModelInitializer<>(ShoppingListViewModel.class, creationExtras ->
            new ShoppingListViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    /**
     * Initializes the ViewModel by loading user data, categories, and shopping items.
     * This method should be called when the ViewModel is first created.
     */
    public void initialize() {
        loadUser();
        loadAllCategory();
        loadAllShoppingItemWithAmountTypeAndCategory();
    }

    /**
     * Loads all shopping items with their associated amount types and categories from the repository.
     * Sets up the allShoppingItemWithAmountTypeAndCategoryLiveData with the loaded data.
     */
    public void loadAllShoppingItemWithAmountTypeAndCategory() {
        allShoppingItemWithAmountTypeAndCategoryLiveData = shoppingRepository
                .loadAllShoppingItemsWithAmountTypeAndCategory(getUserValue());
    }

    /**
     * Sets up an observer for the shopping items LiveData.
     * The observer will be notified whenever the shopping items data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItemWithAmountTypeAndCategory>> observer) {
        allShoppingItemWithAmountTypeAndCategoryLiveData.observe(owner, observer);
    }

    /**
     * Gets the current value of shopping items LiveData.
     * Returns an empty list if the LiveData value is null.
     *
     * @return List of shopping items with amount types and categories
     */
    public List<ShoppingItemWithAmountTypeAndCategory> getAllShoppingItemWithAmountTypeAndCategoryValue() {
        return Optional.ofNullable(allShoppingItemWithAmountTypeAndCategoryLiveData.getValue()).orElse(new ArrayList<>());
    }

    /**
     * Updates multiple shopping items in the database.
     *
     * @param shoppingItems List of shopping items to update
     */
    public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingRepository.updateShoppingItems(shoppingItems);
    }

    /**
     * Updates the flag of a shopping item in the local database and synchronizes it with the server.
     * This method performs a local update operation and then sends the updated data to the server.
     *
     * @param shoppingItem The shopping item whose flag needs to be updated
     */
    public void updateShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, () -> updateShoppingItemServer(shoppingItem));
    }

    /**
     * Sends an updated shopping item to the server using WebSocket POST operation.
     * This method is called after successfully updating a shopping item locally.
     *
     * @param shoppingItem The updated shopping item to be sent to the server
     */
    public void updateShoppingItemServer(ShoppingItem shoppingItem) {
        shoppingServiceRepository.websocketPostShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.UPDATE), getUserValue());
    }

    /**
     * Deletes a shopping item from the local database and synchronizes the deletion with the server.
     * This method performs a local deletion operation and then sends the deletion to the server.
     *
     * @param shoppingItem The shopping item to be deleted
     */
    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.deleteShoppingItemSoft(shoppingItem, () -> deleteShoppingItemServer(shoppingItem));
    }

    /**
     * Sends a shopping item deletion request to the server using WebSocket DELETE operation.
     * This method is called after successfully deleting a shopping item locally.
     *
     * @param shoppingItem The shopping item to be deleted from the server
     */
    public void deleteShoppingItemServer(ShoppingItem shoppingItem) {
        shoppingServiceRepository.websocketDeleteShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.DELETE), getUserValue());
    }

    /**
     * Loads all categories from the repository for the current user.
     * Sets up the allCategoryLiveData with the loaded data.
     */
    public void loadAllCategory() {
        allCategoryLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    /**
     * Sets up an observer for the categories LiveData.
     * The observer will be notified whenever the categories data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setAllCategoryObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }

    /**
     * Deletes a category from the local database and synchronizes the deletion with the server.
     * This method performs a local deletion operation and then sends the deletion to the server.
     *
     * @param category The category to be deleted
     */
    public void deleteCategory(Category category) {
        shoppingRepository.deleteCategorySoft(category, () -> deleteCategoryServer(category));
    }

    /**
     * Inserts a new category into the local database and synchronizes it with the server.
     * This method performs a local insert operation and then sends the data to the server.
     *
     * @param category The category to be inserted
     */
    public void insertCategory(Category category) {
        shoppingRepository.insertCategory(getUserValue(), category, () -> insertCategoryServer(category));
    }

    /**
     * Updates a category in the local database and synchronizes the update with the server.
     * This method performs a local update operation and then sends the updated data to the server.
     *
     * @param category The category to be updated
     */
    public void updateCategory(Category category) {
        shoppingRepository.updateCategoryFlag(category, () -> updateCategoryServer(category));
    }

    /**
     * Updates category in database without setting up a flag for it to be updated on a server.
     * This method performs a local update only, without server synchronization.
     *
     * @param category - to update
     */
    public void updateLocalCategory(Category category) {
        shoppingRepository.updateCategoryLocal(category);
    }

    /**
     * Sends a new category to the server using WebSocket PUT operation.
     * This method is called after successfully inserting a category locally.
     *
     * @param category The category to be sent to the server
     */
    private void insertCategoryServer(Category category) {
        shoppingServiceRepository.websocketPutCategory(ServiceUtil.categoryToCategoryDto(category, ModifyState.INSERT), getUserValue());
    }

    /**
     * Sends an updated category to the server using WebSocket POST operation.
     * This method is called after successfully updating a category locally.
     *
     * @param category The updated category to be sent to the server
     */
    private void updateCategoryServer(Category category) {
        shoppingServiceRepository.websocketPostCategory(ServiceUtil.categoryToCategoryDto(category, ModifyState.UPDATE), getUserValue());
    }

    /**
     * Sends a category deletion request to the server using WebSocket DELETE operation.
     * This method is called after successfully deleting a category locally.
     *
     * @param category The category to be deleted from the server
     */
    private void deleteCategoryServer(Category category) {
        shoppingServiceRepository.websocketDeleteCategory(ServiceUtil.categoryToCategoryDto(category, ModifyState.DELETE), getUserValue());
    }

    /**
     * Gets the size of the category list.
     *
     * @return size of category list
     */
    public int getSizeCategory() {
        return Optional.ofNullable(getAllCategoryLiveData().getValue()).orElse(new ArrayList<>()).size();
    }

    /**
     * Checks if the tutorial has been seen by the user.
     *
     * @return true if tutorial has been seen, false otherwise
     */
    public boolean isTutorialSeen() {
        return sharedRepository.isTutorialSeen();
    }

    /**
     * Marks the tutorial as seen or not seen for the user.
     *
     * @param seen true if tutorial has been seen, false otherwise
     */
    public void tutorialSeen(boolean seen) {
        sharedRepository.tutorialSeen(seen);
    }
}
