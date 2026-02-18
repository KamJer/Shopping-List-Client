package pl.kamjer.shoppinglist.model.recipe;

import android.os.Parcelable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Recipe {
    private String name;
    private String description;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private Set<Tag> tags;
    private Long recipeId;
    private String source;
    private Boolean published;

    public static Recipe map(RecipeDto recipeDto) {
        return Recipe.builder()
                .name(recipeDto.getName())
                .description(recipeDto.getDescription())
                .ingredients(recipeDto.getIngredients().stream().map(Ingredient::map).collect(Collectors.toList()))
                .steps(recipeDto.getSteps().stream().map(Step::map).collect(Collectors.toList()))
                .tags(recipeDto.getTags().stream().map(Tag::map).collect(Collectors.toSet()))
                .recipeId(recipeDto.getRecipeId())
                .source(Optional.ofNullable(recipeDto.getSource()).orElse(""))
                .published(recipeDto.getPublished())
                .build();
    }
}
