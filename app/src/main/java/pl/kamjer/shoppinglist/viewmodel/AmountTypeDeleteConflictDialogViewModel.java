package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;

/**
 * ViewModel for managing the amount type delete conflict dialog.
 * Handles loading amount types, deleting shopping items, and updating amount types.
 */
public class AmountTypeDeleteConflictDialogViewModel extends CustomViewModel{

    /**
     * LiveData containing the list of all amount types.
     * Used to observe changes in amount type data.
     */
    protected LiveData<List<AmountType>> amountTypesLiveData;

    /**
     * ViewModel initializer for creating instances of AmountTypeDeleteConflictDialogViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<AmountTypeDeleteConflictDialogViewModel> initializer = new ViewModelInitializer<>(
            AmountTypeDeleteConflictDialogViewModel.class,
            creationExtras ->
                    new AmountTypeDeleteConflictDialogViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    /**
     * Constructor for AmountTypeDeleteConflictDialogViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public AmountTypeDeleteConflictDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * Initializes the ViewModel by loading user data and all amount types.
     * This method should be called to set up the ViewModel before use.
     */
    public void initialize() {
        loadUser();
        loadAllAmountTypes();
    }

    /**
     * Loads all amount types from the repository for the current user.
     * Sets up the amountTypesLiveData with the loaded data.
     */
    public void loadAllAmountTypes() {
        amountTypesLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    /**
     * Sets up an observer for the amount types LiveData.
     * The observer will be notified whenever the amount types data changes.
     *
     * @param owner   The LifecycleOwner to observe
     * @param observer The observer that will receive updates
     */
    public void setAmountTypesLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        amountTypesLiveData.observe(owner, observer);
    }

    /**
     * Deletes shopping items associated with the specified amount type and then deletes the amount type.
     * Uses soft delete for shopping items and then deletes the amount type.
     *
     * @param amountType The amount type whose associated shopping items should be deleted
     */
    public void deleteShoppingItemsForAmountType(AmountType amountType) {
        shoppingRepository.deleteShoppingItemsSoftDeleteAndDeleteAmountType(amountType, this::synchronizeData);
    }

    /**
     * Updates the amount type of shopping items and then deletes the specified amount type.
     * Changes the amount type of existing shopping items to the new amount type before deleting the old one.
     *
     * @param amountTypeToDelete The amount type to be deleted
     * @param amountTypeToChange The amount type to change existing shopping items to
     */
    public void updateShoppingItemsAmountTypeAndDeleteAmountType(AmountType amountTypeToDelete, AmountType amountTypeToChange) {
        shoppingRepository.updateShoppingItemsAmountTypeAndDeleteAmountType(amountTypeToDelete, amountTypeToChange, this::synchronizeData);
    }
}
