package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.model.recipe.Step;
import pl.kamjer.shoppinglist.model.recipe.Tag;
import pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

/**
 * Fragment responsible for creating and editing recipes.
 * This fragment manages the complete recipe creation workflow including recipe information,
 * ingredients, steps, and saving functionality through a composite RecyclerView setup.
 */
public class CreateRecipeFragment extends Fragment {

    /**
     * ViewModel for handling recipe creation and update operations.
     * Manages the business logic for saving, updating, and retrieving recipe data.
     */
    private CreateRecipeViewModel createRecipeViewModel;

    /**
     * ViewModel for managing the active recipe state.
     * Handles the current recipe being viewed or edited.
     */
    private RecipeViewModel recipeViewModel;

    /**
     * RecyclerView that displays the complete recipe creation interface.
     * Contains multiple adapters for different recipe components (info, ingredients, steps, etc.).
     */
    private RecyclerView recyclerView;

    /**
     * Adapter for displaying and editing recipe information fields.
     * Handles recipe name, source, description, publication status, and tags.
     */
    private RecipeInfoAdapter recipeInfoAdapter;

    /**
     * Adapter for managing recipe ingredients.
     * Provides functionality for adding, editing, and removing ingredients.
     */
    private CreateIngredientAdapter createIngredientAdapter;

    /**
     * Adapter for the "Add Ingredient" button.
     * Displays the button that triggers ingredient addition functionality.
     */
    private CreateIngredientButtonAdapter createIngredientButtonAdapter;

    /**
     * Adapter for managing recipe steps.
     * Handles the display and editing of recipe preparation steps.
     */
    private CreateStepAdapter createStepAdapter;

    /**
     * Adapter for the "Add Step" button.
     * Displays the button that triggers step addition functionality.
     */
    private CreateStepButtonAdapter createStepButtonAdapter;

    /**
     * Adapter for the "Save Recipe" button.
     * Displays the save button that triggers the recipe saving process.
     */
    private SaveRecipeButtonAdapter saveRecipeButtonAdapter;

