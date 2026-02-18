package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.R;

@AllArgsConstructor
@Setter
public class CreateIngredientButtonAdapter extends RecyclerView.Adapter<CreateIngredientButtonViewHolder> {

    private View.OnClickListener addIngredientBtnAction;

    @NonNull
    @Override
    public CreateIngredientButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_ingredient_button_card_layout, parent, false);
        return new CreateIngredientButtonViewHolder(view, addIngredientBtnAction);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateIngredientButtonViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
