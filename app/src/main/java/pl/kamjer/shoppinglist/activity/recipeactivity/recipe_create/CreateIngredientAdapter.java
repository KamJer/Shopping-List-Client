package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;

/**
 * RecyclerView adapter for managing recipe ingredients in the recipe creation flow.
 * This adapter handles the display, editing, and removal of individual ingredients
 * within a dynamic ingredient list for recipe creation.
 */
@Getter
public class CreateIngredientAdapter extends RecyclerView.Adapter<CreateIngredientViewHolder> {

    /**
     * List containing all ingredient objects to be displayed in the RecyclerView.
     * Maintains the current state of ingredients for the recipe being created.
     */
    private List<Ingredient> ingredients = new ArrayList<>();

    /**
     * Creates a new ViewHolder instance for an ingredient card.
     * Inflates the ingredient layout and initializes the ViewHolder for display.
     *
     * @param parent The parent ViewGroup that will contain the ViewHolder
     * @param viewType The view type of the new ViewHolder
     * @return A new CreateIngredientViewHolder instance
     */
    @NonNull
    @Override
    public CreateIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_ingredient_card_layout, parent, false);
        return new CreateIngredientViewHolder(view);
    }

    /**
     * Binds ingredient data to the ViewHolder's UI components and sets up deletion functionality.
     * Configures each ingredient card with its corresponding data and attaches a delete listener
     * that removes the ingredient from the list and updates the RecyclerView.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull CreateIngredientViewHolder holder, int position) {
        holder.bind(ingredients.get(position), view -> {
            ingredients.remove(position);
            notifyItemRemoved(position);
        });
    }

    /**
     * Returns the number of items in the ingredients list.
     * This determines how many ingredient cards will be displayed in the RecyclerView.
     *
     * @return The total number of ingredients in this adapter
     */
    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    /**
     * Updates the adapter's ingredient list with new data and notifies the RecyclerView of changes.
     * This method allows external components to set or replace the entire ingredient list.
     *
     * @param ingredients The new list of ingredients to display
     */
    public void setData(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }
}
