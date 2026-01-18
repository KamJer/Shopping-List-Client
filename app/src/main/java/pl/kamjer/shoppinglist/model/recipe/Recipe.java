package pl.kamjer.shoppinglist.model.recipe;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
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
}
