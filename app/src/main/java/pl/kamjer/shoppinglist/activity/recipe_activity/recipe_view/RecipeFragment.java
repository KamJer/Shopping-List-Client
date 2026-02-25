package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * RecyclerView for displaying ingredients.
     */
    private RecyclerView ingredientRecyclerView;

    /**
     * RecyclerView for displaying steps.
     */
    private RecyclerView stepRecyclerView;

    /**
     * RecyclerView for displaying tags.
     */
    private RecyclerView tagRecyclerView;

    private IngredientAdapter ingredientAdapter;

    private StepAdapter stepAdapter;

    private TagAdapter tagAdapter;

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
        View view = inflater.inflate(R.layout.recipe_fragment_layout, container, false);

        loadViewModel();

        findViews(view);
        loadRecipe();
        return view;
    }

    /**
     * Initializes the ViewModel for recipe data.
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
        this.tagRecyclerView = view.findViewById(R.id.rvTags);
        this.recipeSource = view.findViewById(R.id.source_text_view);
    }

    /**
     * Loads and displays recipe data.
     */
    private void loadRecipe() {
        ingredientAdapter = new IngredientAdapter(
                ingredient -> {
                    Intent createNewShoppingItemIntent = new Intent(requireContext(), NewIngredientToListDialog.class);
                    createNewShoppingItemIntent.putExtra(NewIngredientToListDialog.INGREDIENT_FIELD_NAME, ingredient);
                    startActivity(createNewShoppingItemIntent);
                });

        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.ingredientRecyclerView.setAdapter(ingredientAdapter);

        stepAdapter = new StepAdapter();

        stepRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.stepRecyclerView.setAdapter(stepAdapter);

        tagAdapter = new TagAdapter();
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setAdapter(tagAdapter);

        recipeSearchViewModel.setRecipeWithShoppingItemsObserver(getViewLifecycleOwner(), pair -> {
            Recipe recipe = pair.first;
            List<ShoppingItem> shoppingItems = pair.second;

            recipeTitle.setText(recipe.getName());
            recipeDesc.setText(recipe.getDescription());

            setIngredients(recipe, shoppingItems);
            setSteps(recipe);
            setTags(recipe);

            recipeSource.setText(recipe.getSource());
        });
    }

    public void setIngredients(Recipe recipe, List<ShoppingItem> shoppingItems) {
        ingredientAdapter.setIngredientDataHolders(recipe.getIngredients().stream()
                .map(ingredient -> new IngredientAdapter.IngredientDataHolder(ingredient, isIngredientOnAList(ingredient, shoppingItems))
                )
                .collect(Collectors.toList()));
    }

    public void setSteps(Recipe recipe) {
        stepAdapter.setSteps(recipe.getSteps().stream().sorted(Comparator.comparing(Step::getStepNumber)).collect(Collectors.toList()));
    }

    public void setTags(Recipe recipe) {
        tagAdapter.setTags(new ArrayList<>(recipe.getTags()));
    }

    public boolean isIngredientOnAList(Ingredient ingredient, List<ShoppingItem> shoppingItems) {
        return shoppingItems.stream().anyMatch(shoppingItem -> shoppingItem.getItemName().equals(ingredient.getName()));
    }

}
