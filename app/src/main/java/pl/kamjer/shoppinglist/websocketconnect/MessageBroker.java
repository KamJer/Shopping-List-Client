package pl.kamjer.shoppinglist.websocketconnect;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import pl.kamjer.shoppinglist.websocketconnect.message.Command;
import pl.kamjer.shoppinglist.websocketconnect.message.Header;
import pl.kamjer.shoppinglist.websocketconnect.message.Message;
import pl.kamjer.shoppinglist.websocketconnect.message.SubscribeMessage;

@AllArgsConstructor
@Log
public class MessageBroker {

    private HashMap<String, SubscribeMessage> subscribeMessages;
    private MutableLiveData<Message> connectedLiveData;
    private OnMessageHolder onMessageHolder;

    public void directMessage(okhttp3.WebSocket webSocket, Message message) {
        switch (message.getCommand()) {
            case CONNECTED -> handleConnected(webSocket, message);
            case MESSAGE -> handleMessage(webSocket, message);
            case SUBSCRIBED -> handleSubscribed(webSocket, message);
            case ERROR -> handleError(webSocket, message);
        }
    }

    private void handleConnected(okhttp3.WebSocket webSocket, Message message) {
        connectedLiveData.postValue(message);
    }

    private void handleMessage(okhttp3.WebSocket webSocket, Message message) {
        Optional.ofNullable(subscribeMessages).ifPresent(subscribeMessages -> {
            SubscribeMessage subscribeMessage = subscribeMessages.get(message.getHeaders().get(Header.DEST));
            Gson gson = onMessageHolder.getGsonForSubs(subscribeMessage.getSubscribeUrl());
            String messageBody = message.getHeaders().get(Header.BODY);
            onMessageHolder.getOnMessageAction(subscribeMessage.getSubscribeUrl()).action(webSocket, gson.fromJson(messageBody, subscribeMessage.getType()));
        });
    }

    private void handleSubscribed(okhttp3.WebSocket webSocket, Message message) {
        log.info(message.jsonyfy());
    }

    private void handleError(okhttp3.WebSocket webSocket, Message message) {
        log.warning(message.jsonyfy());
        Optional.ofNullable(onMessageHolder.getOnErrorAction()).ifPresent(stringOnMessageAction -> stringOnMessageAction.action(webSocket, message.getHeaders().get(Header.BODY)));
    }

    public void handleFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
        connectedLiveData.postValue(null);
    }

    public void handleClosing(okhttp3.WebSocket webSocket, int code, String response) {
        connectedLiveData.postValue(null);
    }

    public void handleByteMessage(WebSocket webSocket, ByteString bytes) {
        log.warning("byteString messaging not supported");
    }

    public void handleOpening(WebSocket webSocket, Response response) {
        HashMap<Header, String> headers = new HashMap<>();
        webSocket.send(new Message(Command.CONNECT, headers).jsonyfy());
    }
}
