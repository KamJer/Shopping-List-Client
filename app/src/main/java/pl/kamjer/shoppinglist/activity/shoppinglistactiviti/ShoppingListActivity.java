package pl.kamjer.shoppinglist.activity.shoppinglistactiviti;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog.NewCategoryDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog.UpdateCategoryDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.NewShoppingItemDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.UpdateShoppingItemDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview.ShoppingCategoryRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.TutorialManager;
import pl.kamjer.shoppinglist.util.exception.NoResourceFoundException;
import pl.kamjer.shoppinglist.util.funcinterface.AddShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnOrderChangedListener;
import pl.kamjer.shoppinglist.util.funcinterface.RemoveCategoryAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.viewmodel.ShoppingListViewModel;

@Log
public class ShoppingListActivity extends GenericActivity {

    /**
     * ViewModel for managing shopping list data.
     * Handles the lifecycle and data operations for categories and shopping items.
     */
    private ShoppingListViewModel shoppingListViewModel;

    /**
     * Adapter for the RecyclerView displaying shopping categories and items.
     */
    private ShoppingCategoryRecyclerViewAdapter shoppingCategoryRecyclerViewAdapter;

    /**
     * List of categories to be displayed in the RecyclerView.
     */
    private List<Category> categoryList = new ArrayList<>();

    /**
     * List of shopping items with their associated amount types and categories.
     */
    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategoriesList;

    /**
     * Launcher for creating a new category dialog.
     * Used to handle the result when a new category is created.
     */
    private ActivityResultLauncher<Intent> createNewCategoryDialogLauncher;

    /**
     * Launcher for updating a category dialog.
     * Used to handle the result when a category is updated.
     */
    private ActivityResultLauncher<Intent> updateCategoryDialogLauncher;

