package pl.kamjer.shoppinglist.util.loadManager;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;

@AllArgsConstructor
public class UtilityMessageHandler {
    private final ShoppingRepository shoppingRepository;
    private final ShoppingServiceRepository shoppingServiceRepository;

    public void register(User user, OnMessageAction<String> onErrorAction, OnFailureAction failure) {
        shoppingServiceRepository.setOnMessageActionSynchronize((webSocket, allDto) ->
                shoppingRepository.synchronizeData(user, allDto));

        shoppingServiceRepository.setOnMessageActionPip((webSocket, pip) ->
                shoppingRepository.getAllDataAndAct(user,
                        (amountTypeList, categoryList, shoppingItemList) ->
                                shoppingServiceRepository.websocketSynchronize(
                                        ServiceUtil.collectEntitiyToAllDto(user, amountTypeList, categoryList, shoppingItemList), user)));

        shoppingServiceRepository.setOnErrorAction((webSocket, object) ->
                new android.os.Handler(android.os.Looper.getMainLooper())
                        .post(() -> onErrorAction.action(webSocket, object)));

        shoppingServiceRepository.setOnFailureAction(failure);
    }
}
