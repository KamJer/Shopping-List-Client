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

/**
 * Activity for updating existing shopping items.
 * This dialog allows users to modify the details of an existing shopping item.
 */
public class UpdateShoppingItemDialog extends NewShoppingItemDialog{

    /**
     * Key for passing the shopping item data through intent extras.
     */
    public final static String SELECTED_SHOPPING_ITEM = "selectedShoppingItem";

    /**
     * The shopping item that is being updated.
     */
    protected ShoppingItem shoppingItemToUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the title for the update dialog
        setTitle(R.string.modify_item_dialog_title);

        // Retrieve the shopping item to update from intent extras
        Optional.ofNullable((ShoppingItem) getIntent().getSerializableExtra(SELECTED_SHOPPING_ITEM)).ifPresent(shoppingItem -> {
            shoppingItemToUpdate = shoppingItem;

            // Populate the UI with existing item data
            shoppingItemEditText.setText(shoppingItem.getItemName());
            amountEditText.setText(convertDoubleToString(shoppingItem.getAmount()));

            // Set up the amount type spinner with existing item's amount type
            newShoppingItemDialogViewModel.setAmountTypesListLiveDataObserver(this, amountTypes -> {
                amountTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, amountTypes));
                amountTypeSpinner.setSelection(findIndex(amountTypes, shoppingItemToUpdate.getLocalItemAmountTypeId()).orElseThrow(IllegalStateException::new));
            });
        });
    }

    public String convertDoubleToString(double number) {
        return String.valueOf(number).replaceAll("\\.?0+$", "");
    }

    /**
     * Finds the index of an AmountType in the list based on its local ID.
     *
     * @param objects List of AmountType objects to search through
     * @param localId The local ID to find
     * @return Optional containing the index if found, empty otherwise
     */
    private Optional<Integer> findIndex(List<AmountType> objects, long localId) {
        return Optional.of(objects.stream().map(AmountType::getLocalAmountTypeId)
                        .collect(Collectors.toList())
                        .indexOf(localId))
                .filter(integer -> integer >= 0);
    }

    /**
     * Processes the updated shopping item data.
     * Updates the existing shopping item with new values and saves it.
     *
     * @param shoppingItem The updated shopping item data
     */
    @Override
    protected void actOnData(ShoppingItem shoppingItem) {
        // Update all properties of the existing shopping item with new values
        shoppingItemToUpdate.setItemName(shoppingItem.getItemName());
        shoppingItemToUpdate.setAmount(shoppingItem.getAmount());
        shoppingItemToUpdate.setLocalItemCategoryId(shoppingItem.getLocalItemCategoryId());
        shoppingItemToUpdate.setItemCategoryId(shoppingItem.getItemCategoryId());
        shoppingItemToUpdate.setLocalItemAmountTypeId(shoppingItem.getLocalItemAmountTypeId());
        shoppingItemToUpdate.setItemAmountTypeId(shoppingItem.getItemAmountTypeId());

        // Save the updated shopping item
        newShoppingItemDialogViewModel.updateShoppingItem(shoppingItemToUpdate);
    }
}