    /**
     * Action to add a new shopping item to a category.
     * Opens the NewShoppingItemDialog with the selected category.
     */
    private final AddShoppingItemAction addShoppingItemAction = category -> {
        Intent createNewShoppingItemIntent = new Intent(this, NewShoppingItemDialog.class);
        createNewShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME, category);
        startActivity(createNewShoppingItemIntent);
    };

    /**
     * Action to delete a category.
     * Calls the ViewModel to delete the specified category.
     */
    private final RemoveCategoryAction deleteCategoryAction =
            category -> shoppingListViewModel.deleteCategory(category);

    /**
     * Action to update a category.
     * Opens the UpdateCategoryDialog with the selected category.
     */
    private final RemoveCategoryAction updateCategoryAction = (category) -> {
        Intent updatedCategoryIntent = new Intent(this, UpdateCategoryDialog.class);
        updatedCategoryIntent.putExtra(UpdateCategoryDialog.CATEGORY_FIELD_NAME, category);
        updateCategoryDialogLauncher.launch(updatedCategoryIntent);
    };

    /**
     * Click listener for the add category button.
     * Opens the NewCategoryDialog to create a new category.
     */
    private final View.OnClickListener onClickListener = v -> {
        Intent createNewCategoryIntent = new Intent(this, NewCategoryDialog.class);
        createNewCategoryDialogLauncher.launch(createNewCategoryIntent);
    };

    /**
     * Click listener for the move to bought button.
     * Marks all bought shopping items as moved to bought.
     */
    private final View.OnClickListener moveToBoughtImageButtonAction = v -> {
        List<ShoppingItem> shoppingItemWithAmountTypeAndCategories = shoppingListViewModel.getAllShoppingItemWithAmountTypeAndCategoryValue().stream()
                .filter(shoppingItemWithAmountTypeAndCategory -> shoppingItemWithAmountTypeAndCategory.getShoppingItem().isBought())
                .map(shoppingItemWithAmountTypeAndCategory -> {
                    ShoppingItem shoppingItem = shoppingItemWithAmountTypeAndCategory.getShoppingItem();
                    shoppingItem.setMovedToBought(true);
                    return shoppingItem;
                }).collect(Collectors.toList());
        shoppingListViewModel.updateShoppingItems(shoppingItemWithAmountTypeAndCategories);
    };

    /**
     * Listener for checkbox state changes.
     * Updates the bought status of a shopping item in the ViewModel.
     */
    private final UpdateShoppingItemActonCheckBox checkBoxListener = (isChecked, shoppingItemWithAmountTypeAndCategory) -> {
        ShoppingItem shoppingItem = shoppingItemWithAmountTypeAndCategory.getShoppingItem();
        shoppingItem.setBought(isChecked);
        shoppingListViewModel.updateShoppingItem(shoppingItem);
    };

    /**
     * Action to delete a shopping item.
     * Calls the ViewModel to delete the specified shopping item.
     */
    private final ModifyShoppingItemAction deleteShoppingItemAction = shoppingItemWithAmountTypeAndCategory ->
            shoppingListViewModel.deleteShoppingItem(shoppingItemWithAmountTypeAndCategory.getShoppingItem());

    /**
     * Action to modify a shopping item.
     * Opens the UpdateShoppingItemDialog with the selected shopping item.
     */
    private final ModifyShoppingItemAction modifyShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        Intent updateShoppingItemIntent = new Intent(this, UpdateShoppingItemDialog.class);
        updateShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME, shoppingItemWithAmountTypeAndCategory.getCategory());
        updateShoppingItemIntent.putExtra(UpdateShoppingItemDialog.SELECTED_SHOPPING_ITEM, shoppingItemWithAmountTypeAndCategory.getShoppingItem());
        startActivity(updateShoppingItemIntent);
    };

    /**
     * Listener for order changes in categories.
     * Updates the local categories in the ViewModel when their order changes.
     */
    private final OnOrderChangedListener onOrderChangedListener = (category, oldCategory) -> {
        shoppingListViewModel.updateLocalCategory(category);
        shoppingListViewModel.updateLocalCategory(oldCategory);
    };

    /**
     * Callback for handling the back button press.
     * Moves the task to the background instead of finishing the activity.
     */
    protected OnBackPressedCallback onBack = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            moveTaskToBack(true);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate(R.layout.shopping_list_activity_layout, R.id.shopping_list_activity_id);

        getOnBackPressedDispatcher().addCallback(this, onBack);

        // Initialize ViewModel
        shoppingListViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ShoppingListViewModel.initializer)
        ).get(ShoppingListViewModel.class);

        // Initialize data
        shoppingListViewModel.initialize();

        // Initialize tutorial
        createTutorialManager();

        // Initialize lists
        categoryList = new ArrayList<>();
        shoppingItemWithAmountTypeAndCategoriesList = new ArrayList<>();

        createMenuBar(false);

        // Setup RecyclerView
        RecyclerView categoryRecyclerView = findViewById(R.id.categoryListRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup buttons
        ImageButton addCategoryImageButton = findViewById(R.id.addCategoryImageButton);
        addCategoryImageButton.setOnClickListener(onClickListener);

        ImageButton moveToBoughtImageButton = findViewById(R.id.moveToBoughtImageButton);
        moveToBoughtImageButton.setOnClickListener(moveToBoughtImageButtonAction);

        // Setup adapter
        shoppingCategoryRecyclerViewAdapter = new ShoppingCategoryRecyclerViewAdapter(categoryList,
                shoppingItemWithAmountTypeAndCategoriesList,
                deleteCategoryAction,
                updateCategoryAction,
                addShoppingItemAction,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction,
                onOrderChangedListener);
        shoppingCategoryRecyclerViewAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        categoryRecyclerView.setAdapter(shoppingCategoryRecyclerViewAdapter);

        // Setup observers for categories
        shoppingListViewModel.setAllCategoryObserver(this, categories -> {
            categoryList = categories;
            // Sorting elements in a list with index for showing order
            categoryList.sort(Comparator.comparingInt(Category::getIndex));
            shoppingCategoryRecyclerViewAdapter.setCategoryList(categoryList);
            shoppingCategoryRecyclerViewAdapter.setShoppingItemWithAmountTypeAndCategories(shoppingItemWithAmountTypeAndCategoriesList);
            shoppingCategoryRecyclerViewAdapter.notifyDataSetChanged();
        });

        // Setup observer for shopping items
        shoppingListViewModel.setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(this, shoppingItemWithAmountTypeAndCategories -> {
            shoppingItemWithAmountTypeAndCategoriesList = shoppingItemWithAmountTypeAndCategories;
            categoryList.sort(Comparator.comparingInt(Category::getIndex));
            shoppingCategoryRecyclerViewAdapter.setCategoryList(categoryList);
            shoppingCategoryRecyclerViewAdapter.setShoppingItemWithAmountTypeAndCategories(shoppingItemWithAmountTypeAndCategoriesList);
            shoppingCategoryRecyclerViewAdapter.notifyDataSetChanged();
        });

        // Setup dialog launchers
        createNewCategoryDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    shoppingListViewModel.insertCategory(Category.builder()
                            .categoryName(data.getStringExtra(NewCategoryDialog.NEW_CATEGORY_NAME))
                            // Sets index for the last element in the list
                            .index(shoppingListViewModel.getSizeCategory() + 1)
                            .build());
                }
            }
        });

        updateCategoryDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    try {
                        Category category = Optional.ofNullable((Category) data.getSerializableExtra(UpdateCategoryDialog.CATEGORY_FIELD_NAME)).orElseThrow(() -> new NoResourceFoundException(getString(R.string.no_category_found_massage)));
                        shoppingListViewModel.updateCategory(category);
                    } catch (NoResourceFoundException e) {
                        createToast(e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Creates and runs the tutorial manager for the shopping list activity.
     * Sets up overlay tutorials for first-time users.
     */
    private void createTutorialManager() {
        TutorialManager tutorialManager = new TutorialManager(
                shoppingListViewModel,
                new FrameLayout[]{findViewById(R.id.first_tutorial_overlay), findViewById(R.id.second_tutorial_overlay)},
                new FloatingActionButton[]{findViewById(R.id.nextOverlayButton), findViewById(R.id.okButton)});
        tutorialManager.runOverlayTutorial();
    }
}
