package pl.kamjer.shoppinglist.util.exception.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

@RequiredArgsConstructor
public class DatabaseAndServiceOperationExceptionHandler implements Thread.UncaughtExceptionHandler{

    private final Context context;
    private final ShoppingServiceRepository shoppingServiceRepository;

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
        shoppingServiceRepository.sendLog(ServiceUtil.toExceptionDto(e), () -> {});
        Thread.currentThread().interrupt();
    }
}
