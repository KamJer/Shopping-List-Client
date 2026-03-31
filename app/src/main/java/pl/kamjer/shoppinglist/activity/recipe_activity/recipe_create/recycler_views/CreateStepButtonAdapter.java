package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;

/**
 * Adapter class for managing the "Create Step" button in a RecyclerView for recipe creation.
 * Handles the creation and binding of the button ViewHolder.
 */
@AllArgsConstructor
public class CreateStepButtonAdapter extends RecyclerView.Adapter<CreateStepButtonViewHolder> {

    /**
     * Click listener for the create step button.
     * Handles the action to be performed when the "Create Step" button is clicked.
     */
    private View.OnClickListener createStepBtnAction;

    /**
     * Creates a new ViewHolder instance for the create step button.
     *
     * @param parent   Parent ViewGroup that will contain the ViewHolder
     * @param viewType View type of the ViewHolder
     * @return New CreateStepButtonViewHolder instance
     */
    @NonNull
    @Override
    public CreateStepButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_step_button_card_layout, parent, false);
        return new CreateStepButtonViewHolder(view, createStepBtnAction);
    }

    /**
     * Binds data to the ViewHolder (no data binding needed for a button).
     *
     * @param holder   CreateStepButtonViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull CreateStepButtonViewHolder holder, int position) {
        // No data binding needed for button
    }

    /**
     * Returns the total number of items (always 1 for the create step button).
     *
     * @return Number of items (1 button)
     */
    @Override
    public int getItemCount() {
        return 1;
    }
}
