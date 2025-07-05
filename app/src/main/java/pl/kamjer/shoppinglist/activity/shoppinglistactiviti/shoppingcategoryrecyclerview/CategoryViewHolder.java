package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

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
//    has to initialized this way
    private ImageButton collapseRecyclerViewButton;
    private ShoppingItemRecyclerViewAdapter shoppingItemRecyclerViewAdapter;

    private Category category;

    private final View.OnClickListener collapseRecyclerViewButtonListener = v ->
            Optional.ofNullable(shoppingItemRecyclerViewAdapter).ifPresent(shoppingItemRecyclerViewAdapter1 -> {
                if (shoppingItemRecyclerViewAdapter1.getShoppingItemWithAmountTypeAndCategories().isEmpty()) {
                    return;
                }
                shoppingItemRecyclerViewAdapter1.setExpended(!shoppingItemRecyclerViewAdapter1.isExpended());
                category.setCollapsed(!category.isCollapsed());
                Optional.ofNullable(collapseRecyclerViewButton).ifPresent(imageButton -> {
                    collapseRecyclerViewButton = itemView.findViewById(R.id.collapseRecyclerViewButton);
                    if (shoppingItemRecyclerViewAdapter1.isExpended()) {
                        collapseRecyclerViewButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                    } else {
                        collapseRecyclerViewButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
                    }
                });
                shoppingItemRecyclerViewAdapter1.notifyDataSetChanged();
            });

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
        shoppingItemsRecyclerView = itemView.findViewById(R.id.shoppingItemsRecyclerView);
        deleteCategoryImageButton = itemView.findViewById(R.id.deleteCategoryImageButton);
        addShoppinItemImageButton = itemView.findViewById(R.id.addShoppingItemImageButton);
        updateCategoryImageButton = itemView.findViewById(R.id.updateCategoryImageButton);
        collapseRecyclerViewButton = itemView.findViewById(R.id.collapseRecyclerViewButton);
    }

    public void bind(Category category,
                     List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories,
                     RemoveCategoryAction removeCategoryAction,
                     AddShoppingItemAction addShoppingItemAction,
                     UpdateShoppingItemActonCheckBox checkBoxListener,
                     RemoveCategoryAction updateCategory,
                     ModifyShoppingItemAction deleteShoppingItemAction,
                     ModifyShoppingItemAction modifyShoppingItemAction) {
        this.category = category;
        categoryNameTextView.setText(category.getCategoryName());
        shoppingItemsRecyclerView.setLayoutManager(new ShoppingItemLinearLayoutMenager(itemView.getContext()));
        shoppingItemRecyclerViewAdapter = new ShoppingItemRecyclerViewAdapter(
                !category.isCollapsed(),
                shoppingItemWithAmountTypeAndCategories,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction);
        if (!shoppingItemWithAmountTypeAndCategories.isEmpty()) {
            collapseRecyclerViewButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
        } else {
            collapseRecyclerViewButton.setBackgroundResource(R.drawable.baseline_drag_handle_24);
        }
        shoppingItemsRecyclerView.setAdapter(shoppingItemRecyclerViewAdapter);
        deleteCategoryImageButton.setOnClickListener(v -> removeCategoryAction.action(category));
        updateCategoryImageButton.setOnClickListener(v -> updateCategory.action(category));
        addShoppinItemImageButton.setOnClickListener(v -> addShoppingItemAction.action(category));
        collapseRecyclerViewButton.setOnClickListener(collapseRecyclerViewButtonListener);
    }
}
