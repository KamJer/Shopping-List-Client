package pl.kamjer.shoppinglist.util.exception.handler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

@RequiredArgsConstructor
public class DatabaseAndServiceOperationExceptionHandler implements Thread.UncaughtExceptionHandler{

    private final Context context;
    private final ShoppingServiceRepository shoppingServiceRepository;

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.e("ShoppingListDB", "Uncaught exception in background thread: " + t.getName(), e);
        try {
            String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } catch (Exception ignored) {}
        if (shoppingServiceRepository != null) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                shoppingServiceRepository.sendLog(ServiceUtil.toExceptionDto(e), latch::countDown);
                latch.await(2, TimeUnit.SECONDS);
            } catch (Exception ignored) {}
        }
        Thread.currentThread().interrupt();
    }
}
