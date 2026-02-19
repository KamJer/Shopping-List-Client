package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;

/**
 * RecyclerView adapter for managing the "Add Ingredient" button in the recipe creation flow.
 * This adapter displays a single button that allows users to add new ingredients to the recipe.
 */
@AllArgsConstructor
public class CreateIngredientButtonAdapter extends RecyclerView.Adapter<CreateIngredientButtonViewHolder> {

    /**
     * Click listener for the add ingredient button.
     * Handles the action triggered when the user clicks the "Add Ingredient" button.
     */
    private View.OnClickListener addIngredientBtnAction;

    /**
     * Creates a new ViewHolder instance for the add ingredient button.
     * Inflates the button layout and initializes the ViewHolder with the click listener.
     *
     * @param parent The parent ViewGroup that will contain the ViewHolder
     * @param viewType The view type of the new ViewHolder
     * @return A new CreateIngredientButtonViewHolder instance
     */
    @NonNull
    @Override
    public CreateIngredientButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_ingredient_button_card_layout, parent, false);
        return new CreateIngredientButtonViewHolder(view, addIngredientBtnAction);
    }

    /**
     * Binds the button to the ViewHolder.
     * This method is currently empty as the button requires no data binding.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull CreateIngredientButtonViewHolder holder, int position) {

    }

    /**
     * Returns the number of items in the data set.
     * Since this adapter manages a single add ingredient button, it always returns 1.
     *
     * @return The total number of items in this adapter
     */
    @Override
    public int getItemCount() {
        return 1;
    }
}
