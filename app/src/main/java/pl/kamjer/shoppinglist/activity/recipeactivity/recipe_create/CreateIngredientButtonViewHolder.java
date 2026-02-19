package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

/**
 * ViewHolder implementation for the "Add Ingredient" button in the recipe creation flow.
 * This ViewHolder manages the UI component for adding new ingredients to a recipe.
 */
public class CreateIngredientButtonViewHolder extends RecyclerView.ViewHolder {

    /**
     * Button widget for adding a new ingredient to the recipe.
     * When clicked, triggers the ingredient addition functionality.
     */
    private Button createIngredientBtn;

    /**
     * Constructor for the ViewHolder.
     * Initializes the button UI component and sets up the click listener.
     *
     * @param itemView The root view of the ingredient button layout
     * @param onClickListener The click listener that handles the add ingredient action
     */
    public CreateIngredientButtonViewHolder(@NonNull View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        createIngredientBtn = itemView.findViewById(R.id.btnAddIngredient);
        createIngredientBtn.setOnClickListener(onClickListener);
    }
}
