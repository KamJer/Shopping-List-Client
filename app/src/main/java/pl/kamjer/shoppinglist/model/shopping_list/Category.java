package pl.kamjer.shoppinglist.model.shopping_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
@Getter
@Setter
@Entity(tableName = "CATEGORY",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_name",
                        childColumns = "user_name",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class Category implements Serializable {

    public Category() {
    }

    @ColumnInfo(name = "local_category_id")
    @PrimaryKey(autoGenerate = true)
    private long localCategoryId;
    @ColumnInfo(name = "category_id")
    private long categoryId;
    @ColumnInfo(name = "category_name")
    private String categoryName;
    @ColumnInfo(name = "user_name")
    private String userName;
    @ColumnInfo(name = "updated")
    private boolean updated;
    @ColumnInfo(name = "deleted")
    private boolean deleted;
//   how sorted category should be displayed
    @ColumnInfo(name="index")
    private int index;

    @Override
    public int hashCode() {
        return (int) localCategoryId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Category && obj.hashCode() == this.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return categoryName;
    }

}
