package pl.kamjer.shoppinglist.websocketconnect.funcIntarface;

import okhttp3.WebSocket;

@FunctionalInterface
public interface OnMessageAction {

    void action(WebSocket webSocket, String text);
}
