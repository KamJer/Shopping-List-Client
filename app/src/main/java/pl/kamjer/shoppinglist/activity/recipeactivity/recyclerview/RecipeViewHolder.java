package pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;

public class RecipeViewHolder extends RecyclerView.ViewHolder{

    private final TextView recipeDesc;
    private final TextView recipeTitle;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        recipeDesc = itemView.findViewById(R.id.recipe_desc_id);
        recipeTitle = itemView.findViewById(R.id.recipe_name_id);
    }

    public void bind(Recipe recipe, PassActiveRecipe passActiveRecipe) {
        recipeTitle.setText(recipe.getName());
        recipeDesc.setText(recipe.getDescription());

        itemView.setOnClickListener(view -> {
            passActiveRecipe.passActiveRecipe(recipe);
        });
    }
}
