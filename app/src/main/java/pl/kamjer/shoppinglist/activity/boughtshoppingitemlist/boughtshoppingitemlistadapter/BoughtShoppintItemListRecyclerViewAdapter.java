package pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.boughtshoppingitemlistadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview.ShoppingItemViewHolder;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;

@AllArgsConstructor
public class BoughtShoppintItemListRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingItemViewHolder>{

    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories;

    private UpdateShoppingItemActonCheckBox checkBoxListener;
    private ModifyShoppingItemAction deleteShoppingItemAction;
    private ModifyShoppingItemAction modifyShoppingItemAction;

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
        return shoppingItemWithAmountTypeAndCategories.size();
    }
}
