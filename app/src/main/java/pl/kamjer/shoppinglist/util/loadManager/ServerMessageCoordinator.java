package pl.kamjer.shoppinglist.util.loadManager;

import lombok.Getter;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;

@Getter
public class ServerMessageCoordinator {

    private final UtilityMessageHandler utilityHandler;
    private final AmountTypeMessageHandler amountTypeHandler;
    private final CategoryMessageHandler categoryHandler;
    private final ShoppingItemMessageHandler shoppingItemHandler;

    public ServerMessageCoordinator(ShoppingRepository shoppingRepository,
                                    ShoppingServiceRepository shoppingServiceRepository) {
        this.utilityHandler = new UtilityMessageHandler(shoppingRepository, shoppingServiceRepository);
        this.amountTypeHandler = new AmountTypeMessageHandler(shoppingRepository, shoppingServiceRepository);
        this.categoryHandler = new CategoryMessageHandler(shoppingRepository, shoppingServiceRepository);
        this.shoppingItemHandler = new ShoppingItemMessageHandler(shoppingRepository, shoppingServiceRepository);
    }

    public void register(User user, OnMessageAction<String> onErrorAction, OnFailureAction onFailureAction) {
        utilityHandler.register(user, onErrorAction, onFailureAction);
        amountTypeHandler.register(user);
        categoryHandler.register(user);
        shoppingItemHandler.register(user);
    }
}
