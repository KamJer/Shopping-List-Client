package pl.kamjer.shoppinglist.websocketconnect.funcIntarface;

import okhttp3.WebSocket;
import okhttp3.Response;


@FunctionalInterface
public interface OnFailureAction {

    void action(WebSocket webSocket, Throwable t, Response response);
}
