package pl.kamjer.shoppinglist.websocketconnect;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnClosedAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnOpenAction;
import pl.kamjer.shoppinglist.websocketconnect.message.Message;

@RequiredArgsConstructor
@Setter
@Getter
@Log
public class WebSocketConnectorListener extends WebSocketListener {
    protected OnOpenAction onOpenAction;
    private MessageBroker messageBroker;
    protected OnClosedAction onClosingAction;
    protected OnClosedAction onClosedAction;
    protected OnFailureAction onFailureAction;
    private Gson gson;


    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        messageBroker.handleOpening(webSocket, response);
        Optional.ofNullable(onOpenAction).ifPresent(onOpenAction1 -> onOpenAction1.action(webSocket, response));
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Message message = gson.fromJson(text, Message.class);
        messageBroker.directMessage(webSocket, message);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        messageBroker.handleByteMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Optional.ofNullable(onClosingAction).ifPresent(onClosedAction1 -> onClosedAction1.action(webSocket, code, reason));
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        messageBroker.handleClosing(webSocket, code, reason);
        new Handler(Looper.getMainLooper()).post(() -> Optional.ofNullable(onClosedAction).ifPresent(onFailureAction1 -> onFailureAction1.action(webSocket, code, reason)));
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        getMessageBroker().handleFailure(webSocket, t, response);
        new Handler(Looper.getMainLooper()).post(() -> Optional.ofNullable(onFailureAction).ifPresent(onFailureAction1 -> onFailureAction1.action(webSocket, t, response)));

    }
}
