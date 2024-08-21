package pl.kamjer.shoppinglist.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.kamjer.shoppinglist.database.converter.LocalDateTimeStringConverter;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.service.UtilService;

@Database(entities = {ShoppingItem.class, AmountType.class, Category.class, User.class}, version = 1)
@TypeConverters({LocalDateTimeStringConverter.class})
public abstract class ShoppingDatabase extends RoomDatabase {

    public abstract ShoppingItemDao getShoppingItemDao();
    public abstract CategoryDao getCategoryDao();
    public abstract AmountTypeDao getAmountTypeDao();
    public abstract UtilDao getUtilDao();
    public abstract UserDao getUserDao();

    public static final String DATABASE_NAME = "Shopping_database";
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
}
