package pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipeactivity.RecipeActivity;
import pl.kamjer.shoppinglist.model.recipe.Recipe;

public class RecipeViewHolder extends RecyclerView.ViewHolder{

    private final TextView recipeDesc;
    private final TextView recipeTitle;
    private Recipe recipe;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        recipeDesc = itemView.findViewById(R.id.recipe_desc_id);
        recipeTitle = itemView.findViewById(R.id.recipe_name_id);
    }

    public void bind(Context context, Recipe recipe) {
        recipeTitle.setText(recipe.getName());
        recipeDesc.setText(recipe.getDescription());

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("recipe", new Gson().toJson(recipe));
            context.startActivity(intent);
        });
    }
}
