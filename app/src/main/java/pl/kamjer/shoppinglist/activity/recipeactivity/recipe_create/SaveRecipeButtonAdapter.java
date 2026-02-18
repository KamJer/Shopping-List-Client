package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;

@AllArgsConstructor
public class SaveRecipeButtonAdapter extends RecyclerView.Adapter<SaveRecipeButtonViewHolder> {

    private View.OnClickListener saveRecipeButtonAction;

    @NonNull
    @Override
    public SaveRecipeButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_raciepe_button_card_layout, parent, false);
        return new SaveRecipeButtonViewHolder(view, saveRecipeButtonAction);
    }

    @Override
    public void onBindViewHolder(@NonNull SaveRecipeButtonViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
