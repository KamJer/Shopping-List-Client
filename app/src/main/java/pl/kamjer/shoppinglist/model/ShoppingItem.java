package pl.kamjer.shoppinglist.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity(tableName = "SHOPPING_ITEM",
        foreignKeys = {
                @ForeignKey(
                        entity = AmountType.class,
                        parentColumns = "local_amount_type_id",
                        childColumns = "local_item_amount_type_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "local_category_id",
                        childColumns = "local_item_category_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_name",
                        childColumns = "user_name",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"item_amount_type_id", "shopping_item_id", "item_category_id"}, unique = true)}
)
public class ShoppingItem implements Serializable {
    @ColumnInfo(name = "local_shopping_item_id")
    @PrimaryKey(autoGenerate = true)
    private long localShoppingItemId;
    @ColumnInfo(name = "shopping_item_id")
    private long shoppingItemId;
    @ColumnInfo(name = "item_amount_type_id")
    private long itemAmountTypeId;
    @ColumnInfo(name = "item_category_id")
    private long itemCategoryId;
    @ColumnInfo(name = "local_item_amount_type_id")
    private long localItemAmountTypeId;
    @ColumnInfo(name = "local_item_category_id")
    private long localItemCategoryId;
    @ColumnInfo(name = "item_name")
    private String itemName;
    @ColumnInfo(name = "amount")
    private Double amount;
    @ColumnInfo(name = "bought")
    private boolean bought;
    @ColumnInfo(name = "moved_to_bought")
    private boolean movedToBought;
    @ColumnInfo(name = "user_name")
    private String userName;
    @ColumnInfo(name = "updated")
    private boolean updated;
    @ColumnInfo(name = "deleted")
    private boolean deleted;


}
