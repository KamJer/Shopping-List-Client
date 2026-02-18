package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    private TextView tvName;
    private TextView tvQuantity;
    private TextView tvUnit;

    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvQuantity = itemView.findViewById(R.id.et_ingredient_quantity);
        tvUnit = itemView.findViewById(R.id.et_ingredient_unit);
    }

    public void bind(Ingredient ingredient) {
        tvName.setText(ingredient.getName());
        tvQuantity.setText(String.format(Locale.getDefault(), Optional.ofNullable(ingredient.getQuantity()).orElse(0.0).toString()));
        tvUnit.setText(ingredient.getUnit());
    }
}
