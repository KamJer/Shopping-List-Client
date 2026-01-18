package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.util.funcinterface.ModifyShoppingItemAction;
import pl.kamjer.shoppinglist.util.funcinterface.UpdateShoppingItemActonCheckBox;

public class ShoppingItemViewHolder extends RecyclerView.ViewHolder{

    private final CheckBox isBoughtCheckBox;
    private final TextView shoppingItemsTextView;
    private final TextView amountTextView;
    private final TextView amountTypeTextView;
    private final ImageButton deleteShoppingItemButton;
    private final ImageButton modifyShoppingItemButton;

    public ShoppingItemViewHolder(@NonNull View itemView) {
        super(itemView);
        isBoughtCheckBox = itemView.findViewById(R.id.isBoughtCheckBox);
        shoppingItemsTextView = itemView.findViewById(R.id.shoppingItemTextView);
        amountTextView = itemView.findViewById(R.id.amountTextView);
        amountTypeTextView = itemView.findViewById(R.id.amountTypeTextView);
        deleteShoppingItemButton = itemView.findViewById(R.id.deleteShoppingItemButton);
        modifyShoppingItemButton = itemView.findViewById(R.id.modifyShoppingItemButton);
    }

    public void bind(@NonNull ShoppingItemWithAmountTypeAndCategory shoppingItemWithAmountTypeAndCategory,
                     UpdateShoppingItemActonCheckBox checkBoxListener,
                     ModifyShoppingItemAction deleteShoppingItemAction,
                     ModifyShoppingItemAction modifyShoppingItemAction) {
        isBoughtCheckBox.setChecked(shoppingItemWithAmountTypeAndCategory.getShoppingItem().isBought());
        isBoughtCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkBoxListener.action(isChecked, shoppingItemWithAmountTypeAndCategory));

        shoppingItemsTextView.setText(shoppingItemWithAmountTypeAndCategory.getShoppingItem().getItemName());
        if (shoppingItemWithAmountTypeAndCategory.getShoppingItem().getAmount() == null) {
            amountTextView.setText(String.valueOf(0));
        } else {
            amountTextView.setText(convertDoubleToString(shoppingItemWithAmountTypeAndCategory.getShoppingItem().getAmount()));
        }
        amountTypeTextView.setText(shoppingItemWithAmountTypeAndCategory.getAmountType().getTypeName());

        deleteShoppingItemButton.setOnClickListener(v -> deleteShoppingItemAction.action(shoppingItemWithAmountTypeAndCategory));
        modifyShoppingItemButton.setOnClickListener(v -> modifyShoppingItemAction.action(shoppingItemWithAmountTypeAndCategory));
    }

    public String convertDoubleToString(double number) {
        return String.valueOf(number).replaceAll("\\.?0+$", "");
    }
}
