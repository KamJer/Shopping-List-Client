package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShoppingItemRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingItemViewHolder>{

    private boolean expended;
    private final List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories;
    private final UpdateShoppingItemActonCheckBox checkBoxListener;
    private final ModifyShoppingItemAction deleteShoppingItemAction;
    private final ModifyShoppingItemAction modifyShoppingItemAction;

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item_view_holder_layout, parent, false);
        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position) {
        holder.bind(shoppingItemWithAmountTypeAndCategories.get(position), checkBoxListener, deleteShoppingItemAction, modifyShoppingItemAction);
    }

    @Override
    public int getItemCount() {
        return expended ? shoppingItemWithAmountTypeAndCategories.size(): 0;
    }
}
