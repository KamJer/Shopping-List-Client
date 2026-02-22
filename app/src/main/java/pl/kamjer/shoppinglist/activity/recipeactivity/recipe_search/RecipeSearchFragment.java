package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_search;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview.RecipeRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

/**
 * Fragment responsible for searching and displaying recipes.
 * This fragment manages the user interface for recipe search functionality,
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
     * Adapter for the recipe RecyclerView.
     * Handles binding recipe data to the RecyclerView items.
     */
    private RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;

    /**
     * TextView for displaying empty state message.
     * Shows when no recipes are found for the current search.
     */
    private TextView emptyView;

    /**
     * ProgressBar for showing loading state.
     * Displays while recipe data is being fetched and loaded.
     */
    private ProgressBar loadingDataProgressData;

    /**
     * ImageButton for importing ingredients from shopping list.
     * Allows users to import ingredients from their shopping list for search.
     */
    private ImageButton importIngredientsButton;

    /**
     * Click listener for the import ingredients button.
     * Handles the logic for importing shopping items as search query.
     */
    View.OnClickListener onImportIngredientsClickListener = view -> {
        recipeSearchViewModel.removeBoughtLiveDataObserver(getViewLifecycleOwner());
        recipeSearchViewModel.setBoughtShoppingItemsLiveDataObservers(getViewLifecycleOwner(), shoppingItems -> {
            String[] searchMode = getResources().getStringArray(R.array.search_modes);
            int ingredientsSearchModeIndex = 1;
            if (spinnerSearchMode.getSelectedItem().equals(searchMode[ingredientsSearchModeIndex])) {
                performSearch(shoppingItems.stream().map(ShoppingItem::toString).collect(Collectors.joining(", ")));
            }
        });
    };

    /**
     * Initializes the fragment and sets up all UI components and functionality.
     * This method is called during the fragment creation lifecycle.
     *
     * @param inflater           LayoutInflater used to inflate the fragment layout
     * @param container          ViewGroup container for the fragment
     * @param savedInstanceState Bundle containing the fragment's previously saved state
     * @return View representing the fragment's layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment and set up the container view
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

        return view;
    }

    /**
     * Initializes the RecipeViewModel for this fragment.
     * Creates and configures the ViewModel with proper factory initialization.
     */
    private void loadViewModel() {
        recipeSearchViewModel = new ViewModelProvider(
                requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
        // Initialize the ViewModel's internal state
        recipeSearchViewModel.initialize();
    }

    /**
     * Initializes all UI view references by finding them in the layout.
     * This method assigns each UI element to its corresponding instance variable.
     *
     * @param view View containing the UI elements to be initialized
     */
    private void findViews(View view) {
        editTextSearch = view.findViewById(R.id.editTextSearch);
        spinnerSearchMode = view.findViewById(R.id.spinner_search_mode);
        imageButtonSearch = view.findViewById(R.id.imageButtonSearch);
        recipeMenuButton = view.findViewById(R.id.recipe_menu_button);
        recyclerViewRecipes = view.findViewById(R.id.recyclerViewRecipes);
        this.emptyView = view.findViewById(R.id.emptyView);
        this.loadingDataProgressData = view.findViewById(R.id.loadingDataProgressBar);
        this.importIngredientsButton = view.findViewById(R.id.importIngredientsButton);
    }

    /**
     * Sets up the RecyclerView for displaying recipes.
     * Configures the adapter, layout manager, and load state listeners.
     */
    private void setupRecyclerView() {
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(new RecipeRecyclerViewAdapter.RecipeComparator(),
                recipe -> {
                    recipeSearchViewModel.setActiveRecipe(recipe);
                    findNavController(this).navigate(R.id.action_search_to_recipe);
                }
        );
        recyclerViewRecipes.setAdapter(recipeRecyclerViewAdapter);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeRecyclerViewAdapter.addLoadStateListener(combinedLoadStates -> {
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading && recipeRecyclerViewAdapter.getItemCount() == 0) {
                loadingDataProgressData.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading) {
                loadingDataProgressData.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else if (combinedLoadStates.getRefresh() instanceof LoadState.Loading) {
                loadingDataProgressData.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            return null;
        });

        setUpObservers();
    }

    /**
     * Sets up observers for the ViewModel data.
     * Registers observers to listen for recipe data changes and updates the RecyclerView.
     */
    private void setUpObservers() {
        recipeSearchViewModel.setRecipesLiveDataObserver(this, recipes ->
                recipeRecyclerViewAdapter.submitData(getViewLifecycleOwner().getLifecycle(), recipes));
    }

    /**
     * Sets up the spinner with default selection.
     * Initializes the search mode spinner to its first option and handles selection changes.
     */
    private void setupSpinner() {
        spinnerSearchMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] ingredients = getResources().getStringArray(R.array.search_modes);
                int ingredientsSearchModeIndex = 1;
                if (spinnerSearchMode.getSelectedItem().equals(ingredients[ingredientsSearchModeIndex])) {
                    importIngredientsButton.setVisibility(View.VISIBLE);
                } else {
                    importIngredientsButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No action needed
            }
        });
        spinnerSearchMode.setSelection(0);
    }

    /**
     * Sets up click listeners for all interactive UI elements.
     * Configures the behavior for search button, menu button, and import ingredients button.
     */
    private void setupClickListeners() {
        // Set click listener for search button
        imageButtonSearch.setOnClickListener(view -> performSearch(getQuery()));

        // Set click listener for menu button
        recipeMenuButton.setOnClickListener(showMenu);

        importIngredientsButton.setOnClickListener(onImportIngredientsClickListener);
    }

    /**
     * Retrieves the current search query from the EditText field.
     *
     * @return String containing the search query text
     */
    private String getQuery() {
        return editTextSearch.getText().toString().trim();
    }

    /**
     * Performs the recipe search operation based on user input.
     * Retrieves search text and mode, then executes the search through the ViewModel.
     *
     * @param query Search query string to be used for recipe search
     */
    private void performSearch(String query) {
        // Get selected search mode from spinner
        RecipeViewModel.SearchMode searchMode = RecipeViewModel.SearchMode.getModeBySelection(getContext(), spinnerSearchMode.getSelectedItem().toString());

        if (query.isEmpty()) searchMode = RecipeViewModel.SearchMode.NONE;
        // Execute the search operation through the ViewModel
        recipeSearchViewModel.removeRecipesLiveDataObserver(getViewLifecycleOwner());
        recipeSearchViewModel.performSearch(searchMode, query);
        setUpObservers();
    }

    /**
     * Displays the recipe menu popup.
     * Shows a context menu with additional recipe-related options.
     *
     * @param view View that triggered the popup menu
     */
    View.OnClickListener showMenu = view -> {
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
    };

    /**
     * Navigates to the user recipes fragment.
     * Handles the navigation to the user recipes screen.
     */
    private void startUserRecipeFragment() {
        findNavController(this).navigate(R.id.action_search_to_user_recipes);
    }
}
