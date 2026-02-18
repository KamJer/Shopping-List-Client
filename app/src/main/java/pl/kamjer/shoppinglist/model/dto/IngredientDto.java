package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;

@AllArgsConstructor
@Builder
@Getter
public class IngredientDto {
    private String name;
    private Double quantity;
    private String unit;

    public static IngredientDto map(Ingredient ingredient) {
        return IngredientDto.builder()
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .build();
    }
}
