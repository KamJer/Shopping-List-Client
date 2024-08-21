package pl.kamjer.shoppinglist.activity.shoppinglistactiviti;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog.NewCategoryDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.NewShoppingItemDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview.AddShoppingItemAction;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview.RemoveCategoryAction;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview.ShoppingCategoryRecyclerViewAdapter;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.DeleteShoppingItemAction;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.viewmodel.ShoppingListViewModel;

@Log
public class ShoppingListActivity extends AppCompatActivity {

    private ShoppingListViewModel shoppingListViewModel;
    private ShoppingCategoryRecyclerViewAdapter shoppingCategoryRecyclerViewAdapter;
    private RecyclerView categoryRecyclerView;

    private List<Category> categoryList;
    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategoriesList;

    private ActivityResultLauncher<Intent> createNewCategoryDialogLauncher;
    private ActivityResultLauncher<Intent> createNewShoppingItemDialogLauncher;

    private final OnFailureAction connectionFailedAction =
            (t) -> {
                log.log(Level.WARNING, Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error"));
                createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error"));
            };


    private final AddShoppingItemAction addShoppingItemAction = category -> {
        Intent createNewShoppingItemIntent = new Intent(this, NewShoppingItemDialog.class);
        createNewShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME, category);
        createNewShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_LIST_FIELD_NAME, shoppingListViewModel.getAllCategoryValue());
        createNewShoppingItemIntent.putExtra(NewShoppingItemDialog.AMOUNT_TYPE_LIST_FIELD_NAME, shoppingListViewModel.getAmountTypesValue());
        createNewShoppingItemDialogLauncher.launch(createNewShoppingItemIntent);
    };

    private final RemoveCategoryAction deleteCategoryAction =
            category -> shoppingListViewModel.deleteCategory(category,
                    connectionFailedAction);

    private final RemoveCategoryAction updateCategoryAction =
//            TODO: place holder, some dialog needs to be added here so that the changes can be made to the category
            category -> shoppingListViewModel.updateCategory(
                    category,
                    connectionFailedAction);

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

    private final DeleteShoppingItemAction deleteShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        shoppingListViewModel.deleteShoppingItem(shoppingItemWithAmountTypeAndCategory.getShoppingItem(),
                connectionFailedAction);
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_activity_layout);
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(this));

        shoppingListViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(ShoppingListViewModel.initializer)
        ).get(ShoppingListViewModel.class);

//        loading data
        shoppingListViewModel.loadAllCategory();
        shoppingListViewModel.loadAllShoppingItemWithAmountTypeAndCategory();
        shoppingListViewModel.loadAllAmountTypes();
        shoppingListViewModel.loadUser();

        categoryList = new ArrayList<>();
        shoppingItemWithAmountTypeAndCategoriesList = new ArrayList<>();
//        finding view elements
        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());

        categoryRecyclerView = findViewById(R.id.categoryListRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton addCategoryImageButton = findViewById(R.id.addCategoryImageButton);
        addCategoryImageButton.setOnClickListener(onClickListener);

        ImageButton moveToBoughtImageButton = findViewById(R.id.moveToBoughtImageButton);
        moveToBoughtImageButton.setOnClickListener(moveToBoughtImageButtonAction);

//        setting observers
        shoppingListViewModel.setAllAmountTypeLiveDataObserver(this, amountTypes -> {});

        shoppingListViewModel.setAllCategoryObserver(this, categories -> {
            categoryList = categories;
            shoppingCategoryRecyclerViewAdapter = new ShoppingCategoryRecyclerViewAdapter(categoryList,
                    shoppingItemWithAmountTypeAndCategoriesList,
                    deleteCategoryAction,
                    updateCategoryAction,
                    addShoppingItemAction,
                    checkBoxListener,
                    deleteShoppingItemAction);
            categoryRecyclerView.setAdapter(shoppingCategoryRecyclerViewAdapter);
        });

        shoppingListViewModel.setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(this, shoppingItemWithAmountTypeAndCategories -> {
            shoppingItemWithAmountTypeAndCategoriesList = shoppingItemWithAmountTypeAndCategories;
            shoppingCategoryRecyclerViewAdapter = new ShoppingCategoryRecyclerViewAdapter(categoryList,
                    shoppingItemWithAmountTypeAndCategoriesList,
                    deleteCategoryAction,
                    updateCategoryAction,
                    addShoppingItemAction,
                    checkBoxListener,
                    deleteShoppingItemAction);
            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            categoryRecyclerView.setAdapter(shoppingCategoryRecyclerViewAdapter);
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

        createNewShoppingItemDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Category category = (Category) data.getSerializableExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME);
                    AmountType amountType = (AmountType) data.getSerializableExtra(NewShoppingItemDialog.AMOUNT_TYPE_FIELD_NAME);
                    String shoppingItemName = data.getStringExtra(NewShoppingItemDialog.SHOPPING_ITEM_FIELD_NAME);
                    double amount = data.getDoubleExtra(NewShoppingItemDialog.AMOUNT_FIELD_NAME, 0);
                    if (category != null && amountType != null) {
                        shoppingListViewModel
                                .insertShoppingItem(ShoppingItem.builder()
                                        .localItemCategoryId(category.getLocalCategoryId())
                                        .localItemAmountTypeId(amountType.getLocalAmountTypeId())
                                        .itemCategoryId(category.getCategoryId())
                                        .itemAmountTypeId(amountType.getAmountTypeId())
                                        .itemName(shoppingItemName)
                                        .amount(amount)
                                        .build(), connectionFailedAction);
                    }
                }
            }
        });
    }

    private void createToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}
