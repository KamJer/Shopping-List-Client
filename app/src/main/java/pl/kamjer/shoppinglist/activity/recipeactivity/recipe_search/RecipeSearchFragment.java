package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_search;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview.RecipeRecyclerViewAdapter;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

/**
 * Activity responsible for searching and displaying recipes.
 * This activity manages the user interface for recipe search functionality,
 * including search input, search mode selection, and recipe display.
 */
public class RecipeSearchFragment extends Fragment {

    /**
     * ViewModel for managing recipe search data and business logic.
     * Handles data fetching, search operations, and state management for recipes.
     */
    private RecipeViewModel recipeSearchViewModel;

    /**
     * EditText field for entering search terms.
     * Allows users to input text for recipe search.
     */
    private EditText editTextSearch;

    /**
     * Spinner for selecting search mode.
     * Provides different search modes (e.g., by name, by ingredient, etc.).
     */
    private Spinner spinnerSearchMode;

    /**
     * ImageButton for initiating the search operation.
     * Triggers the recipe search when clicked.
     */
    private ImageButton imageButtonSearch;

    /**
     * ImageButton for displaying the recipe menu.
     * Shows a popup menu with additional recipe-related options.
     */
    private ImageButton recipeMenuButton;

    /**
     * RecyclerView for displaying the list of recipes.
     * Shows search results in a scrollable list format.
     */
    private RecyclerView recyclerViewRecipes;

    /**
     * Initializes the activity and sets up all UI components and functionality.
     * This method is called during the activity creation lifecycle.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this activity and set up the container view
        View view = inflater.inflate(R.layout.recipe_search_fragment_layout, container, false);

        // Initialize the ViewModel with proper factory initialization
        loadViewModel();
        // Initialize all UI views by finding their references
        findViews(view);
        // Setup the RecyclerView for displaying recipes
        setupRecyclerView();
        // Setup the spinner with default selection
        setupSpinner();
        // Setup click listeners for UI elements
        setupClickListeners();

        loadInitialData();

        return view;
    }

    private void loadViewModel() {
        recipeSearchViewModel = new ViewModelProvider(
                requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
        // Initialize the ViewModel's internal state
        recipeSearchViewModel.initialize();
    }

    private void loadInitialData() {
        recipeSearchViewModel.getAllRecipes(0);
    }

    /**
     * Initializes all UI view references by finding them in the layout.
     * This method assigns each UI element to its corresponding instance variable.
     */
    private void findViews(View view) {
        editTextSearch = view.findViewById(R.id.editTextSearch);
        spinnerSearchMode = view.findViewById(R.id.spinner_search_mode);
        imageButtonSearch = view.findViewById(R.id.imageButtonSearch);
        recipeMenuButton = view.findViewById(R.id.recipe_menu_button);
        recyclerViewRecipes = view.findViewById(R.id.recyclerViewRecipes);
    }

    private void setupRecyclerView() {
        // Setup observer for recipe data updates
        recipeSearchViewModel.setRecipesLiveDataObserver(this, recipes -> {
            recyclerViewRecipes.setAdapter(new RecipeRecyclerViewAdapter(recipes.getContent(), recipe -> {
                recipeSearchViewModel.setActiveRecipe(recipe);
                findNavController(RecipeSearchFragment.this).navigate(R.id.action_search_to_recipe);
            }));
            recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        });
    }

    /**
     * Sets up the spinner with default selection.
     * Initializes the search mode spinner to its first option.
     */
    private void setupSpinner() {
        spinnerSearchMode.setSelection(0);
    }

    /**
     * Sets up click listeners for all interactive UI elements.
     * Configures the behavior for search button and menu button.
     */
    private void setupClickListeners() {
        // Set click listener for search button
        imageButtonSearch.setOnClickListener(v -> performSearch());

        // Set click listener for menu button
        recipeMenuButton.setOnClickListener(v -> showMenu());
    }

    /**
     * Performs the recipe search operation based on user input.
     * Retrieves search text and mode, then executes the search through the ViewModel.
     */
    private void performSearch() {
        // Get search text from the input field
        String searchText = editTextSearch.getText().toString().trim();

        // Get selected search mode from spinner
        String searchMode = spinnerSearchMode.getSelectedItem().toString();

        // Execute the search operation through the ViewModel
        recipeSearchViewModel.performSearch(RecipeViewModel.SearchMode.getModeBySelection(getContext(), searchMode), searchText);
    }

    /**
     * Displays the recipe menu popup.
     * Shows a context menu with additional recipe-related options.
     */
    private void showMenu() {
        // Create popup menu anchored to the menu button
        PopupMenu popupMenu = new PopupMenu(getContext(), recipeMenuButton);
        // Inflate the menu resource file
        popupMenu.getMenuInflater().inflate(R.menu.recipe_menu, popupMenu.getMenu());
        // Set click listener for menu items (currently does nothing)
        popupMenu.setOnMenuItemClickListener(item -> false);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.user_recipes_menu_item_id) {
                startUserRecipeFragment();
                return true;
            }
            return false;
        });
        // Display the popup menu
        popupMenu.show();
    }

    private void startUserRecipeFragment() {
        findNavController(this).navigate(R.id.action_search_to_user_recipes);
    }
}
