package pl.kamjer.shoppinglist.websocketconnect;

import com.google.gson.Gson;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;

@AllArgsConstructor
@Getter
@Setter
public class OnMessageHolder {

    private HashMap<String, Gson> gsonsForSubs;
    private HashMap<String, OnMessageAction<?>> onMessageActionsForSubs;
    private OnMessageAction<String> onErrorAction;

    void addOnMessageAction(String subsUrl, OnMessageAction<?> onMessageAction) {
        onMessageActionsForSubs.put(subsUrl, onMessageAction);

    }
}
