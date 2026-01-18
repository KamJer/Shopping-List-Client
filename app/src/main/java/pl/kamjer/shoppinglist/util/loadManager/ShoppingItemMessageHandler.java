package pl.kamjer.shoppinglist.util.loadManager;

import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;

public class ShoppingItemMessageHandler {
    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;


    public ShoppingItemMessageHandler(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository) {
        this.shoppingRepository = shoppingRepository;
        this.shoppingServiceRepository = shoppingServiceRepository;
    }

    public void register(User user) {
        shoppingServiceRepository.setOnMessageActionAddShoppingItem((webSocket, shoppingItemDto) ->
                shoppingRepository.updateShoppingItemFinal(shoppingItemDto, user));

        shoppingServiceRepository.setOnMessageActionUpdateShoppingItem((webSocket, shoppingItemDto) ->
                shoppingRepository.updateShoppingItemFinal(shoppingItemDto, user));

        shoppingServiceRepository.setOnMessageActionDeleteShoppingItem((webSocket, shoppingItemDto) ->
                shoppingRepository.deleteShoppingItemFinal(shoppingItemDto, user));
    }
}
