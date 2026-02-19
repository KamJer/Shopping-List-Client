package pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;

public class RecipeRecyclerViewAdapter extends PagingDataAdapter<Recipe, RecipeViewHolder> {

    private PassActiveRecipe passActiveRecipe;

    public RecipeRecyclerViewAdapter(@NonNull DiffUtil.ItemCallback<Recipe> diffCallback, PassActiveRecipe passActiveRecipe) {
        super(diffCallback);
        this.passActiveRecipe = passActiveRecipe;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        if (getItem(position) != null) {
            holder.bind(getItem(position), passActiveRecipe);
        }
    }

    public static class RecipeComparator extends DiffUtil.ItemCallback<Recipe> {

        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.equals(newItem);

        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.equals(newItem);

        }
    }
}
