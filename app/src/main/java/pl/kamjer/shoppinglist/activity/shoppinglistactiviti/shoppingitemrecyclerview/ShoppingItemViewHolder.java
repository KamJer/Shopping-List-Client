package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;

public class ShoppingItemViewHolder extends RecyclerView.ViewHolder{

    private final CheckBox isBoughtCheckBox;
    private final TextView shoppingItemsTextView;
    private final TextView amountTextView;
    private final TextView amountTypeTextView;
    private final ImageButton deleteShoppingItemButton;

    public ShoppingItemViewHolder(@NonNull View itemView) {
        super(itemView);
        isBoughtCheckBox = itemView.findViewById(R.id.isBoughtCheckBox);
        shoppingItemsTextView = itemView.findViewById(R.id.shoppingItemTextView);
        amountTextView = itemView.findViewById(R.id.amountTextView);
        amountTypeTextView = itemView.findViewById(R.id.amountTypeTextView);
        deleteShoppingItemButton = itemView.findViewById(R.id.deleteShoppingItemButton);
    }

    public void bind(@NonNull ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory,
                     UpdateShoppingItemActonCheckBox checkBoxListener,
                     DeleteShoppingItemAction deleteShoppingItemAction) {
        isBoughtCheckBox.setChecked(shoppingItemWithAmountTypeAndCategory.getShoppingItem().isBought());
        isBoughtCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkBoxListener.action(isChecked, shoppingItemWithAmountTypeAndCategory));

        shoppingItemsTextView.setText(shoppingItemWithAmountTypeAndCategory.getShoppingItem().getItemName());
        amountTextView.setText(shoppingItemWithAmountTypeAndCategory.getShoppingItem().getAmount().toString());
        amountTypeTextView.setText(shoppingItemWithAmountTypeAndCategory.getAmountType().getTypeName());

        deleteShoppingItemButton.setOnClickListener(v -> deleteShoppingItemAction.action(shoppingItemWithAmountTypeAndCategory));
    }
}
