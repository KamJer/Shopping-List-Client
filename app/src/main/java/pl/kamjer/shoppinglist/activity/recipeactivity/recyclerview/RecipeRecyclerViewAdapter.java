package pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;

@AllArgsConstructor
public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private List<Recipe> recipes;
    private PassActiveRecipe passActiveRecipe;

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position), passActiveRecipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
