package pl.kamjer.shoppinglist.websocketconnect.funcIntarface;

import okhttp3.WebSocket;

@FunctionalInterface
public interface OnClosedAction {

    void action(WebSocket webSocket, int code, String reason);
}
