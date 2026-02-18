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

public class CreateIngredientViewHolder extends RecyclerView.ViewHolder {

    private final EditText ingredientNameEt;
    private final EditText ingredientQuantityEt;
    private final EditText ingredientUnitEt;
    private final ImageButton ingredientDeleteBtn;

    private Ingredient ingredient;

    public CreateIngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        this.ingredientNameEt = itemView.findViewById(R.id.et_ingredient_name);
        this.ingredientQuantityEt = itemView.findViewById(R.id.et_ingredient_quantity);
        this.ingredientUnitEt = itemView.findViewById(R.id.et_ingredient_unit);
        this.ingredientDeleteBtn = itemView.findViewById(R.id.ib_delete_ingredient);

        setupListeners();
    }

    public void bind(Ingredient ingredient, View.OnClickListener deleteBtnAction) {
        this.ingredient = ingredient;

        ingredientNameEt.setText(ingredient.getName());
        ingredientQuantityEt.setText(String.format(Locale.getDefault(), Optional.ofNullable(ingredient.getQuantity()).orElse(0.0).toString()));
        ingredientUnitEt.setText(ingredient.getUnit());
        ingredientDeleteBtn.setOnClickListener(deleteBtnAction);
    }

    private void setupListeners() {
        ingredientNameEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                ingredient.setName(s.toString());
            }
        });
        ingredientQuantityEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    ingredient.setQuantity(Double.parseDouble(s.toString()));
                }
            }
        });
        ingredientUnitEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                ingredient.setUnit(s.toString());
            }
        });
    }
}
