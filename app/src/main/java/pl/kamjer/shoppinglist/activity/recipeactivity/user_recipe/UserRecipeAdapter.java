package pl.kamjer.shoppinglist.activity.recipeactivity.user_recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.EditRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;

public class UserRecipeAdapter extends PagingDataAdapter<Recipe, UserRecipeCardViewHolder> {

    private PassActiveRecipe passActiveRecipe;
    private EditRecipeAction editRecipeAction;
    private DeleteRecipeAction deleteRecipeAction;

    public UserRecipeAdapter(@NonNull DiffUtil.ItemCallback<Recipe> diffCallback,
                             PassActiveRecipe passActiveRecipe,
                             EditRecipeAction editRecipeAction,
                             DeleteRecipeAction deleteRecipeAction) {
        super(diffCallback);
        this.passActiveRecipe = passActiveRecipe;
        this.editRecipeAction = editRecipeAction;
        this.deleteRecipeAction = deleteRecipeAction;
    }

    @NonNull
    @Override
    public UserRecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_recipe_card_layout, parent, false);
        return new UserRecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecipeCardViewHolder holder, int position) {
        holder.bind(getItem(position), passActiveRecipe, editRecipeAction, deleteRecipeAction);
    }

}
