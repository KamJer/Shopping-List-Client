package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

public class AmountTypeViewModel extends CustomViewModel {

    /**
     * LiveData containing all amount types for the current user.
     * Used to observe changes in the amount type data.
     */
    private LiveData<List<AmountType>> allAmountTypeLiveData;

    /**
     * LiveData containing all shopping items associated with a specific amount type.
     * Used to observe changes in the shopping items data for a particular amount type.
     */
    private LiveData<List<ShoppingItem>> allShoppingItemsForAmountTypeLiveData;

    /**
     * Constructor for AmountTypeViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public AmountTypeViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of AmountTypeViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<AmountTypeViewModel> initializer = new ViewModelInitializer<>(
            AmountTypeViewModel.class,
            creationExtras ->
                    new AmountTypeViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    /**
     * Loads all amount types from the repository for the current user.
     * Sets up the allAmountTypeLiveData with the loaded data.
     */
    public void loadAllAmountType() {
        allAmountTypeLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    /**
     * Sets up an observer for the all amount types LiveData.
     * The observer will be notified whenever the amount types data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setAllAmountTypeLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        allAmountTypeLiveData.observe(owner, observer);
    }

    /**
     * Deletes an amount type from the local database and then synchronizes with the server.
     * Uses soft delete for the amount type in the local database.
     *
     * @param amountType The amount type to be deleted
     */
    public void deleteAmountType(AmountType amountType) {
        shoppingRepository.deleteAmountTypeSoft(amountType, () -> deleteAmountTypeServer(amountType));
    }

    /**
     * Loads all shopping items associated with the specified amount type for the current user.
     * Sets up the allShoppingItemsForAmountTypeLiveData with the loaded data.
     *
     * @param amountType The amount type to load shopping items for
     */
    public void loadAllShoppingItemsForAmountType(AmountType amountType) {
        allShoppingItemsForAmountTypeLiveData = shoppingRepository.loadAllShoppingItemsForAmountType(getUserValue(), amountType);
    }

    /**
     * Sets up an observer for the shopping items for a specific amount type LiveData.
     * The observer will be notified whenever the shopping items data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setAllShoppingItemsForAmountTypeLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItem>> observer) {
        allShoppingItemsForAmountTypeLiveData.observe(owner, observer);
    }

    /**
     * Removes an observer from the shopping items for a specific amount type LiveData.
     * This is used to clean up observers when they are no longer needed.
     *
     * @param observer The observer to be removed
     */
    public void removeAllShoppingItemsForAmountTypeLiveDataObserver(Observer<List<ShoppingItem>> observer) {
        allShoppingItemsForAmountTypeLiveData.removeObserver(observer);
    }

    /**
     * Deletes an amount type from the server.
     * Sends a delete request to the server with the amount type data.
     *
     * @param amountType The amount type to be deleted from the server
     */
    public void deleteAmountTypeServer(AmountType amountType) {
        shoppingServiceRepository.websocketDeleteAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.DELETE), getUserValue());
    }
}
