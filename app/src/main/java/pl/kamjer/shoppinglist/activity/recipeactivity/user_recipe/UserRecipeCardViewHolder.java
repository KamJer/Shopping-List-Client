package pl.kamjer.shoppinglist.activity.recipeactivity.user_recipe;

import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview.RecipeViewHolder;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.EditRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;

public class UserRecipeCardViewHolder extends RecipeViewHolder {

    private final ImageButton btnEditRecipe;
    private final ImageButton btnDeleteRecipe;

    public UserRecipeCardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.btnEditRecipe = itemView.findViewById(R.id.btn_user_edit_recipe_id);
        this.btnDeleteRecipe = itemView.findViewById(R.id.btn_user_delete_recipe_id);
    }

    public void bind(Recipe recipe, PassActiveRecipe passActiveRecipe, EditRecipeAction editRecipeAction, DeleteRecipeAction deleteRecipeAction) {
        super.bind(recipe, passActiveRecipe);
        btnEditRecipe.setOnClickListener(view -> editRecipeAction.action(recipe));
        btnDeleteRecipe.setOnClickListener(view -> deleteRecipeAction.action(recipe));

    }
}
