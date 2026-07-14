package pl.kamjer.shoppinglist.activity.boughtshoppingitemlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.boughtcategorylistrecyclerviewadapter.BoughtCategoryListRecyclerViewAdapter;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.NewShoppingItemDialog;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog.UpdateShoppingItemDialog;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.viewmodel.BoughtShoppingItemsListViewModel;

public class BoughtShoppingItemListActivity extends GenericActivity {

    private BoughtShoppingItemsListViewModel boughtShoppingItemsListViewModel;

    private RecyclerView boughtShoppingItemsListRecyclerView;

    private List<Category> allCategories = new ArrayList<>();
    private List<ShoppingItemWithAmountTypeAndCategory> allShoppingWithAmountTypeAndCategories = new ArrayList<>();

    private BoughtCategoryListRecyclerViewAdapter boughtCategoryListRecyclerViewAdapter;

    private final ModifyShoppingItemAction deleteShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        boughtShoppingItemsListViewModel.deleteShoppingItem(shoppingItemWithAmountTypeAndCategory.getShoppingItem());
    };
    private final ModifyShoppingItemAction modifyShoppingItemAction = shoppingItemWithAmountTypeAndCategory -> {
        Intent updateShoppingItemIntent = new Intent(this, UpdateShoppingItemDialog.class);
        updateShoppingItemIntent.putExtra(NewShoppingItemDialog.CATEGORY_FIELD_NAME, shoppingItemWithAmountTypeAndCategory.getCategory());
        updateShoppingItemIntent.putExtra(UpdateShoppingItemDialog.SELECTED_SHOPPING_ITEM, shoppingItemWithAmountTypeAndCategory.getShoppingItem());
        startActivity(updateShoppingItemIntent);
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
        inflate(R.layout.bought_shopping_item_list_activity_layout, R.id.bought_list_activity_id);

        createMenuBar(true);

        boughtShoppingItemsListViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(BoughtShoppingItemsListViewModel.initializer)
        ).get(BoughtShoppingItemsListViewModel.class);

        boughtShoppingItemsListViewModel.loadUser();
        boughtShoppingItemsListViewModel.loadAllShoppingItemWithAmountTypeAndCategoryLiveData();
        boughtShoppingItemsListViewModel.loadAllCategory();

        boughtShoppingItemsListRecyclerView = findViewById(R.id.boughtShoppingItemsListRecyclerView);
        boughtShoppingItemsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        boughtCategoryListRecyclerViewAdapter = new BoughtCategoryListRecyclerViewAdapter(allCategories,
                allShoppingWithAmountTypeAndCategories,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction);
        boughtShoppingItemsListRecyclerView.setAdapter(boughtCategoryListRecyclerViewAdapter);


        boughtShoppingItemsListViewModel.setAllCategoryLiveDataObserver(this, categories -> {
            allCategories = categories;
            boughtCategoryListRecyclerViewAdapter.setCategoryList(allCategories);
            boughtCategoryListRecyclerViewAdapter.setShoppingItemWithAmountTypeAndCategories(allShoppingWithAmountTypeAndCategories);
            boughtCategoryListRecyclerViewAdapter.notifyDataSetChanged();
        });

        boughtShoppingItemsListViewModel.setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(this, shoppingItemWithAmountTypeAndCategories -> {
            allShoppingWithAmountTypeAndCategories = shoppingItemWithAmountTypeAndCategories;
            boughtCategoryListRecyclerViewAdapter.setCategoryList(allCategories);
            boughtCategoryListRecyclerViewAdapter.setShoppingItemWithAmountTypeAndCategories(allShoppingWithAmountTypeAndCategories);
            boughtCategoryListRecyclerViewAdapter.notifyDataSetChanged();
        });
    }
}
