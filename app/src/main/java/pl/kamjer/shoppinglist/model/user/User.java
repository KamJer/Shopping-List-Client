package pl.kamjer.shoppinglist.model.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(tableName = "USER")
public class User {

    @ColumnInfo(name = "user_name")
    @PrimaryKey
    @NonNull
    @EqualsAndHashCode.Include
    private String userName;
    @ColumnInfo(name = "password")
    @EqualsAndHashCode.Include
    private String password;
    @ColumnInfo(name = "saved_time")
    private LocalDateTime savedTime;
}
