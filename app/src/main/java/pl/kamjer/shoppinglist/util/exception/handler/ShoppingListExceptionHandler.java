package pl.kamjer.shoppinglist.util.exception.handler;

import android.content.Context;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

@RequiredArgsConstructor
public class ShoppingListExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "ShoppingListCrash";
    private static final long LOG_SEND_TIMEOUT_MS = 2000;

    private final Context context;
    private final ShoppingServiceRepository shoppingServiceRepository;

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.e(TAG, "Uncaught exception in thread: " + t.getName(), e);

        showToast(e);
        sendCrashLogSync(e);

        Process.killProcess(Process.myPid());
        System.exit(1);
    }

    private void showToast(Throwable e) {
        try {
            String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } catch (Exception ignored) {}
    }

    private void sendCrashLogSync(Throwable e) {
        if (shoppingServiceRepository == null) {
            return;
        }
        try {
            CountDownLatch latch = new CountDownLatch(1);
            shoppingServiceRepository.sendLog(ServiceUtil.toExceptionDto(e), latch::countDown);
            latch.await(LOG_SEND_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {}
    }
}
