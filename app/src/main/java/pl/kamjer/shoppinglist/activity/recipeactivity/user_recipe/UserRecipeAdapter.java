package pl.kamjer.shoppinglist.activity.recipeactivity.user_recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.EditRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;

@AllArgsConstructor
public class UserRecipeAdapter extends RecyclerView.Adapter<UserRecipeCardViewHolder>{

    private List<Recipe> recipes;
    private PassActiveRecipe passActiveRecipe;
    private EditRecipeAction editRecipeAction;
    private DeleteRecipeAction deleteRecipeAction;

    @NonNull
    @Override
    public UserRecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_recipe_card_layout, parent, false);
        return new UserRecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecipeCardViewHolder holder, int position) {
        holder.bind(recipes.get(position), passActiveRecipe, editRecipeAction, deleteRecipeAction);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
