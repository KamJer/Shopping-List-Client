package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.recipe.Recipe;

@FunctionalInterface
public interface PassActiveRecipe {
    void passActiveRecipe(Recipe recipe);
}
