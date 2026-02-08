package pl.kamjer.shoppinglist.activity.recipeactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview.RecipeRecyclerViewAdapter;
import pl.kamjer.shoppinglist.viewmodel.RecipeSearchViewModel;

/**
 * Activity responsible for searching and displaying recipes.
 * This activity manages the user interface for recipe search functionality,
 * including search input, search mode selection, and recipe display.
 */
public class RecipeSearchActivity extends GenericActivity {

    /**
     * ViewModel for managing recipe search data and business logic.
     * Handles data fetching, search operations, and state management for recipes.
     */
    private RecipeSearchViewModel recipeViewModel;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this activity and set up the container view
        inflate(R.layout.recipe_search_layout, R.id.recipe_search_activity_id);

        // Initialize the ViewModel with proper factory initialization
        recipeViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(RecipeSearchViewModel.initializer)
        ).get(RecipeSearchViewModel.class);

        // Create the action bar menu with specified options
        createMenuBar(true);

        // Initialize the ViewModel's internal state
        recipeViewModel.initialize();

        // Initialize all UI views by finding their references
        initViews();

        // Setup the RecyclerView for displaying recipes
        setupRecyclerView();

        // Setup the spinner with default selection
        setupSpinner();

        // Setup click listeners for UI elements
        setupClickListeners();
    }

    /**
     * Initializes all UI view references by finding them in the layout.
     * This method assigns each UI element to its corresponding instance variable.
     */
    private void initViews() {
        editTextSearch = findViewById(R.id.editTextSearch);
        spinnerSearchMode = findViewById(R.id.spinner_search_mode);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        recipeMenuButton = findViewById(R.id.recipe_menu_button);
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
    }

    /**
     * Sets up the RecyclerView for displaying recipes.
     * Configures the adapter and layout manager for the recipe list.
     * Handles both initial data loading and subsequent data updates.
     */
    private void setupRecyclerView() {
        // Handle initial recipes data if available
        recipeViewModel.getRecipesLiveDataValue().ifPresent(recipes -> {
            recyclerViewRecipes.setAdapter(new RecipeRecyclerViewAdapter(this, recipes));
            recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        });

        // Setup observer for recipe data updates
        recipeViewModel.setRecipesLiveDataObserver(this, recipes -> {
            recyclerViewRecipes.setAdapter(new RecipeRecyclerViewAdapter(this, recipes));
            recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
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
        recipeViewModel.performSearch(RecipeSearchViewModel.SearchMode.getModeBySelection(this, searchMode), searchText);

        // Show feedback to the user about the search operation
        Toast.makeText(this, "Wyszukiwanie: " + searchText + " (" + searchMode + ")", Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays the recipe menu popup.
     * Shows a context menu with additional recipe-related options.
     */
    private void showMenu() {
        // Create popup menu anchored to the menu button
        PopupMenu popupMenu = new PopupMenu(this, recipeMenuButton);

        // Inflate the menu resource file
        popupMenu.getMenuInflater().inflate(R.menu.recipe_menu, popupMenu.getMenu());

        // Set click listener for menu items (currently does nothing)
        popupMenu.setOnMenuItemClickListener(item -> false);

        // Display the popup menu
        popupMenu.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("A_LIFECYCLE", "A destroyed");
    }
}
