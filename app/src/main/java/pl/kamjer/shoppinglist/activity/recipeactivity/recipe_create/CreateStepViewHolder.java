package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;

/**
 * ViewHolder implementation for creating and managing recipe step items in a RecyclerView.
 * This class handles UI binding, data synchronization, and user input processing for individual
 * recipe steps within the recipe creation flow.
 */
public class CreateStepViewHolder extends RecyclerView.ViewHolder {

    /**
     * EditText field for entering the step number.
     * Displays and allows editing of the sequential number of the recipe step.
     */
    private final EditText etStepNumber;

    /**
     * EditText field for entering the step instruction.
     * Contains the textual description of what needs to be done in this step.
     */
    private final EditText etStepDesc;

    /**
     * ImageButton for deleting the current step.
     * Provides user interaction to remove a step from the recipe.
     */
    private final ImageButton deleteStepBtn;

    /**
     * Model object representing the current recipe step being displayed and edited.
     * Maintains the state of the step data throughout the lifecycle of this ViewHolder.
     */
    private Step step;

    /**
     * Constructor for the ViewHolder.
     * Initializes all UI components and sets up text change listeners for data binding.
     *
     * @param itemView The root view of the step item layout
     */
    public CreateStepViewHolder(@NonNull View itemView) {
        super(itemView);
        etStepNumber = itemView.findViewById(R.id.et_step_number_id);
        etStepDesc = itemView.findViewById(R.id.et_step_instruction_id);
        deleteStepBtn = itemView.findViewById(R.id.delete_step_btn);

        setupListeners();
    }

    /**
     * Binds step data to the ViewHolder's UI components.
     * Sets up the initial values for step number and instruction, and attaches the delete button listener.
     *
     * @param step The Step model object containing the step data to display
     * @param position The current position in the RecyclerView (used for default step numbering)
     * @param deleteBtnAction The click listener to handle step deletion
     */
    public void bind(Step step, int position, View.OnClickListener deleteBtnAction) {
        this.step = step;
        etStepNumber.setText(String.format(Locale.getDefault(), Optional.ofNullable(step.getStepNumber()).orElse(++position).toString()));
        etStepDesc.setText(step.getInstruction());
        deleteStepBtn.setOnClickListener(deleteBtnAction);
    }

    /**
     * Sets up text change listeners for both input fields to synchronize user input with the model.
     * Ensures that changes in the UI are immediately reflected in the underlying Step object.
     */
    private void setupListeners() {
        etStepNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    step.setStepNumber(Integer.parseInt(s.toString()));
                }
            }
        });
        etStepDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                step.setInstruction(s.toString());
            }
        });
    }
}
