package pl.kamjer.shoppinglist.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.IngredientDto;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Ingredient {
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
}
