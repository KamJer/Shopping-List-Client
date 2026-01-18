package pl.kamjer.shoppinglist.model.shopping_list;

import androidx.room.Embedded;
import androidx.room.Relation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.user.User;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ShoppingItemWithAmountTypeAndCategory {

    @Embedded
    private ShoppingItem shoppingItem;

    @Relation(
            parentColumn = "local_item_amount_type_id",
            entityColumn = "local_amount_type_id"
    )
    private AmountType amountType;

    @Relation(
            parentColumn = "local_item_category_id",
            entityColumn = "local_category_id"
    )
    private Category category;

    @Relation(
            parentColumn = "user_name",
            entityColumn = "user_name"
    )
    private User user;

}
