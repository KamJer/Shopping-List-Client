package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;

/**
 * Adapter class for managing the "Save Recipe" button in recipe creation.
 * Handles the creation and binding of the save button ViewHolder.
 */
@AllArgsConstructor
public class SaveRecipeButtonAdapter extends RecyclerView.Adapter<SaveRecipeButtonViewHolder> {

    /**
     * Click listener for the save recipe button.
     * Handles the action to be performed when the "Save Recipe" button is clicked.
     */
    private View.OnClickListener saveRecipeButtonAction;

    /**
     * Creates a new ViewHolder instance for the save recipe button.
     *
     * @param parent   Parent ViewGroup that will contain the ViewHolder
     * @param viewType View type of the ViewHolder
     * @return New SaveRecipeButtonViewHolder instance
     */
    @NonNull
    @Override
    public SaveRecipeButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_raciepe_button_card_layout, parent, false);
        return new SaveRecipeButtonViewHolder(view, saveRecipeButtonAction);
    }

    /**
     * Binds data to the ViewHolder (no data binding needed for a button).
     *
     * @param holder   SaveRecipeButtonViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull SaveRecipeButtonViewHolder holder, int position) {
        // No data binding needed for button
    }

    /**
     * Returns the total number of items (always 1 for the save recipe button).
     *
     * @return Number of items (1 button)
     */
    @Override
    public int getItemCount() {
        return 1;
    }
}
