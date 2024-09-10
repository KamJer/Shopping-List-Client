package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.Optional;

import pl.kamjer.shoppinglist.model.ShoppingItem;

public class UpdateShoppingItemDialog extends NewShoppingItemDialog{

    public final static String SELECTED_SHOPPING_ITEM = "selectedShoppingItem";

    protected ShoppingItem shoppingItemToUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Optional.ofNullable((ShoppingItem) getIntent().getSerializableExtra(SELECTED_SHOPPING_ITEM)).ifPresent(shoppingItem -> {
            shoppingItemToUpdate = shoppingItem;

            shoppingItemEditText.setText(shoppingItem.getItemName());
            amountEditText.setText(String.valueOf(shoppingItem.getAmount()));
        });
    }

    @Override
    protected void actOnData(ShoppingItem shoppingItem) {
        shoppingItemToUpdate.setItemName(shoppingItem.getItemName());
        shoppingItemToUpdate.setAmount(shoppingItem.getAmount());
        shoppingItemToUpdate.setLocalItemCategoryId(shoppingItem.getLocalItemCategoryId());
        shoppingItemToUpdate.setItemCategoryId(shoppingItem.getItemCategoryId());
        shoppingItemToUpdate.setLocalItemAmountTypeId(shoppingItem.getLocalItemAmountTypeId());
        shoppingItemToUpdate.setItemAmountTypeId(shoppingItem.getItemAmountTypeId());
        newShoppingItemDialogViewModel.updateShoppingItem(shoppingItemToUpdate, connectionFailedAction);
    }
}
