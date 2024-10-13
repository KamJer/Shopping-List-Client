package pl.kamjer.shoppinglist.websocketconnect.funcIntarface;

import okhttp3.WebSocket;

@FunctionalInterface
public interface OnMessageAction<T> {

    void action(WebSocket webSocket, T object);
}
