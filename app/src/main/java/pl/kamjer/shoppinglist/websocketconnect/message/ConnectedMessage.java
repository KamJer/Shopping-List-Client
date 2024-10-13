package pl.kamjer.shoppinglist.websocketconnect.message;

import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okio.ByteString;

@AllArgsConstructor
@Getter
public class ConnectedMessage {
    private String sessionId;

}
