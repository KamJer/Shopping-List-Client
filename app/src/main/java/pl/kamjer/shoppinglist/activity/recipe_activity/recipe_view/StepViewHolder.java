package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;

/**
 * ViewHolder class for displaying step items in recipe viewing.
 * Manages the binding of step data to UI elements for display.
 */
public class StepViewHolder extends RecyclerView.ViewHolder {

    /**
     * TextView for displaying step number.
     */
    private final TextView stepNumberTextView;

    /**
     * TextView for displaying step description.
     */
    private final TextView stepDescTextView;

    /**
     * Constructor for StepViewHolder.
     *
     * @param itemView View containing the step card layout
     */
    public StepViewHolder(@NonNull View itemView) {
        super(itemView);
        stepNumberTextView = itemView.findViewById(R.id.step_number_id);
        stepDescTextView = itemView.findViewById(R.id.step_instruction_id);
    }

    /**
     * Binds step data to the ViewHolder's UI elements.
     *
     * @param step Step object containing the data to display
     */
    public void bind(Step step) {
        stepNumberTextView.setText(String.format(Locale.getDefault(), step.getStepNumber().toString()));
        stepDescTextView.setText(step.getInstruction());
    }
}
