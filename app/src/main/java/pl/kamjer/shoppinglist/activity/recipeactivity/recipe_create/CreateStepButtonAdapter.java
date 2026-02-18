package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;

@AllArgsConstructor
public class CreateStepButtonAdapter extends RecyclerView.Adapter<CreateStepButtonViewHolder> {

    private View.OnClickListener createStepBtnAction;

    @NonNull
    @Override
    public CreateStepButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_step_button_card_layout, parent, false);
        return new CreateStepButtonViewHolder(view, createStepBtnAction);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateStepButtonViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
