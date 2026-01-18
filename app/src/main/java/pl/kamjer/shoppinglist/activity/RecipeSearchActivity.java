package pl.kamjer.shoppinglist.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

public class RecipeSearchActivity extends GenericActivity {

    private RecipeViewModel recipeViewModel;

    private EditText editTextSearch;
    private Spinner spinnerSearchMode;
    private ImageButton imageButtonSearch;
    private ImageButton recipeMenuButton;
    private RecyclerView recyclerViewRecipes;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_layout);

        recipeViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);

        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayShowTitleEnabled(false));
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));

        shoppingListActionBar.create(this);

        initViews();
        setupRecyclerView();
        setupSpinner();
        setupClickListeners();
    }

    private void initViews() {
        editTextSearch = findViewById(R.id.editTextSearch);
        spinnerSearchMode = findViewById(R.id.spinner_search_mode);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        recipeMenuButton = findViewById(R.id.recipe_menu_button);
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
    }

    private void setupRecyclerView() {
        recipeList = new ArrayList<>();
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSpinner() {
        spinnerSearchMode.setSelection(0);
    }

    private void setupClickListeners() {
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // Menu
        recipeMenuButton.setOnClickListener(v -> showMenu());
    }

    private void performSearch() {
        String searchText = editTextSearch.getText().toString().trim();
        String searchMode = spinnerSearchMode.getSelectedItem().toString();

        recipeViewModel.performSearch(RecipeViewModel.SearchMode.getModeBySelection(this, searchMode), searchText);
        Toast.makeText(this, "Wyszukiwanie: " + searchText + " (" + searchMode + ")", Toast.LENGTH_SHORT).show();
    }

    private void showMenu() {
        // Wyświetlanie menu
        PopupMenu popupMenu = new PopupMenu(this, recipeMenuButton);
        popupMenu.getMenuInflater().inflate(R.menu.recipe_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> false);
        popupMenu.show();
    }
}


