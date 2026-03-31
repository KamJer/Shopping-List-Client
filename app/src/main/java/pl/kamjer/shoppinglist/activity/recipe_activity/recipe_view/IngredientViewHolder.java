package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.util.funcinterface.PassIngredientAction;

/**
 * ViewHolder class for displaying ingredient items in recipe viewing.
 * Manages the binding of ingredient data to UI elements for display.
 */
public class IngredientViewHolder extends RecyclerView.ViewHolder {

    /**
     * TextView for displaying ingredient name.
     * Shows the name of the ingredient.
     */
    private TextView tvName;

    /**
     * TextView for displaying ingredient quantity.
     * Shows the quantity of the ingredient.
     */
    private TextView tvQuantity;

    /**
     * TextView for displaying ingredient unit.
     * Shows the unit of measurement for the ingredient.
     */
    private TextView tvUnit;

    private CheckBox importedToTheListCheckBox;

    private CheckBox importedToBoughtTheListCheckBox;

    private ImageButton importToTheListimageButton;

    /**
     * Constructor for IngredientViewHolder.
     *
     * @param itemView View containing the ingredient card layout
     */
    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvQuantity = itemView.findViewById(R.id.et_ingredient_quantity);
        tvUnit = itemView.findViewById(R.id.et_ingredient_unit);
        importedToBoughtTheListCheckBox = itemView.findViewById(R.id.OnListBoughtCheckBox);
        importedToTheListCheckBox = itemView.findViewById(R.id.OnListCheckBox);
        importToTheListimageButton = itemView.findViewById(R.id.exportIngredientToList);
    }

    /**
     * Binds ingredient data to the ViewHolder's UI elements.
     *
     * @param ingredient Ingredient object containing the data to display
     */
    public void bind(IngredientAdapter.IngredientDataHolder ingredient, PassIngredientAction action) {
        tvName.setText(ingredient.ingredient().getName());
        tvQuantity.setText(String.format(Locale.getDefault(), Optional.ofNullable(ingredient.ingredient().getQuantity()).orElse(0.0).toString()));
        tvUnit.setText(ingredient.ingredient().getUnit());
        importedToBoughtTheListCheckBox.setChecked(ingredient.onTheListBought());
        importedToTheListCheckBox.setChecked(ingredient.onTheListToBuy());
        importToTheListimageButton.setOnClickListener(view -> action.action(ingredient.ingredient()));
    }
}
