package pl.kamjer.shoppinglist.websocketconnect;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnClosedAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnConnectChangeAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnOpenAction;
import pl.kamjer.shoppinglist.websocketconnect.message.Command;
import pl.kamjer.shoppinglist.websocketconnect.message.Header;
import pl.kamjer.shoppinglist.websocketconnect.message.Message;
import pl.kamjer.shoppinglist.websocketconnect.message.SubscribeMessage;

@RequiredArgsConstructor
@Getter
@Log
public class WebSocket {

    private static final String NOT_OPEN_EXCEPTION_MESSAGE = "Connection not open";
    private static final String NO_SUCH_SUBS_EXCEPTION_MESSAGE = "There is no subscription with such id";

    private final Request.Builder request;
    private final String baseUrl;
    private final WebSocketConnectorListener webSocketListener;
    private final MutableLiveData<HashMap<String, SubscribeMessage>> subscribeMessagesLiveData;
    private final MutableLiveData<LinkedList<Message>> messageQueueLiveData;
    private final MutableLiveData<Message> connectedLiveData;
    private final OnMessageHolder onMessageHolder;

    private okhttp3.WebSocket okHttpWebSocket;

    private OnConnectChangeAction onConnectAction;

    private Observer<HashMap<String, SubscribeMessage>> subscribeMessagesLiveDataObserver = subscribeMessages -> {
        if (getOpenValue().isPresent()) {
            sendSavedSubs(subscribeMessages);
        }
    };

    private Observer<Message> connectedLiveDataObserver = openMessage -> {
//        if message is not null it means connection was successful
        if (openMessage != null) {
            sendSavedSubs(getSubscribeMessageValue());
            LinkedList<Message> sendMessages = getStompMessagesValue();
            while (!sendMessages.isEmpty()) {
                Message message = sendMessages.poll();
                message.getHeaders().put(Header.ID, getOpenValue().orElseThrow(() -> new NoSuchElementException(NOT_OPEN_EXCEPTION_MESSAGE)).getHeaders().get(Header.ID));
                sendMessage(message);
            }
        } else {
//            if received connect message is null unregister all observers
            unregisterObservers();
        }
        onConnectAction.action(openMessage != null);
    };

    private Observer<LinkedList<Message>> messageQueueLiveDataObserver = sendMessages -> {
        if (getOpenValue().isPresent()) {
            while (!sendMessages.isEmpty()) {
                Message message = sendMessages.poll();
                message.getHeaders().put(Header.ID, getOpenValue().orElseThrow(() -> new NoSuchElementException(NOT_OPEN_EXCEPTION_MESSAGE)).getHeaders().get(Header.ID));
                sendMessage(message);
            }
        }
    };

    public WebSocket(String baseUrl) {
        this.request = new Request.Builder();
        this.baseUrl = baseUrl;
        this.webSocketListener = new WebSocketConnectorListener();
        this.subscribeMessagesLiveData = new MutableLiveData<>(new HashMap<>());
        this.messageQueueLiveData = new MutableLiveData<>(new LinkedList<>());
        this.connectedLiveData =new MutableLiveData<>();
        this.onMessageHolder =new OnMessageHolder(new HashMap<>(), new HashMap<>(), null);

        request.url(baseUrl);
    }

    void sendSavedSubs(HashMap<String, SubscribeMessage> subscribeMessages) {
        Optional.ofNullable(okHttpWebSocket).ifPresent(webSocket ->
                subscribeMessages.values().forEach(subscribeMessage -> {
                            if (subscribeMessage.isSend() && subscribeMessage.isUnsubscribed()) {
                                log.info("Sending message by websocket connection:\n" +
                                        subscribeMessage.getUnsubscribeMessage() + "\n");
                                if (webSocket.send(subscribeMessage.getUnsubscribeMessage())) {
                                    subscribeMessage.setSend(false);
                                    subscribeMessage.setUnsubscribed(true);
                                    subscribeMessages.remove(subscribeMessage);
                                    getOnMessageHolder().getOnMessageActionsForSubs().remove(subscribeMessage.getBaseUrl());
                                }
                            } else if (!subscribeMessage.isSend()) {
                                log.info("Sending message by websocket connection:\n" +
                                        subscribeMessage.getSubscribeMessage() + "\n");
                                if (webSocket.send(subscribeMessage.getSubscribeMessage())) {
                                    subscribeMessage.setSend(true);
                                    subscribeMessage.setUnsubscribed(false);
                                }
                            }
                        }));
    }

    private void sendMessage(Message message) {
        log.info("Sending message by websocket connection:\n" + message.jsonyfy());
        Optional.ofNullable(okHttpWebSocket).ifPresent(webSocket -> webSocket.send(message.jsonyfy()));
    }

    public WebSocket basicWebsocketHeader() {
        request.url(baseUrl)
                .header("Connection", "Upgrade")
                .header("Upgrade", "websocket")
                .header("Sec-WebSocket-Version", "13");
        return this;
    }


    public WebSocket header(String key, String value) {
        request.header(key, value);
        return this;
    }

