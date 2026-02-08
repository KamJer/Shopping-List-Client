package pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;

@AllArgsConstructor
public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipes;

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(context, recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
