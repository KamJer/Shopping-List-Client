package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.viewmodel.ViewModelInitializer;

import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnConnectChangeAction;

public class ShoppingListActionBarViewModel extends CustomViewModel{

    public ShoppingListActionBarViewModel(ShoppingRepository shoppingRepository,
                                          ShoppingServiceRepository shoppingServiceRepository,
                                          SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<ShoppingListActionBarViewModel> initializer = new ViewModelInitializer<>(ShoppingListActionBarViewModel.class, creationExtras ->
            new ShoppingListActionBarViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    public void setOnOpenConnectionAction(OnConnectChangeAction action) {
        shoppingServiceRepository.setOnConnectChangeAction(action);
    }

    public boolean isConnected() {
        return shoppingServiceRepository.isConnected();
    }

    public void reconnectWebsocket() {
        shoppingServiceRepository.reconnectWebsocket();
    }
}
