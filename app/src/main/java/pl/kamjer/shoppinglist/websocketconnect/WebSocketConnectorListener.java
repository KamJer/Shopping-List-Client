package pl.kamjer.shoppinglist.websocketconnect;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnClosedAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnOpenAction;

@RequiredArgsConstructor
@Setter
@Getter
public class WebSocketConnectorListener extends WebSocketListener {
    protected OnOpenAction onOpenAction;
    protected List<OnMessageAction> onMassageActions;
    protected OnClosedAction onClosingAction;
    protected OnClosedAction onClosedAction;
    protected OnFailureAction onFailureAction;

    protected OnOpenAction onOpenActionDefault;
    protected OnClosedAction onClosedActionDefault;
    protected OnFailureAction onFailureActionDefault;


    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        Optional.ofNullable(onOpenActionDefault).ifPresent(onOpenAction1 -> onOpenAction1.action(webSocket, response));
        Optional.ofNullable(onOpenAction).ifPresent(onOpenAction1 -> onOpenAction1.action(webSocket, response));
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Optional.ofNullable(onMassageActions).ifPresent(onMassageActionList -> onMassageActionList.forEach(onMassageAction -> onMassageAction.action(webSocket, text)));
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        Optional.ofNullable(onMassageActions).ifPresent(onMassageActionList -> onMassageActionList.forEach(onMassageAction -> onMassageAction.action(webSocket, bytes.toString())));
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Optional.ofNullable(onClosingAction).ifPresent(onClosedAction1 -> onClosedAction1.action(webSocket, code, reason));
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Optional.ofNullable(onClosedActionDefault).ifPresent(onFailureAction1 -> onFailureAction1.action(webSocket, code, reason));
        Optional.ofNullable(onClosedAction).ifPresent(onFailureAction1 -> onFailureAction1.action(webSocket, code, reason));
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        Optional.ofNullable(onFailureActionDefault).ifPresent(onFailureAction1 -> onFailureAction1.action(webSocket, t, response));
        Optional.ofNullable(onFailureAction).ifPresent(onFailureAction1 -> onFailureAction1.action(webSocket, t, response));
    }

    public void addOnMessageActin(OnMessageAction action){
        this.onMassageActions.add(action);
    }

    public void removeOnMessageAction(OnMessageAction action) {
        this.onMassageActions.remove(action);

    }
}
