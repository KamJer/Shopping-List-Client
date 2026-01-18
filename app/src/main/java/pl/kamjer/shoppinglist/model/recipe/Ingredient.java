package pl.kamjer.shoppinglist.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ingredient {
    private Long ingredientId;
    private String name;
    private Double quantity;
    private String unit;
}
