package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_recycler_view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import pl.kamjer.shoppinglist.model.recipe.Recipe;

public class RecipeComparator extends DiffUtil.ItemCallback<Recipe> {

    @Override
    public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
        return oldItem.equals(newItem);

    }

    @Override
    public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
        return oldItem.equals(newItem);

    }
}
