package pl.kamjer.shoppinglist.model.shopping_list;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.user.User;

@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity(tableName = "AMOUNT_TYPE",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_name",
                        childColumns = "user_name",
                        onDelete = ForeignKey.CASCADE)
        })
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
