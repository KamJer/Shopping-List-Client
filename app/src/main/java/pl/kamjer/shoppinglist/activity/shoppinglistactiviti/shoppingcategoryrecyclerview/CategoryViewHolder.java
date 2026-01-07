package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.ShoppingItemLinearLayoutMenager;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.ShoppingItemRecyclerViewAdapter;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.AddShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnOrderChangedListener;
import pl.kamjer.shoppinglist.util.funcinterface.RemoveCategoryAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView categoryNameTextView;
    private final RecyclerView shoppingItemsRecyclerView;
    private final ImageButton menuButton;
    private final ImageButton addShoppinItemImageButton;
    private final ImageButton moveUpButton;
    private final ImageButton moveDownButton;
    private final ImageView collapseIndicator;
    //    has to initialized this way
    private ShoppingItemRecyclerViewAdapter shoppingItemRecyclerViewAdapter;

    private boolean collapseClicked;

    private Category category;

    private final View.OnClickListener collapseRecyclerViewButtonListener = v ->
            Optional.ofNullable(shoppingItemRecyclerViewAdapter).ifPresent(shoppingItemRecyclerViewAdapter1 -> {
                if (shoppingItemRecyclerViewAdapter1.getShoppingItemWithAmountTypeAndCategories().isEmpty()) {
                    return;
                }
                shoppingItemRecyclerViewAdapter1.setExpended(!shoppingItemRecyclerViewAdapter1.isExpended());
                collapseClicked = !collapseClicked;
                shoppingItemRecyclerViewAdapter1.notifyDataSetChanged();
            });

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        this.categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
        this.shoppingItemsRecyclerView = itemView.findViewById(R.id.shoppingItemsRecyclerView);
        this.menuButton = itemView.findViewById(R.id.menu_button);
        this.addShoppinItemImageButton = itemView.findViewById(R.id.addShoppingItemImageButton);
        this.moveUpButton = itemView.findViewById(R.id.moveUpButton);
        this.moveDownButton = itemView.findViewById(R.id.moveDownButton);
        this.collapseIndicator = itemView.findViewById(R.id.collapseIndicator);
    }

    public void bind(Category category,
                     List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories,
                     RemoveCategoryAction removeCategoryAction,
                     AddShoppingItemAction addShoppingItemAction,
                     UpdateShoppingItemActonCheckBox checkBoxListener,
                     RemoveCategoryAction updateCategory,
                     ModifyShoppingItemAction deleteShoppingItemAction,
                     ModifyShoppingItemAction modifyShoppingItemAction,
                     OnOrderChangedListener onOrderChangedListener,
                     ShoppingCategoryRecyclerViewAdapter adapter) {
        this.category = category;
        categoryNameTextView.setText(category.getCategoryName());
        shoppingItemsRecyclerView.setLayoutManager(new ShoppingItemLinearLayoutMenager(itemView.getContext()));
        shoppingItemRecyclerViewAdapter = new ShoppingItemRecyclerViewAdapter(
                !collapseClicked,
                shoppingItemWithAmountTypeAndCategories,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction);
        shoppingItemsRecyclerView.setAdapter(shoppingItemRecyclerViewAdapter);
        menuButton.setOnClickListener(v -> removeCategoryAction.action(category));
        addShoppinItemImageButton.setOnClickListener(v -> addShoppingItemAction.action(category));

        menuButton.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(itemView.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.category_card_menu, menu.getMenu());
            MenuItem collapseItem = menu.getMenu().findItem(R.id.collapse_category);
            collapseItem.setTitle(
                    collapseClicked ?
                            itemView.getContext().getString(R.string.uncollapse_category) :
                            itemView.getContext().getString(R.string.collapse_category)
            );
            menu.setOnMenuItemClickListener(item -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return false;

                int id = item.getItemId();

                if (id == R.id.delete_category) {
                    removeCategoryAction.action(category);
                    return true;
                } else if (id == R.id.edit_category) {
                    updateCategory.action(category);
                    return true;
                } else if (id == R.id.collapse_category) {
                    collapseCategory();
                    return true;
                }
                return false;
            });

            menu.show();
        });
        moveUpButton.setOnClickListener(v -> moveUp(adapter, onOrderChangedListener));
        moveDownButton.setOnClickListener(v -> moveDown(adapter, onOrderChangedListener));
    }

    private void moveUp(ShoppingCategoryRecyclerViewAdapter adapter, OnOrderChangedListener onOrderChangedListener) {
        int currentIndex = adapter.getCategoryList().indexOf(category);
        if (currentIndex > 0) {
            int newIndex = currentIndex - 1;
            Collections.swap(adapter.getCategoryList(), currentIndex, newIndex);
            adapter.notifyDataSetChanged();
            category.setIndex(newIndex);
            Category oldCategory = adapter.getCategoryList().get(currentIndex);
            oldCategory.setIndex(currentIndex);
            onOrderChangedListener.onOrderChanged(category, oldCategory);
        }
    }

    private void moveDown(ShoppingCategoryRecyclerViewAdapter adapter, OnOrderChangedListener onOrderChangedListener) {
        int currentIndex = adapter.getCategoryList().indexOf(category);
        if (currentIndex != adapter.getCategoryList().size() - 1) {
            int newIndex = currentIndex + 1;
            Collections.swap(adapter.getCategoryList(), currentIndex, newIndex);
            adapter.notifyDataSetChanged();
            category.setIndex(newIndex);
            Category oldCategory = adapter.getCategoryList().get(currentIndex);
            oldCategory.setIndex(currentIndex);
            onOrderChangedListener.onOrderChanged(category, oldCategory);
        }
    }

    private void collapseCategory() {
        Optional.ofNullable(shoppingItemRecyclerViewAdapter).ifPresent(shoppingItemRecyclerViewAdapter1 -> {
            if (shoppingItemRecyclerViewAdapter1.getShoppingItemWithAmountTypeAndCategories().isEmpty()) {
                return;
            }
            if (!collapseClicked) {
                collapseIndicator.setVisibility(View.VISIBLE);
            } else {
                collapseIndicator.setVisibility(View.GONE);
            }
            shoppingItemRecyclerViewAdapter1.setExpended(!shoppingItemRecyclerViewAdapter1.isExpended());
            collapseClicked = !collapseClicked;
            shoppingItemRecyclerViewAdapter1.notifyDataSetChanged();
        });
    }
}
