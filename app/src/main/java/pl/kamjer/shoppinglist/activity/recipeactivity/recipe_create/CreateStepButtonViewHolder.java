package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

public class CreateStepButtonViewHolder extends RecyclerView.ViewHolder {

    private final Button btnAddStep;

    public CreateStepButtonViewHolder(@NonNull View itemView, View.OnClickListener addStepBtnAction) {
        super(itemView);
        this.btnAddStep = itemView.findViewById(R.id.btnAddStep);
        btnAddStep.setOnClickListener(addStepBtnAction);
    }
}
