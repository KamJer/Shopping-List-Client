package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

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

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateStepAdapter extends RecyclerView.Adapter<CreateStepViewHolder> {

    private List<Step> steps = new ArrayList<>();

    @NonNull
    @Override
    public CreateStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_step_card_layout, parent, false);
        return new CreateStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateStepViewHolder holder, int position) {
        holder.bind(steps.get(position), position, view -> {
            steps.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public void setData(List<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }
}
