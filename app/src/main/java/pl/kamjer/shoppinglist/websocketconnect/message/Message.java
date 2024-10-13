package pl.kamjer.shoppinglist.websocketconnect.message;

import com.google.gson.Gson;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Message {

    private Command command;
    private HashMap<Header, String> headers;

    public String jsonyfy() {
        return new Gson().toJson(this);
    }
}
