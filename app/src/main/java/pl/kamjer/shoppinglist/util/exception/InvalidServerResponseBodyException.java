package pl.kamjer.shoppinglist.util.exception;

public class InvalidServerResponseBodyException extends RuntimeException{

    public InvalidServerResponseBodyException(String message) {
        super(message);
    }

    public InvalidServerResponseBodyException() {
        super("Invalid body of a response from a server, something is wrong with server or application");
    }
}
