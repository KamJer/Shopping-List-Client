package pl.kamjer.shoppinglist.model.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.recipe.Recipe;

@AllArgsConstructor
@Builder
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

    public static RecipeDto map(Recipe recipe) {
        return RecipeDto.builder()
                .recipeId(recipe.getRecipeId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients().stream().map(IngredientDto::map).collect(Collectors.toList()))
                .steps(recipe.getSteps().stream().map(StepDto::map).collect(Collectors.toList()))
                .tags(recipe.getTags().stream().map(TagDto::map).collect(Collectors.toList()))
                .source(recipe.getSource())
                .published(recipe.getPublished())
                .build();

    }
}
