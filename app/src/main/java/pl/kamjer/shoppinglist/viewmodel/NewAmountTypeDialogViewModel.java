package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.viewmodel.ViewModelInitializer;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

/**
 * ViewModel for managing the creation and updating of amount types.
 * Handles local database operations and synchronization with the server.
 */
public class NewAmountTypeDialogViewModel extends CustomViewModel{

    /**
     * Constructor for NewAmountTypeDialogViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public NewAmountTypeDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of NewAmountTypeDialogViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<NewAmountTypeDialogViewModel> initializer = new ViewModelInitializer<>(NewAmountTypeDialogViewModel.class, creationExtras ->
            new NewAmountTypeDialogViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    /**
     * Inserts a new amount type into the local database and synchronizes it with the server.
     * This method performs a local insert operation and then sends the data to the server.
     *
     * @param amountType The amount type to be inserted
     */
    public void insertAmountType(AmountType amountType) {
        shoppingRepository.insertAmountType(getUserValue(), amountType, () -> putAmountTypeServer(amountType));
    }

    /**
     * Updates an existing amount type in the local database and synchronizes it with the server.
     * This method performs a local update operation and then sends the updated data to the server.
     *
     * @param amountType The amount type to be updated
     */
    public void updateAmountType(AmountType amountType) {
        shoppingRepository.updateAmountTypeSoft(amountType, () -> postAmountTypeServer(amountType));
    }

    /**
     * Sends a new amount type to the server using WebSocket PUT operation.
     * This method is called after successfully inserting an amount type locally.
     *
     * @param amountType The amount type to be sent to the server
     */
    private void putAmountTypeServer(AmountType amountType) {
        shoppingServiceRepository.websocketPutAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.INSERT), getUserValue());
    }

    /**
     * Sends an updated amount type to the server using WebSocket POST operation.
     * This method is called after successfully updating an amount type locally.
     *
     * @param amountType The updated amount type to be sent to the server
     */
    private void postAmountTypeServer(AmountType amountType) {
        shoppingServiceRepository.websocketPostAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.UPDATE), getUserValue());
    }
}