    public WebSocket connect(OkHttpClient okHttpClient) {
//        makes sure that observers are set on main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            subscribeMessagesLiveData.observeForever(subscribeMessagesLiveDataObserver);
            connectedLiveData.observeForever(connectedLiveDataObserver);
            messageQueueLiveData.observeForever(messageQueueLiveDataObserver);

            webSocketListener.setMessageBroker(new MessageBroker(getSubscribeMessageValue(), connectedLiveData, onMessageHolder));
            webSocketListener.setGson(new Gson());
            okHttpWebSocket = okHttpClient.newWebSocket(request.build(), webSocketListener);
        });
        return this;
    }

    private void unregisterObservers() {
        subscribeMessagesLiveData.removeObserver(subscribeMessagesLiveDataObserver);
        connectedLiveData.removeObserver(connectedLiveDataObserver);
        messageQueueLiveData.removeObserver(messageQueueLiveDataObserver);
    }

    public WebSocket disconnect() {
        okHttpWebSocket.close(1000, "finished");
        connectedLiveData.postValue(null);
        return this;
    }

    public WebSocket disconnect(int code, String reason) {
        okHttpWebSocket.close(code, reason);
        connectedLiveData.postValue(null);
        return this;
    }

    public WebSocket onOpen(OnOpenAction action) {
        webSocketListener.setOnOpenAction(action);
        return this;
    }

    public WebSocket onClosed(OnClosedAction action) {
        webSocketListener.setOnClosedAction(action);
        return this;
    }

    public WebSocket onClosing(OnClosedAction action) {
        webSocketListener.setOnClosingAction(action);
        return this;
    }

    public WebSocket onFailure(OnFailureAction action) {
        webSocketListener.setOnFailureAction(action);
        return this;
    }

    public WebSocket onError(OnMessageAction<String> action) {
        getOnMessageHolder().setOnErrorAction(action);
        return this;
    }

    /**
     * Method for subscribing a topic on a service, creates new gson object
     * @param subscribeUrl - url of a topic
     * @param type - type of a field topic will return
     * @param onMassageAction - action on a message
     * @return websocket
     */
    public WebSocket subscribe(String subscribeUrl, Type type, OnMessageAction<?> onMassageAction) {
        subscribe(new Gson(), subscribeUrl, type, onMassageAction);
        return this;
    }

    /**
     * Subscribes topic on a service, takes gson as a parameter allowing definition of a gson with its own deserialization and serialization
     * @param gson - Gson object for deserialization and serialization
     * @param subscribeUrl - url of a topic
     * @param type - type of a field topic will return
     * @param onMassageAction - action on a message
     * @return websocket
     */
    public WebSocket subscribe(Gson gson, String subscribeUrl, Type type, OnMessageAction<?> onMassageAction, String... parameters) {
        SubscribeMessage subscribeMessage = new SubscribeMessage(subscribeUrl, type, false, false, parameters);
        if (parameters.length > 0) {
            getOnMessageHolder().addOnMessageAction(subscribeMessage.getParameterUrl(), onMassageAction);
            getOnMessageHolder().getGsonsForSubs().put(subscribeMessage.getParameterUrl(), gson);
        } else {
            getOnMessageHolder().addOnMessageAction(subscribeMessage.getBaseUrl(), onMassageAction);
            getOnMessageHolder().getGsonsForSubs().put(subscribeMessage.getBaseUrl(), gson);
        }
        addSubscribeMessagesValue(subscribeMessage);
        return this;
    }

    public WebSocket unsubscribe(String subscribeUrl) {
        Optional.ofNullable(getSubscribeMessageValue().get(subscribeUrl))
                .map(subscribeMessage -> {
                    subscribeMessage.setUnsubscribed(true);
                    return subscribeMessage;
                })
                .orElseThrow(IllegalStateException::new);
        subscribeMessagesLiveData.postValue(subscribeMessagesLiveData.getValue());
        return this;
    }

    public WebSocket send(Gson gson, String dest, Object body, String... parameters) {
        HashMap<Header, String> headers = new HashMap<>();
        headers.put(Header.DEST, dest);
        headers.put(Header.BODY, gson.toJson(body));
        StringBuilder parameterBuilder = new StringBuilder();
        for (String parameter: parameters) {
            parameterBuilder.append(parameter).append(";");
        }
        headers.put(Header.PARA, parameterBuilder.toString());
        addMessage(new Message(Command.MESSAGE, headers));
        return this;
    }

    public WebSocket onConnectAction(OnConnectChangeAction action) {
        onConnectAction = action;
        return this;
    }

    public boolean isConnected() {
        return Optional.ofNullable(connectedLiveData.getValue()).isPresent();
    }

    private HashMap<String, SubscribeMessage> getSubscribeMessageValue() {
        return Optional.ofNullable(subscribeMessagesLiveData.getValue()).orElse(new HashMap<>());
    }

    private void addSubscribeMessagesValue(SubscribeMessage subscribeMessage) {
        HashMap<String, SubscribeMessage> subscribeMessageList = getSubscribeMessageValue();
        subscribeMessageList.put(subscribeMessage.getSubscribeUrl(), subscribeMessage);
        subscribeMessagesLiveData.postValue(subscribeMessageList);
    }

    private Optional<Message> getOpenValue() {
        return Optional.ofNullable(connectedLiveData.getValue());
    }

    private LinkedList<Message> getStompMessagesValue() {
        return Optional.ofNullable(messageQueueLiveData.getValue()).orElse(new LinkedList<>());
    }

    private void addMessage(Message message) {
        LinkedList<Message> messages = getStompMessagesValue();
        messages.add(message);
        messageQueueLiveData.postValue(messages);
    }


}
