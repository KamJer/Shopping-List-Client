package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;

/**
 * Adapter class for managing step items in a RecyclerView for recipe creation.
 * Handles the binding of Step objects to CreateStepViewHolder and manages the step list.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateStepAdapter extends RecyclerView.Adapter<CreateStepViewHolder> {

    /**
     * List of Step objects to be displayed in the RecyclerView.
     * Contains all the steps for the recipe being created or edited.
     */
    private List<Step> steps = new ArrayList<>();

    /**
     * Creates a new ViewHolder instance for a step item.
     *
     * @param parent   Parent ViewGroup that will contain the ViewHolder
     * @param viewType View type of the ViewHolder
     * @return New CreateStepViewHolder instance
     */
    @NonNull
    @Override
    public CreateStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_step_card_layout, parent, false);
        return new CreateStepViewHolder(view);
    }

    /**
     * Binds data from a Step object to a ViewHolder.
     *
     * @param holder   CreateStepViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull CreateStepViewHolder holder, int position) {
        holder.bind(steps.get(position), position, view -> {
            steps.remove(position);
            notifyItemRemoved(position);
        });
    }

    /**
     * Returns the total number of items in the step list.
     *
     * @return Number of steps in the list
     */
    @Override
    public int getItemCount() {
        return steps.size();
    }

    /**
     * Sets the step data and notifies the adapter of data changes.
     *
     * @param steps List of steps to set
     */
    public void setData(List<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }
}
