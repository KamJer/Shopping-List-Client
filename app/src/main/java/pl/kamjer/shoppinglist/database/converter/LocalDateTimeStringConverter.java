package pl.kamjer.shoppinglist.database.converter;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeStringConverter {

    @TypeConverter
    public static LocalDateTime fromTimestamp(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    @TypeConverter
    public static String dateToTimestamp(LocalDateTime date) {
        return date == null ? null : date.toString();
    }
}
