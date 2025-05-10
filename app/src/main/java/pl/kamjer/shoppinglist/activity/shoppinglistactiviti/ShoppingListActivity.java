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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog.NewCategoryDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog.UpdateCategoryDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.NewShoppingItemDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.UpdateShoppingItemDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview.ShoppingCategoryRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.TutorialManager;
import pl.kamjer.shoppinglist.util.exception.NoResourceFoundException;
import pl.kamjer.shoppinglist.util.funcinterface.AddShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.RemoveCategoryAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.viewmodel.ShoppingListViewModel;

@Log
public class ShoppingListActivity extends GenericActivity {

    private ShoppingListViewModel shoppingListViewModel;
    private ShoppingCategoryRecyclerViewAdapter shoppingCategoryRecyclerViewAdapter;

    private List<Category> categoryList = new ArrayList<>();
    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategoriesList;

    private ActivityResultLauncher<Intent> createNewCategoryDialogLauncher;
    private ActivityResultLauncher<Intent> updateCategoryDialogLauncher;

    private final AddShoppingItemAction addShoppingItemAction = category -> {
        Intent createNewShoppingItemIntent = new Intent(this, NewShoppingItemDialog.class);
        createNewShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME, category);
        startActivity(createNewShoppingItemIntent);
    };

    private final RemoveCategoryAction deleteCategoryAction =
            category -> shoppingListViewModel.deleteCategory(category,
                    connectionFailedAction);

    private final RemoveCategoryAction updateCategoryAction = (category) -> {
        Intent updatedCategoryIntent = new Intent(this, UpdateCategoryDialog.class);
        updatedCategoryIntent.putExtra(UpdateCategoryDialog.CATEGORY_FIELD_NAME, category);
        updateCategoryDialogLauncher.launch(updatedCategoryIntent);
    };

    private final View.OnClickListener onClickListener = v -> {
        Intent createNewCategoryIntent = new Intent(this, NewCategoryDialog.class);
        createNewCategoryDialogLauncher.launch(createNewCategoryIntent);
    };

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

    private final UpdateShoppingItemActonCheckBox checkBoxListener = (isChecked, shoppingItemWithAmountTypeAndCategory) -> {
        ShoppingItem shoppingItem = shoppingItemWithAmountTypeAndCategory.getShoppingItem();
        shoppingItem.setBought(isChecked);
        shoppingListViewModel.updateShoppingItem(shoppingItem,
                connectionFailedAction);
    };

    private final ModifyShoppingItemAction deleteShoppingItemAction = shoppingItemWithAmountTypeAndCategory ->
            shoppingListViewModel.deleteShoppingItem(shoppingItemWithAmountTypeAndCategory.getShoppingItem(),
                    connectionFailedAction);
    private final ModifyShoppingItemAction modifyShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        Intent updateShoppingItemIntent = new Intent(this, UpdateShoppingItemDialog.class);
        updateShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME, shoppingItemWithAmountTypeAndCategory.getCategory());
        updateShoppingItemIntent.putExtra(UpdateShoppingItemDialog.SELECTED_SHOPPING_ITEM, shoppingItemWithAmountTypeAndCategory.getShoppingItem());
        startActivity(updateShoppingItemIntent);
    };

    protected OnBackPressedCallback onBack = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            moveTaskToBack(true);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shopping_list_activity_layout);

        getOnBackPressedDispatcher().addCallback(this, onBack);

        shoppingListViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ShoppingListViewModel.initializer)
        ).get(ShoppingListViewModel.class);

//        loading data
        shoppingListViewModel.initialize();

//        initliazing tutorial
        TutorialManager tutorialManager = new TutorialManager(
                shoppingListViewModel,
                new FrameLayout[]{findViewById(R.id.first_tutorial_overlay), findViewById(R.id.second_tutorial_overlay)},
                new FloatingActionButton[]{findViewById(R.id.nextOverlayButton), findViewById(R.id.okButton)});
        tutorialManager.runOverlayTutorial();

        categoryList = new ArrayList<>();
        shoppingItemWithAmountTypeAndCategoriesList = new ArrayList<>();
//        finding view elements
        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        shoppingListActionBar.create(this);
        setSupportActionBar(shoppingListActionBar.getToolbar());

        RecyclerView categoryRecyclerView = findViewById(R.id.categoryListRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton addCategoryImageButton = findViewById(R.id.addCategoryImageButton);
        addCategoryImageButton.setOnClickListener(onClickListener);

        ImageButton moveToBoughtImageButton = findViewById(R.id.moveToBoughtImageButton);
        moveToBoughtImageButton.setOnClickListener(moveToBoughtImageButtonAction);

        shoppingCategoryRecyclerViewAdapter = new ShoppingCategoryRecyclerViewAdapter(categoryList,
                shoppingItemWithAmountTypeAndCategoriesList,
                deleteCategoryAction,
                updateCategoryAction,
                addShoppingItemAction,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction);
        shoppingCategoryRecyclerViewAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        categoryRecyclerView.setAdapter(shoppingCategoryRecyclerViewAdapter);

        shoppingListViewModel.setAllCategoryObserver(this, categories -> {
            categoryList = categories;
            shoppingCategoryRecyclerViewAdapter.setCategoryList(categoryList);
            shoppingCategoryRecyclerViewAdapter.setShoppingItemWithAmountTypeAndCategories(shoppingItemWithAmountTypeAndCategoriesList);
            shoppingCategoryRecyclerViewAdapter.notifyDataSetChanged();
        });

        shoppingListViewModel.setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(this, shoppingItemWithAmountTypeAndCategories -> {
            shoppingItemWithAmountTypeAndCategoriesList = shoppingItemWithAmountTypeAndCategories;
            shoppingCategoryRecyclerViewAdapter.setCategoryList(categoryList);
            shoppingCategoryRecyclerViewAdapter.setShoppingItemWithAmountTypeAndCategories(shoppingItemWithAmountTypeAndCategoriesList);
            shoppingCategoryRecyclerViewAdapter.notifyDataSetChanged();
        });

//        dialogsLaunchers
        createNewCategoryDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    shoppingListViewModel.insertCategory(Category.builder()
                                    .categoryName(data.getStringExtra(NewCategoryDialog.NEW_CATEGORY_NAME))
                                    .build(),
                            connectionFailedAction);
                }
            }
        });

        updateCategoryDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    try {
                        Category category = Optional.ofNullable((Category) data.getSerializableExtra(UpdateCategoryDialog.CATEGORY_FIELD_NAME)).orElseThrow(() -> new NoResourceFoundException(getString(R.string.no_category_found_massage)));
                        shoppingListViewModel.updateCategory(category,
                                connectionFailedAction);
                    } catch (NoResourceFoundException e) {
                        createToast(e.getMessage());
                    }
                }
            }
        });

    }
}
