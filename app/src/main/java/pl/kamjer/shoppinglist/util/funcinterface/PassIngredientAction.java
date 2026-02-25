package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.recipe.Ingredient;

@FunctionalInterface
public interface PassIngredientAction {
    void action(Ingredient ingredient);
}
