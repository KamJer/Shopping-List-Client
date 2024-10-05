package pl.kamjer.shoppinglist.websocketconnect;

import androidx.lifecycle.MutableLiveData;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.ByteString;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnClosedAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnOpenAction;

@RequiredArgsConstructor
@Getter
@Log
public class WebSocket {

    protected static final String NOT_OPEN_EXCEPTION_MESSAGE = "Connection not open";
    protected static final String NO_SUCH_SUBS_EXCEPTION_MESSAGE = "There is no subscription with such id";

    protected final Request.Builder request;
    protected final String baseUrl;
    protected final WebSocketConnectorListener webSocketListener;
    protected final MutableLiveData<HashMap<String, SubscribeMessage>> subscribeMessagesLiveData;
    protected final MutableLiveData<LinkedList<SendMessage>> messageQueueLiveData;
    private final MutableLiveData<Boolean> openLiveData;
    private boolean connected;

    protected okhttp3.WebSocket okHttpWebSocket;

    public WebSocket(String baseUrl) {
        this(new Request.Builder(),
                baseUrl,
                new WebSocketConnectorListener(),
                new MutableLiveData<>(new HashMap<>()),
                new MutableLiveData<>(new LinkedList<>()),
                new MutableLiveData<>(false));

        webSocketListener.setOnOpenActionDefault((webSocket, response) -> openLiveData.postValue(true));
        webSocketListener.setOnClosedActionDefault((webSocket, code, reason) -> openLiveData.postValue(false));
        webSocketListener.setOnFailureActionDefault((webSocket, t, reason) -> openLiveData.postValue(false));

        request.url(baseUrl);
    }

    protected void sendSavedSubs(HashMap<String, SubscribeMessage> subscribeMessages) {
        Optional.ofNullable(okHttpWebSocket).ifPresent(webSocket ->
                subscribeMessages.values()
                        .forEach(subscribeMessage -> {
                            if (subscribeMessage.isSend() && subscribeMessage.isUnsubscribed()) {
                                log.info("Sending message by websocket connection:\n" +
                                        subscribeMessage.getUnsubscribeMessage() + "\n" +
                                        subscribeMessage.getByteUnsubscribeMessage());
                                if (webSocket.send(subscribeMessage.getByteUnsubscribeMessage())) {
                                    subscribeMessage.setSend(false);
                                    subscribeMessage.setUnsubscribed(true);
                                    subscribeMessages.remove(subscribeMessage.getId());
                                }
                            } else if (!subscribeMessage.isSend()) {
                                log.info("Sending message by websocket connection:\n" +
                                        subscribeMessage.getSubscribeMessage() + "\n" +
                                        subscribeMessage.getByteSubscribeMessage());
                                if (webSocket.send(subscribeMessage.getByteSubscribeMessage())) {
                                    subscribeMessage.setSend(true);
                                    subscribeMessage.setUnsubscribed(false);
                                }
                            }
                        }));
    }

    protected void sendMessage(SendMessage sendMessage) {
        log.info("Sending message by websocket connection:\n" + sendMessage.getMessage() + "\n" + sendMessage.getByteMessage());
        Optional.ofNullable(okHttpWebSocket).ifPresent(webSocket -> webSocket.send(sendMessage.getByteMessage()));
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
        subscribeMessagesLiveData.observeForever(subscribeMessages -> {
            if (getOpenValue()) {
                sendSavedSubs(subscribeMessages);
            }
        });
        openLiveData.observeForever(open -> {
            if (open) {
                if (!connected) {
                    sendConnectMessage();
                    connected = true;
                }
                sendSavedSubs(getSubscribeMessageValue());
                LinkedList<SendMessage> sendMessages = getStompMessagesValue();
                while (!sendMessages.isEmpty()) {
                    sendMessage(sendMessages.poll());
                }
            }
        });
        messageQueueLiveData.observeForever(sendMessages -> {
            if (getOpenValue()) {
                while (!sendMessages.isEmpty()) {
                    sendMessage(sendMessages.poll());
                }
            }
        });
        webSocketListener.setOnMassageActions(getSubscribeMessageValue()
                .values()
                .stream()
                .map(SubscribeMessage::getOnMessageAction)
                .collect(Collectors.toList()));
        okHttpWebSocket = okHttpClient.newWebSocket(request.build(), webSocketListener);
        return this;
    }

    public WebSocket disconnect() {
        okHttpWebSocket.close(1000, "finished");
        return this;
    }

    public WebSocket disconnect(int code, String reason) {
        okHttpWebSocket.close(code, reason);
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

    public WebSocket subscribe(String id, String subscribeUrl, OnMessageAction onMassageAction) {
        addSubscribeMessagesValue(new SubscribeMessage(id, subscribeUrl, onMassageAction, false, false));
        return this;
    }

    public WebSocket unsubscribe(String id) {
        Optional.ofNullable(getSubscribeMessageValue().get(id))
                .map(subscribeMessage -> {
                    subscribeMessage.setUnsubscribed(true);
                    return subscribeMessage;
                })
                .orElseThrow(IllegalStateException::new);
        subscribeMessagesLiveData.postValue(subscribeMessagesLiveData.getValue());
        return this;
    }

    public WebSocket sendJson(String url, String payload) {
        addMessage(new SendMessage(url, "application/json", payload));
        return this;
    }

    public WebSocket send(String url, String contentType, String payload) {
        addMessage(new SendMessage(url, contentType, payload));
        return this;
    }

    protected HashMap<String, SubscribeMessage> getSubscribeMessageValue() {
        return Optional.ofNullable(subscribeMessagesLiveData.getValue()).orElse(new HashMap<>());
    }

    protected void addSubscribeMessagesValue(SubscribeMessage subscribeMessage) {
        HashMap<String, SubscribeMessage> subscribeMessageList = getSubscribeMessageValue();
        subscribeMessageList.put(subscribeMessage.getId(), subscribeMessage);
        subscribeMessagesLiveData.postValue(subscribeMessageList);
    }

    protected Boolean getOpenValue() {
        return Optional.ofNullable(openLiveData.getValue()).orElse(false);
    }

    protected LinkedList<SendMessage> getStompMessagesValue() {
        return Optional.ofNullable(messageQueueLiveData.getValue()).orElse(new LinkedList<>());
    }

    protected void addMessage(SendMessage stompMessage) {
        LinkedList<SendMessage> stompMessages = getStompMessagesValue();
        stompMessages.add(stompMessage);
        messageQueueLiveData.postValue(stompMessages);
    }

    protected void sendConnectMessage() {
        String message = "CONNECT" + System.lineSeparator() +
                "accept-version:1.2" + System.lineSeparator() +
                "host:stomp.github.org" + System.lineSeparator() +
                System.lineSeparator() +
                (char) 0;
        ByteString byteMessage = ByteString.encodeString(message,
                StandardCharsets.UTF_8);
        log.info(message + "\n" + byteMessage);
        okHttpWebSocket.send(byteMessage);
    }
}
