package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

@AllArgsConstructor
@Getter
@Builder
public class ShoppingItemRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingItemViewHolder>{

    private List<ShoppingItemWithAmountTypeAndCategory> shoppingItemWithAmountTypeAndCategories;
    private UpdateShoppingItemActonCheckBox checkBoxListener;
    private DeleteShoppingItemAction deleteShoppingItemAction;

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item_view_holder_layout, parent, false);
        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position) {
        holder.bind(shoppingItemWithAmountTypeAndCategories.get(position), checkBoxListener, deleteShoppingItemAction);
    }

    @Override
    public int getItemCount() {
        return shoppingItemWithAmountTypeAndCategories.size();
    }
}
