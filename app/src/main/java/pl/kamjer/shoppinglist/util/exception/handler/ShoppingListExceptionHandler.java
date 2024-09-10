package pl.kamjer.shoppinglist.util.exception.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

@RequiredArgsConstructor
@Builder
@Getter
@Setter
@Log
public class ShoppingListExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;
    private final ShoppingServiceRepository shoppingServiceRepository;
    private final ExecutorService executorService;

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
        shoppingServiceRepository.sendLog(ServiceUtil.toExceptionDto(e));
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
