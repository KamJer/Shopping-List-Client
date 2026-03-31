package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.model.recipe.Step;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

/**
 * Fragment class for displaying recipe details.
 * Manages the UI elements and data binding for recipe viewing.
 */
public class RecipeFragment extends Fragment {

    /**
     * ViewModel for managing recipe data.
     * Handles the lifecycle and data operations for recipes.
     */
    private RecipeViewModel recipeSearchViewModel;

    /**
     * TextView for displaying recipe title.
     */
    private TextView recipeTitle;

    /**
     * TextView for displaying recipe description.
     */
    private TextView recipeDesc;

    /**
     * TextView for displaying recipe source.
     */
    private TextView recipeSource;

    private TextView ingredientTextView;

    /**
     * RecyclerView for displaying ingredients.
     */
    private RecyclerView ingredientRecyclerView;

    private TextView stepTextView;

    /**
     * RecyclerView for displaying steps.
     */
    private RecyclerView stepRecyclerView;

    private TextView tagTextView;

    /**
     * RecyclerView for displaying tags.
     */
    private RecyclerView tagRecyclerView;

    /**
     * Adapter for displaying ingredients in the RecyclerView.
     */
    private IngredientAdapter ingredientAdapter;

    /**
     * Adapter for displaying steps in the RecyclerView.
     */
    private StepAdapter stepAdapter;

    /**
     * Adapter for displaying tags in the RecyclerView.
     */
    private TagAdapter tagAdapter;

    private CheckBox isPublished;

    /**
     * Creates the view for the fragment.
     *
     * @param inflater           LayoutInflater to inflate the layout
     * @param container          Parent view group
     * @param savedInstanceState Saved instance state
     * @return Inflated view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recipe_fragment_layout, container, false);

        // Load the ViewModel and initialize it
        loadViewModel();

        // Find and initialize all UI elements
        findViews(view);

        // Load and display recipe data
        loadRecipe();

        return view;
    }

    /**
     * Initializes the ViewModel for recipe data.
     * This method creates a new instance of RecipeViewModel and initializes it.
     */
    private void loadViewModel() {
        recipeSearchViewModel = new ViewModelProvider(
                requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
        recipeSearchViewModel.initialize();
    }

    /**
     * Finds and initializes all UI elements.
     *
     * @param view View containing the UI elements
     */
    private void findViews(View view) {
        this.recipeTitle = view.findViewById(R.id.tvRecipeTitle);
        this.recipeDesc = view.findViewById(R.id.tvRecipeDescription);
        this.ingredientRecyclerView = view.findViewById(R.id.rvIngredients);
        this.stepRecyclerView = view.findViewById(R.id.rvSteps);
        this.tagTextView = view.findViewById(R.id.tvTags);
        this.tagRecyclerView = view.findViewById(R.id.rvTags);
        this.recipeSource = view.findViewById(R.id.source_text_view);
        this.isPublished = view.findViewById(R.id.isPublic);
        this.ingredientTextView  = view.findViewById(R.id.tvIngredients);
        this.stepTextView = view.findViewById(R.id.tvSteps);
    }

    /**
     * Loads and displays recipe data.
     * Sets up RecyclerViews with their respective adapters and observes recipe data.
     */
    private void loadRecipe() {
        // Initialize ingredient adapter with click listener to create new shopping items
        ingredientAdapter = new IngredientAdapter(
                ingredient -> {
                    Intent createNewShoppingItemIntent = new Intent(requireContext(), NewIngredientToListDialog.class);
                    createNewShoppingItemIntent.putExtra(NewIngredientToListDialog.INGREDIENT_FIELD_NAME, ingredient);
                    startActivity(createNewShoppingItemIntent);
                });

        // Set up ingredient RecyclerView
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.ingredientRecyclerView.setAdapter(ingredientAdapter);

        // Initialize step adapter
        stepAdapter = new StepAdapter();
        stepRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.stepRecyclerView.setAdapter(stepAdapter);

        // Initialize tag adapter
        tagAdapter = new TagAdapter();
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setAdapter(tagAdapter);

        // Observe recipe and shopping items data to update UI
        recipeSearchViewModel.setRecipeWithShoppingItemsObserver(getViewLifecycleOwner(), pair -> {
            Recipe recipe = pair.first;
            List<ShoppingItem> shoppingItems = pair.second;

            // Update recipe title and description
            recipeTitle.setText(recipe.getName());

            if (recipe.getDescription().isEmpty()) {
                recipeDesc.setVisibility(View.GONE);
            } else {
                recipeDesc.setVisibility(View.VISIBLE);
                recipeDesc.setText(recipe.getDescription());
            }

            isPublished.setChecked(recipe.getPublished());

            // Set ingredients data
            setIngredients(recipe, shoppingItems);
            if (recipe.getIngredients().isEmpty()) {
                ingredientTextView.setVisibility(View.GONE);
            } else {
                ingredientTextView.setVisibility(View.VISIBLE);
            }
            // Set steps data
            setSteps(recipe);
            if (recipe.getSteps().isEmpty()) {
                stepTextView.setVisibility(View.GONE);
            } else {
                stepTextView.setVisibility(View.VISIBLE);
            }
            // Set tags data
            setTags(recipe);
            if (recipe.getTags().isEmpty()) {
                tagTextView.setVisibility(View.GONE);
            } else {
                tagTextView.setVisibility(View.VISIBLE);
            }

            // Update recipe source
            recipeSource.setText(recipe.getSource());
        });
    }

    /**
     * Sets the ingredients data for the ingredient adapter.
     * Maps recipe ingredients to ingredient data holders with shopping list status.
     *
     * @param recipe        The recipe containing ingredients
     * @param shoppingItems List of shopping items to check against
     */
    public void setIngredients(Recipe recipe, List<ShoppingItem> shoppingItems) {
        ingredientAdapter.setIngredientDataHolders(recipe.getIngredients().stream()
                .map(ingredient -> new IngredientAdapter.IngredientDataHolder(
                        ingredient,
                        isIngredientOnAListBought(ingredient, shoppingItems),
                        isIngredientOnAListToBuy(ingredient, shoppingItems))
                )
                .collect(Collectors.toList()));
    }

    /**
     * Sets the steps data for the step adapter.
     * Sorts steps by step number before setting them.
     *
     * @param recipe The recipe containing steps
     */
    public void setSteps(Recipe recipe) {
        stepAdapter.setSteps(recipe.getSteps().stream().sorted(Comparator.comparing(Step::getStepNumber)).collect(Collectors.toList()));
    }

    /**
     * Sets the tags data for the tag adapter.
     *
     * @param recipe The recipe containing tags
     */
    public void setTags(Recipe recipe) {
        tagAdapter.setTags(new ArrayList<>(recipe.getTags()));
    }

    /**
     * Checks if an ingredient is already on the shopping list.
     *
     * @param ingredient    The ingredient to check
     * @param shoppingItems List of shopping items to search through
     * @return true if ingredient is on the list, false otherwise
     */
    public boolean isIngredientOnAListBought(Ingredient ingredient, List<ShoppingItem> shoppingItems) {
        return shoppingItems.stream().anyMatch(shoppingItem -> shoppingItem.getItemName().equals(ingredient.getName()) && shoppingItem.isBought());
    }

    public boolean isIngredientOnAListToBuy(Ingredient ingredient, List<ShoppingItem> shoppingItems) {
        return shoppingItems.stream().anyMatch(shoppingItem -> shoppingItem.getItemName().equals(ingredient.getName()) && !shoppingItem.isBought());
    }
}
