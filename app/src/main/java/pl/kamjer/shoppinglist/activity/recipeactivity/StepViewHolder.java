package pl.kamjer.shoppinglist.activity.recipeactivity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;

public class StepViewHolder extends RecyclerView.ViewHolder {

    private TextView stepNumberTextView;
    private TextView stepDescTextView;

    public StepViewHolder(@NonNull View itemView) {
        super(itemView);
        stepNumberTextView = itemView.findViewById(R.id.step_number_id);
        stepDescTextView = itemView.findViewById(R.id.step_instruction_id);
    }

    public void bind(Step step) {
        stepNumberTextView.setText(String.format(Locale.getDefault(), step.getStepNumber().toString()));
        stepDescTextView.setText(step.getInstruction());
    }
}
