package pl.kamjer.shoppinglist.websocketconnect.message;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeMessage {
    private String baseUrl;
    private String parameterUrl;
    private boolean send;
    private boolean unsubscribed;
    private Message subscribeMessage;
    private Message unsubscribeMessage;
    private String[] parameters;

    private Type type;

    public SubscribeMessage(String baseUrl, Type type, boolean send, boolean unsubscribed, String... parameters){
        this.baseUrl = baseUrl;
        this.send = send;
        this.unsubscribed = unsubscribed;
        this.type = type;
        this.parameters = parameters;

        if (parameters.length > 0) {
            this.parameterUrl = getSubUrlBuilder(baseUrl, parameters).toString();
        }

        HashMap<Header, String> subHeaders = new HashMap<>();
        subHeaders.put(Header.DEST, baseUrl);
        putParaHeader(subHeaders, parameters);
        this.subscribeMessage = new Message(Command.SUBSCRIBE, subHeaders);

        HashMap<Header, String> unsubHeaders = new HashMap<>();
        unsubHeaders.put(Header.DEST, baseUrl);
        putParaHeader(unsubHeaders, parameters);
        this.unsubscribeMessage = new Message(Command.UNSUBSCRIBE, unsubHeaders);
    }

    private static StringBuilder getSubUrlBuilder(String dest, String[] parameters) {
        String[] urlElements = dest.split("/");
        int parameterCount = 0;
        StringBuilder subUrlBuilder = new StringBuilder();
//            beginning of a url element
        for (int i = 0; i < urlElements.length; i++) {

//                if element of url is parameter replace it with passed parameter
            if (urlElements[i].startsWith("{") && urlElements[i].endsWith("}")) {
                subUrlBuilder.append(parameters[parameterCount]);
                parameterCount ++;
            } else {
//                    if element is not parameter add it back at its place
                subUrlBuilder.append(urlElements[i]);
            }
            if (i < urlElements.length - 1) {
//                end of a url element with the exception of a last one
                subUrlBuilder.append("/");
            }
        }
        return subUrlBuilder;
    }

    private void putParaHeader(HashMap<Header, String> header, String... parameters) {
        StringBuilder parameterBuilder = new StringBuilder();
        for (String parameter: parameters) {
            parameterBuilder.append(parameter).append(";");
        }
        header.put(Header.PARA, parameterBuilder.toString());
    }

    public String getSubscribeMessage() {
        return new Gson().toJson(subscribeMessage);
    }

    public String getUnsubscribeMessage() {
        return new Gson().toJson(subscribeMessage);
    }

    public String getSubscribeUrl() {
        if (parameterUrl == null) {
            return baseUrl;
        } else {
            return parameterUrl;
        }
    }
}
