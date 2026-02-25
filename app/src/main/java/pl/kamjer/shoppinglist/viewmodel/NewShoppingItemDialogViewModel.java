package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

/**
 * ViewModel for managing the creation and updating of shopping items.
 * Handles loading of amount types and categories, and synchronization with the server.
 */
public class NewShoppingItemDialogViewModel extends CustomViewModel{

    /**
     * LiveData containing all amount types for the current user.
     * Used to observe changes in the amount types data.
     */
    public LiveData<List<AmountType>> amountTypesListLiveData;

    /**
     * LiveData containing all categories for the current user.
     * Used to observe changes in the categories data.
     */
    public LiveData<List<Category>> categoryListLiveData;

    /**
     * Constructor for NewShoppingItemDialogViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public NewShoppingItemDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of NewShoppingItemDialogViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<NewShoppingItemDialogViewModel> initializer = new ViewModelInitializer<>(NewShoppingItemDialogViewModel.class, creationExtras ->
            new NewShoppingItemDialogViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    /**
     * Loads all amount types from the repository for the current user.
     * Sets up the amountTypesListLiveData with the loaded data.
     */
    public void loadAllAmountTypes() {
        amountTypesListLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    /**
     * Sets up an observer for the amount types LiveData.
     * The observer will be notified whenever the amount types data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setAmountTypesListLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        amountTypesListLiveData.observe(owner, observer);
    }

    /**
     * Loads all categories from the repository for the current user.
     * Sets up the categoryListLiveData with the loaded data.
     */
    public void loadAllCategory() {
        categoryListLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    /**
     * Sets up an observer for the categories LiveData.
     * The observer will be notified whenever the categories data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setCategoryListLiveDataObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        categoryListLiveData.observe(owner, observer);
    }

    /**
     * Inserts a new shopping item into the local database and synchronizes it with the server.
     * This method performs a local insert operation and then sends the data to the server.
     *
     * @param shoppingItem The shopping item to be inserted
     */
    public void insertShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.insertShoppingItem(getUserValue(), shoppingItem, () -> insertShoppingItemServer(shoppingItem));
    }

    /**
     * Sends a new shopping item to the server using WebSocket PUT operation.
     * This method is called after successfully inserting a shopping item locally.
     *
     * @param shoppingItem The shopping item to be sent to the server
     */
    private void insertShoppingItemServer(ShoppingItem shoppingItem) {
        shoppingServiceRepository.websocketPutShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.INSERT), getUserValue());
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
}
