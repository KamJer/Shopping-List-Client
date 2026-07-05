package pl.kamjer.shoppinglist.model.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.model.recipe.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;

@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Recipe {
    private Long recipeId;
    private String name;
    private String description;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private Set<Tag> tags;
    private String source;
    private Boolean published;

    public Recipe() {
        name = "";
        description = "";
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        tags = new HashSet<>();
        source = "";
        published = false;
    }

    public static Recipe map(RecipeDto recipeDto) {
        return Recipe.builder()
                .name(recipeDto.getName())
                .description(recipeDto.getDescription())
                .ingredients(recipeDto.getIngredients().stream().map(Ingredient::map).collect(Collectors.toList()))
                .steps(recipeDto.getSteps().stream().map(Step::map).collect(Collectors.toList()))
                .tags(recipeDto.getTags().stream().map(t -> Tag.builder().tag(t).build()).collect(Collectors.toSet()))
                .recipeId(recipeDto.getRecipeId())
                .source(Optional.ofNullable(recipeDto.getSource()).orElse(""))
                .published(recipeDto.getPublished())
                .build();
    }
}
