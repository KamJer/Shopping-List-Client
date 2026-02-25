package pl.kamjer.shoppinglist.model.recipe;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.IngredientDto;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.user.User;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Ingredient implements Serializable {
    private Long ingredientId;
    private String name;
    private Double quantity;
    private String unit;

    public static Ingredient map(IngredientDto ingredientDto) {
        return Ingredient.builder()
                .name(ingredientDto.getName())
                .quantity(ingredientDto.getQuantity())
                .unit(ingredientDto.getUnit())
                .build();
    }

    public static ShoppingItem ingredientTopShoppingItem(Ingredient ingredient, AmountType amountType, Category category, User user) {
        return ShoppingItem.builder()
                .itemCategoryId(category.getCategoryId())
                .itemAmountTypeId(amountType.getAmountTypeId())
                .localItemAmountTypeId(amountType.getLocalAmountTypeId())
                .localItemCategoryId(category.getLocalCategoryId())
                .itemName(ingredient.getName())
                .amount(ingredient.getQuantity())
                .bought(false)
                .movedToBought(false)
                .userName(user.getUserName())
                .updated(false)
                .deleted(false)
                .build();
    }
}
