package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;

/**
 * ViewModel for managing the list of bought shopping items.
 * Handles loading, updating, and deleting shopping items and categories.
 */
public class BoughtShoppingItemsListViewModel extends CustomViewModel {

    /**
     * LiveData containing all shopping items with their associated amount type and category data.
     * Used to observe changes in the shopping items data.
     */
    private LiveData<List<ShoppingItemWithAmountTypeAndCategory>> allShoppingItemWithAmountTypeAndCategoryLiveData;

    /**
     * LiveData containing all categories for the current user.
     * Used to observe changes in the categories data.
     */
    private LiveData<List<Category>> allCategoryLiveData;

    /**
     * LiveData containing the current user data.
     * Used to observe changes in the user data.
     */
    private LiveData<User> userLiveData;

    /**
     * Constructor for BoughtShoppingItemsListViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public BoughtShoppingItemsListViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of BoughtShoppingItemsListViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<BoughtShoppingItemsListViewModel> initializer = new ViewModelInitializer<>(
            BoughtShoppingItemsListViewModel.class,
            creationExtras ->
                    new BoughtShoppingItemsListViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    /**
     * Loads all shopping items with their associated amount type and category data for the current user.
     * Sets up the allShoppingItemWithAmountTypeAndCategoryLiveData with the loaded data.
     */
    public void loadAllShoppingItemWithAmountTypeAndCategoryLiveData() {
        allShoppingItemWithAmountTypeAndCategoryLiveData = shoppingRepository.loadAllShoppingItemsWithAmountTypeAndCategory(getUserValue());
    }

    /**
     * Sets up an observer for the shopping items with amount type and category LiveData.
     * The observer will be notified whenever the shopping items data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItemWithAmountTypeAndCategory>> observer) {
        this.allShoppingItemWithAmountTypeAndCategoryLiveData.observe(owner, observer);
    }

    /**
     * Updates the flag/status of a shopping item.
     * This typically marks an item as bought or not bought.
     *
     * @param shoppingItem The shopping item to be updated
     */
    public void updateShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, this::synchronizeData);
    }

    /**
     * Deletes a shopping item from the database.
     * Uses soft delete to mark the item as deleted rather than permanently removing it.
     *
     * @param shoppingItem The shopping item to be deleted
     */
    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.deleteShoppingItemSoft(shoppingItem, this::synchronizeData);
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
    public void setAllCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }
}
