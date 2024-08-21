package pl.kamjer.shoppinglist.util.exception.handler;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ShoppingListExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        System.exit(2);
    }
}
