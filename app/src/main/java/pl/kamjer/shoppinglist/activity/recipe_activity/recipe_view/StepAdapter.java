package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;

/**
 * Adapter class for managing step items in a RecyclerView for recipe viewing.
 * Handles the binding of Step objects to StepViewHolder.
 */
@Getter
public class StepAdapter extends RecyclerView.Adapter<StepViewHolder>{

    /**
     * List of Step objects to be displayed in the RecyclerView.
     * Contains all the steps for the recipe being viewed.
     */
    private List<Step> steps = new ArrayList<>();

    /**
     * Creates a new ViewHolder instance for a step item.
     *
     * @param parent   Parent ViewGroup that will contain the ViewHolder
     * @param viewType View type of the ViewHolder
     * @return New StepViewHolder instance
     */
    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_card_layout, parent, false);
        return new StepViewHolder(view);
    }

    /**
     * Binds data from a Step object to a ViewHolder.
     *
     * @param holder   StepViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(steps.get(position));
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

    public void setSteps(List<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }
}
