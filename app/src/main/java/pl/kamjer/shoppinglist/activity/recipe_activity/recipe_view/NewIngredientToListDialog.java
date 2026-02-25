package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.NewShoppingItemDialog;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

/**
 * Dialog class for adding ingredients to shopping list from recipe view.
 * Extends NewShoppingItemDialog to provide ingredient-specific functionality.
 */
public class NewIngredientToListDialog extends NewShoppingItemDialog {

    /**
     * ViewModel for managing recipe data.
     * Handles the lifecycle and data operations for recipes.
     */
    private RecipeViewModel recipeViewModel;

    /**
     * Key for passing ingredient data through intent.
     */
    public static final String INGREDIENT_FIELD_NAME = "ingredientField";

    /**
     * Ingredient object that will be added to shopping list.
     */
    public Ingredient ingredient;

    /**
     * Called when the dialog is created.
     * Initializes the dialog with ingredient data from intent.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Optional.ofNullable((Ingredient) getIntent().getSerializableExtra(INGREDIENT_FIELD_NAME))
                .ifPresent(ingredient -> {
                    recipeViewModel.setActiveIngredientValue(ingredient);
                    this.ingredient = ingredient;
                    setupObservers();
                });
    }

    /**
     * Sets up observers for recipe data.
     */
    private void setupObservers() {
        recipeViewModel.setUpActiveIngredientObserver(this, this::setupEditText);
    }

    /**
     * Initializes the ViewModel for recipe data.
     */
    @Override
    protected void initViewModel() {
        super.initViewModel();
        recipeViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
        recipeViewModel.initialize();
    }

    /**
     * Sets up the text fields with ingredient data.
     *
     * @param ingredient Ingredient object containing data to display
     */
    private void setupEditText(Ingredient ingredient) {
        shoppingItemEditText.setText(ingredient.getName());
        amountEditText.setText(Optional.ofNullable(ingredient.getQuantity()).map(Object::toString).orElse(""));
    }

    /**
     * Sets the selection in the amount type spinner based on ingredient unit.
     *
     * @param ingredient   Ingredient object containing unit information
     * @param amountTypes  List of available amount types
     */
    private void setAmountTypeSelection(Ingredient ingredient, List<AmountType> amountTypes) {
        amountTypeSpinner.setSelection(amountTypes.stream()
                .filter(amountType -> amountType.getTypeName().equals(ingredient.getUnit()))
                .findFirst()
                .map(amountTypes::indexOf)
                .orElseGet(() -> {
                    Toast.makeText(this, getString(R.string.no_selected_unit_exists_mssg),
                            Toast.LENGTH_SHORT).show();
                    return 0;
                }));
    }

    /**
     * Sets up the amount type spinner action and selects the appropriate unit.
     *
     * @param amountTypes List of available amount types
     */
    @Override
    protected void setupAmountTypeSpinnerAction(List<AmountType> amountTypes) {
        super.setupAmountTypeSpinnerAction(amountTypes);
        setAmountTypeSelection(ingredient, amountTypes);
    }

    /**
     * Sets up the click listener for the dialog.
     * Reloads the active recipe after adding to shopping list.
     */
    @Override
    protected void setOnclickListener() {
        super.setOnclickListener();
        recipeViewModel.reloadActiveRecipe();
    }
}
