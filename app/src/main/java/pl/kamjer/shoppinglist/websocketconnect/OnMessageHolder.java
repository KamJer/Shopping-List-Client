package pl.kamjer.shoppinglist.websocketconnect;

import com.google.gson.Gson;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;

@AllArgsConstructor
@Setter
public class OnMessageHolder {

    private HashMap<String, Gson> gsonsForSubs;
    private HashMap<String, OnMessageAction<?>> onMessageActionsForSubs;
    @Getter
    private OnMessageAction<String> onErrorAction;

    void addOnMessageAction(String subsUrl, OnMessageAction<?> onMessageAction) {
        onMessageActionsForSubs.put(subsUrl, onMessageAction);
    }

    void putGsonForSubs(String subsUrl, Gson gson) {
        gsonsForSubs.put(subsUrl, gson);
    }

    void removeOnMessageAction(String subsUrl) {
        onMessageActionsForSubs.remove(subsUrl);
    }

    Gson getGsonForSubs(String subsUrl) {
        return gsonsForSubs.get(subsUrl);
    }

    OnMessageAction<?> getOnMessageAction(String subsUrl) {
        return onMessageActionsForSubs.get(subsUrl);
    }
}
