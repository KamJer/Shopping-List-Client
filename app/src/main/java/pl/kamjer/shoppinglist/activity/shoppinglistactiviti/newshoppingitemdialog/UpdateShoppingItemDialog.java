package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;

public class UpdateShoppingItemDialog extends NewShoppingItemDialog{

    public final static String SELECTED_SHOPPING_ITEM = "selectedShoppingItem";

    protected ShoppingItem shoppingItemToUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.modify_item_dialog_title);

        Optional.ofNullable((ShoppingItem) getIntent().getSerializableExtra(SELECTED_SHOPPING_ITEM)).ifPresent(shoppingItem -> {
            shoppingItemToUpdate = shoppingItem;

            shoppingItemEditText.setText(shoppingItem.getItemName());
            amountEditText.setText(String.valueOf(shoppingItem.getAmount()));

            newShoppingItemDialogViewModel.setAmountTypesListLiveDataObserver(this, amountTypes -> {
                amountTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, amountTypes));
                amountTypeSpinner.setSelection(findIndex(amountTypes, shoppingItemToUpdate.getLocalItemAmountTypeId()).orElseThrow(IllegalStateException::new));
            });
        });
    }

    private Optional<Integer> findIndex(List<AmountType> objects, long localId) {
        return Optional.of(objects.stream().map(AmountType::getLocalAmountTypeId)
                .collect(Collectors.toList())
                .indexOf(localId))
                .filter(integer -> integer >= 0);
    }

    @Override
    protected void actOnData(ShoppingItem shoppingItem) {
        shoppingItemToUpdate.setItemName(shoppingItem.getItemName());
        shoppingItemToUpdate.setAmount(shoppingItem.getAmount());
        shoppingItemToUpdate.setLocalItemCategoryId(shoppingItem.getLocalItemCategoryId());
        shoppingItemToUpdate.setItemCategoryId(shoppingItem.getItemCategoryId());
        shoppingItemToUpdate.setLocalItemAmountTypeId(shoppingItem.getLocalItemAmountTypeId());
        shoppingItemToUpdate.setItemAmountTypeId(shoppingItem.getItemAmountTypeId());
        newShoppingItemDialogViewModel.updateShoppingItem(shoppingItemToUpdate);
    }
}
