package pl.kamjer.shoppinglist.util.exception;

import androidx.annotation.Nullable;

public class NotOkHttpResponseException extends Throwable{
    public NotOkHttpResponseException(@Nullable String message) {
        super(message);
    }
}
