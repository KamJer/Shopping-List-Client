package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingcategoryrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.DeleteShoppingItemAction;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.UpdateShoppingItemActonCheckBox;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShoppingCategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryViewHolder>  {

    private List<Category> categoryList;
    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories;
    private RemoveCategoryAction removeCategoryAction;
    private RemoveCategoryAction updateCategoryAction;
    private AddShoppingItemAction addShoppingItemAction;
    private UpdateShoppingItemActonCheckBox checkBoxListener;
    private DeleteShoppingItemAction deleteShoppingItemAction;

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_category_view_holder_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories1 = shoppingItemWithAmountTypeAndCategories.stream()
                .filter(shoppingItemWithAmountTypeAndCategory -> shoppingItemWithAmountTypeAndCategory.getCategory().equals(categoryList.get(position)))
                .filter(shoppingItemWithAmountTypeAndCategory -> !shoppingItemWithAmountTypeAndCategory.getShoppingItem().isMovedToBought())
                .collect(Collectors.toList());
        holder.bind(categoryList.get(position), shoppingItemWithAmountTypeAndCategories1, removeCategoryAction, addShoppingItemAction, checkBoxListener, updateCategoryAction, deleteShoppingItemAction);
    }

    @Override
    public int getItemCount() {
        return Optional.ofNullable(categoryList).orElse(new ArrayList<>()).size();
    }
}
