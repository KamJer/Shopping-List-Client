package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IngredientDto {
    private String name;
    private Double quantity;
    private String unit;
}
