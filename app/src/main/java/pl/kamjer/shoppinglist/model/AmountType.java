package pl.kamjer.shoppinglist.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity(tableName = "AMOUNT_TYPE")
public class AmountType implements Serializable {

    @ColumnInfo(name = "local_amount_type_id")
    @PrimaryKey(autoGenerate = true)
    private long localAmountTypeId;
    @ColumnInfo(name = "amount_type_id")
    private long amountTypeId;
    @ColumnInfo(name = "type_name")
    private String typeName;
    @ColumnInfo(name = "user_name")
    private String userName;
    @ColumnInfo(name = "updated")
    private boolean updated;
    @ColumnInfo(name = "deleted")
    private boolean deleted;

    @NonNull
    @Override
    public String toString() {
        return typeName;
    }
}
