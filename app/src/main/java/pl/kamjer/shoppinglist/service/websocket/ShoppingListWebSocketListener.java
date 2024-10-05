package pl.kamjer.shoppinglist.service.websocket;

import android.content.Context;

import androidx.annotation.NonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@Log
@RequiredArgsConstructor
public class ShoppingListWebSocketListener extends WebSocketListener {

    protected final Context context;
    protected WebSocket webSocket;

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        this.webSocket = webSocket;
        log.info("starting");
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        log.info(text);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        log.info(reason);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        log.info("closed");
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        log.info("failed");
    }
}
