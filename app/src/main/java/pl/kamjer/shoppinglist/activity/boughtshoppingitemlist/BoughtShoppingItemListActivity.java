package pl.kamjer.shoppinglist.activity.boughtshoppingitemlist;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.boughtcategorylistrecyclerviewadapter.BoughtCategoryListRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.viewmodel.BoughtShoppingItemsListViewModel;

public class BoughtShoppingItemListActivity extends AppCompatActivity {

    private BoughtShoppingItemsListViewModel boughtShoppingItemsListViewModel;

    private RecyclerView boughtShoppingItemsListRecyclerView;

    private List<Category> allCategories = new ArrayList<>();
    private List<ShoppingItemWithAmountTypeAndCategory> allShoppingWithAmountTypeAndCategories = new ArrayList<>();

    private final OnFailureAction connectionFailedAction =
            (t) -> createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error"));


    private final ModifyShoppingItemAction deleteShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        boughtShoppingItemsListViewModel.deleteShoppingItem(shoppingItemWithAmountTypeAndCategory.getShoppingItem());
    };

    private final ModifyShoppingItemAction modifyShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        boughtShoppingItemsListViewModel.deleteShoppingItem(shoppingItemWithAmountTypeAndCategory.getShoppingItem());
    };

    private final UpdateShoppingItemActonCheckBox checkBoxListener = (isChecked, shoppingItemWithAmountTypeAndCategory) -> {
        ShoppingItem shoppingItem = shoppingItemWithAmountTypeAndCategory.getShoppingItem();
        shoppingItem.setBought(isChecked);
        shoppingItem.setMovedToBought(isChecked);
        boughtShoppingItemsListViewModel.updateShoppingItem(shoppingItem);
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bought_shopping_item_list_activity_layout);

        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        shoppingListActionBar.create(this);
        setSupportActionBar(shoppingListActionBar.getToolbar());

        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));

        boughtShoppingItemsListViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(BoughtShoppingItemsListViewModel.initializer)
        ).get(BoughtShoppingItemsListViewModel.class);

//        loading data
        boughtShoppingItemsListViewModel.loadUser();
        boughtShoppingItemsListViewModel.loadAllShoppingItemWithAmountTypeAndCategoryLiveData();
        boughtShoppingItemsListViewModel.loadAllCategory();

        boughtShoppingItemsListRecyclerView = findViewById(R.id.boughtShoppingItemsListRecyclerView);
        boughtShoppingItemsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        boughtShoppingItemsListViewModel.setAllCategoryLiveDataObserver(this, categories -> {
            allCategories = categories;
            boughtShoppingItemsListRecyclerView.setAdapter(new BoughtCategoryListRecyclerViewAdapter(allCategories,
                    allShoppingWithAmountTypeAndCategories,
                    checkBoxListener,
                    deleteShoppingItemAction,
                    modifyShoppingItemAction));
        });

        boughtShoppingItemsListViewModel.setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(this, shoppingItemWithAmountTypeAndCategories -> {
            allShoppingWithAmountTypeAndCategories = shoppingItemWithAmountTypeAndCategories;
            boughtShoppingItemsListRecyclerView.setAdapter(new BoughtCategoryListRecyclerViewAdapter(allCategories,
                    allShoppingWithAmountTypeAndCategories,
                    checkBoxListener,
                    deleteShoppingItemAction,
                    modifyShoppingItemAction));
        });
    }

    private void createToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}
