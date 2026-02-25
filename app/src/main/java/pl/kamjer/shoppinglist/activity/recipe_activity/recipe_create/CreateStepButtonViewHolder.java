package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

/**
 * ViewHolder class for the "Create Step" button in recipe creation.
 * Manages the button's UI element and its click listener.
 */
public class CreateStepButtonViewHolder extends RecyclerView.ViewHolder {

    /**
     * Button for adding a new step.
     * Triggers the creation of a new step when clicked.
     */
    private final Button btnAddStep;

    /**
     * Constructor for CreateStepButtonViewHolder.
     *
     * @param itemView           View containing the button layout
     * @param addStepBtnAction   Click listener for the add step button
     */
    public CreateStepButtonViewHolder(@NonNull View itemView, View.OnClickListener addStepBtnAction) {
        super(itemView);
        this.btnAddStep = itemView.findViewById(R.id.btnAddStep);
        btnAddStep.setOnClickListener(addStepBtnAction);
    }
}
