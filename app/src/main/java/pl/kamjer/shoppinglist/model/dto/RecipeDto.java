package pl.kamjer.shoppinglist.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecipeDto {
    private Long recipeId;
    private String name;
    private String description;
    private List<IngredientDto> ingredients;
    private List<StepDto> steps;
    private List<TagDto> tags;
    private String source;
    private Boolean published;
}
