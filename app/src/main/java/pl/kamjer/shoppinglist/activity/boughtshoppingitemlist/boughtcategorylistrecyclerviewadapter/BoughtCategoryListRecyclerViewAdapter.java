package pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.boughtcategorylistrecyclerviewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@AllArgsConstructor
public class BoughtCategoryListRecyclerViewAdapter extends RecyclerView.Adapter<BoughtCategoryListRecyclerViewHolder>{

    private List<Category> categoryList;
    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories;
    private UpdateShoppingItemActonCheckBox checkBoxListener;
    private ModifyShoppingItemAction deleteShoppingItemAction;
    private ModifyShoppingItemAction modifyShoppingItemAction;

    @NonNull
    @Override
    public BoughtCategoryListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bought_category_view_holder_layout, parent, false);
        return new BoughtCategoryListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoughtCategoryListRecyclerViewHolder holder, int position) {
        List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories1 = shoppingItemWithAmountTypeAndCategories.stream()
                .filter(shoppingItemWithAmountTypeAndCategory -> shoppingItemWithAmountTypeAndCategory.getCategory().equals(categoryList.get(position)))
                .filter(shoppingItemWithAmountTypeAndCategory -> shoppingItemWithAmountTypeAndCategory.getShoppingItem().isBought())
                .collect(Collectors.toList());
        holder.bind(categoryList.get(position),
                shoppingItemWithAmountTypeAndCategories1,
                checkBoxListener,
                deleteShoppingItemAction,
                modifyShoppingItemAction);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
