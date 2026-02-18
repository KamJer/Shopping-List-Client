package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.recipe.Recipe;

@FunctionalInterface
public interface DeleteRecipeAction {

    void action(Recipe recipe);
}
