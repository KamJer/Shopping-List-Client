package pl.kamjer.shoppinglist.database.threadfactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShoppingListDataBaseThreadFactory implements ThreadFactory {
    private final ThreadFactory defaultFactory =    Executors.defaultThreadFactory();
    private final Thread.UncaughtExceptionHandler exceptionHandler;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = defaultFactory.newThread(r);
        thread.setUncaughtExceptionHandler(exceptionHandler);
        return thread;
    }
}
