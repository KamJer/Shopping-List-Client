package pl.kamjer.shoppinglist.websocketconnect;

import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import okio.ByteString;

@AllArgsConstructor
public class SendMessage extends StompMessage {
    protected String url;
    protected String contentType;
    protected String payload;

    public String getMessage() {
        return "SEND" + System.lineSeparator() +
                "destination:" + url + System.lineSeparator() +
                "content-type:" + contentType + System.lineSeparator() +
                System.lineSeparator() +
                payload + System.lineSeparator()
                + closeStompMassage();
    }

    public ByteString getByteMessage() {
        return ByteString.encodeString(getMessage(), StandardCharsets.UTF_8);
    }
}
