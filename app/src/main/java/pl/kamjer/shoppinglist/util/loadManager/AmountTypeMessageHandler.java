package pl.kamjer.shoppinglist.util.loadManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;

@Getter
@AllArgsConstructor
public class AmountTypeMessageHandler {

    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;

    public void register(User user) {
        shoppingServiceRepository.setOnMessageActionAddAmountType((webSocket, amountTypeDto) ->
                shoppingRepository.updateAmountTypeFinal(amountTypeDto, user));

        shoppingServiceRepository.setOnMessageActionUpdateAmountType((webSocket, amountTypeDto) ->
                shoppingRepository.updateAmountTypeFinal(amountTypeDto, user));

        shoppingServiceRepository.setOnMessageActionDeleteAmountType((webSocket, amountTypeDto) ->
                shoppingRepository.deleteAmountTypeFinal(amountTypeDto, user));

    }
}
