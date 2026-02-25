package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;
import pl.kamjer.shoppinglist.util.funcinterface.PassIngredientAction;

/**
 * Adapter class for managing ingredient items in a RecyclerView for recipe viewing.
 * Handles the binding of Ingredient objects to IngredientViewHolder.
 */
@RequiredArgsConstructor
public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder>{

    /**
     * List of IngredientDataHolder objects to be displayed in the RecyclerView.
     * Contains all the ingredients for the recipe being viewed along with their shopping list status.
     */
    private List<IngredientDataHolder> ingredients = new ArrayList<>();

    /**
     * Action to be performed when an ingredient is clicked.
     * Typically used to create a new shopping item from the ingredient.
     */
    private final PassIngredientAction passIngredientAction;

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
     * Binds data from an IngredientDataHolder object to a ViewHolder.
     *
     * @param holder   IngredientViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position), passIngredientAction);
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

    /**
     * Sets the list of ingredient data holders and notifies the adapter that the data has changed.
     *
     * @param ingredientDataHolders List of ingredient data holders to display
     */
    public void setIngredientDataHolders(List<IngredientDataHolder> ingredientDataHolders) {
        ingredients = ingredientDataHolders;
        notifyDataSetChanged();
    }

    /**
     * Data holder class for ingredient information and shopping list status.
     * Contains an ingredient and a boolean indicating whether it's already on the shopping list.
     */
    public record IngredientDataHolder(Ingredient ingredient, boolean onTheList){
    }
}
