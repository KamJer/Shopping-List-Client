package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;

/**
 * ViewHolder class for creating and managing ingredient items in a RecyclerView.
 * Handles the binding of ingredient data to UI elements and manages user input.
 */
public class CreateIngredientViewHolder extends RecyclerView.ViewHolder {

    /**
     * EditText field for entering ingredient name.
     * Displays and allows editing of the ingredient's name.
     */
    private final EditText ingredientNameEt;

    /**
     * EditText field for entering ingredient quantity.
     * Displays and allows editing of the ingredient's quantity.
     */
    private final EditText ingredientQuantityEt;

    /**
     * EditText field for entering ingredient unit.
     * Displays and allows editing of the ingredient's unit of measurement.
     */
    private final EditText ingredientUnitEt;

    /**
     * ImageButton for deleting the ingredient.
     * Triggers the deletion of the ingredient item when clicked.
     */
    private final ImageButton ingredientDeleteBtn;

    /**
     * Ingredient object associated with this ViewHolder.
     * Holds the data for the ingredient being displayed and edited.
     */
    private Ingredient ingredient;

    /**
     * Constructor for CreateIngredientViewHolder.
     * Initializes all UI elements and sets up text change listeners.
     *
     * @param itemView View containing the ingredient item layout
     */
    public CreateIngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        this.ingredientNameEt = itemView.findViewById(R.id.et_ingredient_name);
        this.ingredientQuantityEt = itemView.findViewById(R.id.et_ingredient_quantity);
        this.ingredientUnitEt = itemView.findViewById(R.id.et_ingredient_unit);
        this.ingredientDeleteBtn = itemView.findViewById(R.id.ib_delete_ingredient);

        setupListeners();
    }

    /**
     * Binds ingredient data to the ViewHolder's UI elements.
     * Populates the EditText fields with ingredient information and sets up delete button action.
     *
     * @param ingredient       Ingredient object containing the data to display
     * @param deleteBtnAction  Click listener for the delete button
     */
    public void bind(Ingredient ingredient, View.OnClickListener deleteBtnAction) {
        this.ingredient = ingredient;

        ingredientNameEt.setText(ingredient.getName());
        ingredientQuantityEt.setText(String.format(Locale.getDefault(), Optional.ofNullable(ingredient.getQuantity()).orElse(0.0).toString()));
        ingredientUnitEt.setText(ingredient.getUnit());
        ingredientDeleteBtn.setOnClickListener(deleteBtnAction);
    }

    /**
     * Sets up text change listeners for all ingredient EditText fields.
     * Updates the ingredient object with user input as the user types.
     */
    private void setupListeners() {
        ingredientNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ingredient.setName(s.toString());
            }
        });
        ingredientQuantityEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    ingredient.setQuantity(Double.parseDouble(s.toString()));
                }
            }
        });
        ingredientUnitEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ingredient.setUnit(s.toString());
            }
        });
    }
}