    /**
     * Initializes the fragment view and sets up the RecyclerView with all component adapters.
     * This method is called during the fragment's creation lifecycle.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return The View for the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.create_recipe_fragment_layout, container, false);

        loadViewModel();
        findViews(view);
        recyclerViewSetup();
//        loadRecipe();
//        btnAction();
//        setupActiveRecipeObserver();

        return view;
    }

    /**
     * Initializes all UI components by finding views in the layout.
     * Sets up the RecyclerView that will host all recipe creation components.
     *
     * @param view The root view of the fragment
     */
    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recipeRecyclerView);
    }

    /**
     * Initializes the ViewModels required for recipe creation and management.
     * Sets up the ViewModel providers and initializes the ViewModels.
     */
    private void loadViewModel() {
        createRecipeViewModel = new ViewModelProvider(
                getActivity(),
                ViewModelProvider.Factory.from(CreateRecipeViewModel.initializer)
        ).get(CreateRecipeViewModel.class);
        createRecipeViewModel.initialize();
        recipeViewModel = new ViewModelProvider(
                getActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
        recipeViewModel.initialize();
    }

    /**
     * Sets up the RecyclerView with all component adapters and configures data observers.
     * Configures the RecyclerView layout manager and combines multiple adapters into a single
     * concatenated adapter for the complete recipe creation interface.
     */
    public void recyclerViewSetup() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recipeInfoAdapter = new RecipeInfoAdapter();
        createIngredientAdapter = new CreateIngredientAdapter();
        createIngredientButtonAdapter = new CreateIngredientButtonAdapter(addIngredientAction);
        createStepAdapter = new CreateStepAdapter();
        createStepButtonAdapter = new CreateStepButtonAdapter(addStepAction);
        saveRecipeButtonAdapter = new SaveRecipeButtonAdapter(saveRecipeAction);

        recyclerView.setAdapter(
                new ConcatAdapter(
                        recipeInfoAdapter,
                        createIngredientAdapter,
                        createIngredientButtonAdapter,
                        createStepAdapter,
                        createStepButtonAdapter,
                        saveRecipeButtonAdapter
                ));

        createRecipeViewModel.setIngredientLiveDataObserver(this.getViewLifecycleOwner(), ingredients -> {
            createIngredientAdapter.setData(ingredients);
        });

        createRecipeViewModel.setStepLiveDataObserver(this.getViewLifecycleOwner(), steps -> {
            createStepAdapter.setData(steps);
        });

        recipeViewModel.setActiveRecipeLiveDataObserver(this.getViewLifecycleOwner(),
                recipe -> {
                    if (recipe != null) {
                        recipeInfoAdapter.setData(recipe.getName(), recipe.getSource(), recipe.getDescription(), recipe.getPublished(), Tag.denormalizeTags(recipe.getTags()));
                        createIngredientAdapter.setData(recipe.getIngredients());
                        createStepAdapter.setData(recipe.getSteps());
                    }
                });
    }

    // BUTTONS ACTIONS

    /**
     * Click listener for the save recipe button.
     * Handles both saving new recipes and updating existing recipes.
     * Processes all recipe data from the various adapters and performs the appropriate
     * database operation based on whether an active recipe exists.
     */
    private final View.OnClickListener saveRecipeAction = view -> {
        Optional<Recipe> activeRecipe = recipeViewModel.getActiveRecipeLiveDataValue();
        if (activeRecipe.isPresent()) {
            Recipe recipe = activeRecipe.get();
            recipe.setName(recipeInfoAdapter.getData().getRecipeName());
            recipe.setDescription(recipeInfoAdapter.getData().getDescription());
            recipe.setPublished(recipeInfoAdapter.getData().isPublished());
            recipe.setSource(recipeInfoAdapter.getData().getSource());
            recipe.setTags(Tag.normalizeTags(recipeInfoAdapter.getData().getTags()));
            recipe.setIngredients(createIngredientAdapter.getIngredients());
            recipe.setSteps(createStepAdapter.getSteps());
            createRecipeViewModel.updateRecipe(RecipeDto.map(recipe),
                    () -> {
                        recipeViewModel.setActiveRecipe(recipe);
                        findNavController(this).navigate(R.id.action_create_user_recipe_to_recipe);
                    },
                    throwable -> Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Recipe recipeToUpdate = new Recipe();
            recipeToUpdate.setName(recipeInfoAdapter.getData().getRecipeName());
            recipeToUpdate.setDescription(recipeInfoAdapter.getData().getDescription());
            recipeToUpdate.setPublished(recipeInfoAdapter.getData().isPublished());
            recipeToUpdate.setSource(recipeInfoAdapter.getData().getSource());
            recipeToUpdate.setTags(Tag.normalizeTags(recipeInfoAdapter.getData().getTags()));
            recipeToUpdate.setIngredients(createIngredientAdapter.getIngredients());
            recipeToUpdate.setSteps(createStepAdapter.getSteps());
            createRecipeViewModel.insertRecipe(RecipeDto.map(recipeToUpdate),
                    (recipe) -> {
                        recipeViewModel.setActiveRecipe(recipe);
                        findNavController(this).navigate(R.id.action_create_user_recipe_to_recipe);
                    },
                    throwable -> {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    };

    /**
     * Click listener for the "Add Step" button.
     * Adds a new empty step to the steps list and updates the LiveData observer.
     */
    private final View.OnClickListener addStepAction = view -> {
        List<Step> steps = createStepAdapter.getSteps();
        steps.add(new Step());
        createRecipeViewModel.setStepsLiveDataValue(steps);
    };

    /**
     * Click listener for the "Add Ingredient" button.
     * Adds a new empty ingredient to the ingredients list and updates the LiveData observer.
     */
    private final View.OnClickListener addIngredientAction = view -> {
        List<Ingredient> ingredients = createIngredientAdapter.getIngredients();
        ingredients.add(new Ingredient());
        createRecipeViewModel.setIngredientLiveDataValue(ingredients);
    };
}
