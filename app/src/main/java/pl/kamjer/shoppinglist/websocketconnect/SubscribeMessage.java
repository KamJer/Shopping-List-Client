package pl.kamjer.shoppinglist.websocketconnect;

import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import okio.ByteString;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;

@AllArgsConstructor
@Getter
@Setter
public class SubscribeMessage extends StompMessage {
    protected String id;
    protected String subscribeUrl;
    protected OnMessageAction onMessageAction;
    protected boolean send;
    protected boolean unsubscribed;

    public String getSubscribeMessage() {
        return ("SUBSCRIBE" + System.lineSeparator() +
                "id:" + id + System.lineSeparator() +
                "destination:" + subscribeUrl + System.lineSeparator() +
                "ack:" + "client" + System.lineSeparator() +
                System.lineSeparator() +
                closeStompMassage());
    }

    public String getUnsubscribeMessage() {
        return "UNSUBSCRIBE" + System.lineSeparator() +
                "id:" + id + System.lineSeparator() +
                System.lineSeparator() +
                closeStompMassage();
    }

    public ByteString getByteSubscribeMessage() {
        return ByteString.encodeString(getSubscribeMessage(), StandardCharsets.UTF_8);
    }
    public ByteString getByteUnsubscribeMessage() {
        return ByteString.encodeString(getUnsubscribeMessage(), StandardCharsets.UTF_8);
    }
}
