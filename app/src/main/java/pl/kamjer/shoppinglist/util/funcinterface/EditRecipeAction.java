package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.recipe.Recipe;

@FunctionalInterface
public interface EditRecipeAction {
    void action(Recipe recipe);

}
