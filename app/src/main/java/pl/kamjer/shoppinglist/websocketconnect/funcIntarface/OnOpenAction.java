package pl.kamjer.shoppinglist.websocketconnect.funcIntarface;

import okhttp3.Response;
import okhttp3.WebSocket;

@FunctionalInterface
public interface OnOpenAction {
    void action(WebSocket webSocket, Response response);
}
