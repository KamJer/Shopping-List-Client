package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.ShoppingItemLinearLayoutMenager;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.ShoppingItemRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.AddShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.RemoveCategoryAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView categoryNameTextView;
    private final RecyclerView shoppingItemsRecyclerView;
    private final ImageButton deleteCategoryImageButton;
    private final ImageButton updateCategoryImageButton;
    private final ImageButton addShoppinItemImageButton;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
        shoppingItemsRecyclerView = itemView.findViewById(R.id.shoppingItemsRecyclerView);
        deleteCategoryImageButton = itemView.findViewById(R.id.deleteCategoryImageButton);
        addShoppinItemImageButton = itemView.findViewById(R.id.addShoppingItemImageButton);
        updateCategoryImageButton = itemView.findViewById(R.id.updateCategoryImageButton);
    }

    public void bind(Category category,
                     List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories,
                     RemoveCategoryAction removeCategoryAction,
                     AddShoppingItemAction addShoppingItemAction,
                     UpdateShoppingItemActonCheckBox checkBoxListener,
                     RemoveCategoryAction updateCategory,
                     ModifyShoppingItemAction deleteShoppingItemAction,
                     ModifyShoppingItemAction modifyShoppingItemAction) {
        categoryNameTextView.setText(category.getCategoryName());
        shoppingItemsRecyclerView.setLayoutManager(new ShoppingItemLinearLayoutMenager(itemView.getContext()));
        ShoppingItemRecyclerViewAdapter shoppingItemRecyclerViewAdapter = new ShoppingItemRecyclerViewAdapter(shoppingItemWithAmountTypeAndCategories,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction);
        shoppingItemsRecyclerView.setAdapter(shoppingItemRecyclerViewAdapter);
        deleteCategoryImageButton.setOnClickListener(v -> removeCategoryAction.action(category));
        updateCategoryImageButton.setOnClickListener(v -> updateCategory.action(category));
        addShoppinItemImageButton.setOnClickListener(v -> addShoppingItemAction.action(category));
    }
}
