package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;

/**
 * Adapter class for managing ingredient items in a RecyclerView for recipe viewing.
 * Handles the binding of Ingredient objects to IngredientViewHolder.
 */
@AllArgsConstructor
public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder>{

    /**
     * List of Ingredient objects to be displayed in the RecyclerView.
     * Contains all the ingredients for the recipe being viewed.
     */
    private List<Ingredient> ingredients;

    /**
     * Creates a new ViewHolder instance for an ingredient item.
     *
     * @param parent   Parent ViewGroup that will contain the ViewHolder
     * @param viewType View type of the ViewHolder
     * @return New IngredientViewHolder instance
     */
    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_card_layout, parent, false);
        return new IngredientViewHolder(view);
    }

    /**
     * Binds data from an Ingredient object to a ViewHolder.
     *
     * @param holder   IngredientViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position));
    }

    /**
     * Returns the total number of items in the ingredient list.
     *
     * @return Number of ingredients in the list
     */
    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
