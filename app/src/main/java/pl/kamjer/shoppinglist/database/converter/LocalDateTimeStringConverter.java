package pl.kamjer.shoppinglist.database.converter;

import androidx.room.TypeConverter;

import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalDateTime;
import java.util.Locale;

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
