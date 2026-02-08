package pl.kamjer.shoppinglist.activity.recipeactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;

@AllArgsConstructor
public class StepAdapter extends RecyclerView.Adapter<StepViewHolder>{

    private List<Step> stepList;

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_card_layout, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.bind(stepList.get(position));
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }
}
