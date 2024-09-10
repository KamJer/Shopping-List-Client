package pl.kamjer.shoppinglist.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import pl.kamjer.shoppinglist.database.converter.LocalDateTimeStringConverter;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.User;

@Database(entities = {ShoppingItem.class, AmountType.class, Category.class, User.class}, version = 1)
@TypeConverters({LocalDateTimeStringConverter.class})
public abstract class ShoppingDatabase extends RoomDatabase {

    public abstract ShoppingItemDao getShoppingItemDao();
    public abstract CategoryDao getCategoryDao();
    public abstract AmountTypeDao getAmountTypeDao();
    public abstract UtilDao getUtilDao();
    public abstract UserDao getUserDao();

    public static final String DATABASE_NAME = "Shopping_database";
}
