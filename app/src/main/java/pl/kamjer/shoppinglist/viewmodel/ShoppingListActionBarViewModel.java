package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.viewmodel.ViewModelInitializer;

import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnConnectChangeAction;

/**
 * ViewModel for managing the shopping list action bar functionality.
 * Handles WebSocket connection management and synchronization operations.
 */
public class ShoppingListActionBarViewModel extends CustomViewModel{

    /**
     * Constructor for ShoppingListActionBarViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public ShoppingListActionBarViewModel(ShoppingRepository shoppingRepository,
                                          ShoppingServiceRepository shoppingServiceRepository,
                                          SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of ShoppingListActionBarViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<ShoppingListActionBarViewModel> initializer = new ViewModelInitializer<>(ShoppingListActionBarViewModel.class, creationExtras ->
            new ShoppingListActionBarViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    /**
     * Sets up an action to be performed when the WebSocket connection state changes.
     * This allows the UI to respond to connection status updates (connected/disconnected).
     *
     * @param action The action to be performed when connection state changes
     */
    public void setOnOpenConnectionAction(OnConnectChangeAction action) {
        shoppingServiceRepository.addOnConnectionChangedAction(action);
    }

    /**
     * Checks if the WebSocket connection is currently active.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return shoppingServiceRepository.isConnected();
    }

    /**
     * Forces a reconnection to the WebSocket and synchronizes data with the server.
     * This method reconnects the WebSocket and then triggers a data synchronization.
     */
    public void reconnectWebsocket() {
        shoppingServiceRepository.reconnectWebsocket();
        synchronizeData();
    }
}
