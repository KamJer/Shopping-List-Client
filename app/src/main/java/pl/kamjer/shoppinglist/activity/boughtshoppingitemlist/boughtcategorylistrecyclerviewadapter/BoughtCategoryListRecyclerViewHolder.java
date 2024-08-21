package pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.boughtcategorylistrecyclerviewadapter;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.boughtshoppingitemlistadapter.BoughtShoppintItemListRecyclerViewAdapter;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.DeleteShoppingItemAction;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

public class BoughtCategoryListRecyclerViewHolder extends RecyclerView.ViewHolder {

    private final TextView boughtCategoryTextView;
    private final RecyclerView boughtShoppingItemsRecyclerView;

    public BoughtCategoryListRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        boughtCategoryTextView = itemView.findViewById(R.id.boughtCategoryTextView);
        boughtShoppingItemsRecyclerView = itemView.findViewById(R.id.boughtShoppingItemsRecyclerView);
    }

    public void bind(Category category,
                     List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories,
                     UpdateShoppingItemActonCheckBox checkBoxListener, DeleteShoppingItemAction deleteShoppingItemAction) {
        boughtCategoryTextView.setText(category.getCategoryName());
        boughtShoppingItemsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        boughtShoppingItemsRecyclerView.setAdapter(new BoughtShoppintItemListRecyclerViewAdapter(shoppingItemWithAmountTypeAndCategories, checkBoxListener, deleteShoppingItemAction));
    }
}
