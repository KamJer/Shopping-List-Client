package pl.kamjer.shoppinglist.util.loadManager;

import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;

public class CategoryMessageHandler {

    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;

    public CategoryMessageHandler(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository) {
        this.shoppingRepository = shoppingRepository;
        this.shoppingServiceRepository = shoppingServiceRepository;
    }

    public void register(User user) {
        shoppingServiceRepository.setOnMessageActionAddCategory((webSocket, categoryDto) ->
                shoppingRepository.updateCategoryFinal(categoryDto, user));

        shoppingServiceRepository.setOnMessageActionUpdateCategory((webSocket, categoryDto) ->
                shoppingRepository.updateCategoryFinal(categoryDto, user));

        shoppingServiceRepository.setOnMessageActionDeleteCategory((webSocket, categoryDto) ->
                shoppingRepository.deleteCategoryFinal(categoryDto, user));
    }
}
