package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.util.validation.NewItemDialogDataValidator;
import pl.kamjer.shoppinglist.viewmodel.NewShoppingItemDialogViewModel;

public class NewShoppingItemDialog extends GenericActivity {

    public final static String CATEGORY_FIELD_NAME = "categoryFieldName";

    protected EditText shoppingItemEditText;
    protected EditText amountEditText;
    protected Spinner amountTypeSpinner;
    protected Spinner categorySpinner;

    protected NewShoppingItemDialogViewModel newShoppingItemDialogViewModel;

    private final View.OnClickListener createNewShoppingItemAction = v -> {
        ShoppingItem.ShoppingItemBuilder shoppingItemToInsert = ShoppingItem.builder();
//        Validating if passed data is correct
        if (NewItemDialogDataValidator.isShoppingItemNameValid(shoppingItemEditText.getText().toString())) {
            shoppingItemToInsert.itemName(shoppingItemEditText.getText().toString());
        } else {
            Toast.makeText(this, R.string.shopping_item_name_error_massage, Toast.LENGTH_SHORT).show();
            return;
        }
        if (NewItemDialogDataValidator.isShoppingItemAmountValid(amountEditText.getText().toString())) {
            shoppingItemToInsert.amount(Double.parseDouble(amountEditText.getText().toString()));
        } else {
            shoppingItemToInsert.amount(0D);
        }
        AmountType amountTypeSelected = (AmountType) amountTypeSpinner.getSelectedItem();
        if (NewItemDialogDataValidator.isShoppingItemAmountTypeValid(amountTypeSelected)) {
            shoppingItemToInsert.localItemAmountTypeId(amountTypeSelected.getLocalAmountTypeId());
            shoppingItemToInsert.itemAmountTypeId(amountTypeSelected.getAmountTypeId());
        } else {
            Toast.makeText(this, R.string.shopping_item_amount_type_error_massage, Toast.LENGTH_SHORT).show();
            return;
        }
        Category categorySelected = (Category) categorySpinner.getSelectedItem();
        if (NewItemDialogDataValidator.isShoppingItemCategoryValid(categorySelected)) {
            shoppingItemToInsert.localItemCategoryId(categorySelected.getLocalCategoryId());
            shoppingItemToInsert.itemCategoryId(categorySelected.getCategoryId());
        } else {
            Toast.makeText(this, R.string.shopping_item_category_error_massage, Toast.LENGTH_SHORT).show();
            return;
        }
        actOnData(shoppingItemToInsert.build());
        this.finish();
    };

    protected void actOnData(ShoppingItem shoppingItem) {
        newShoppingItemDialogViewModel.insertShoppingItem(shoppingItem, connectionFailedAction);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shopping_item_dialog_layout);

        newShoppingItemDialogViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(NewShoppingItemDialogViewModel.initializer)
        ).get(NewShoppingItemDialogViewModel.class);

        newShoppingItemDialogViewModel.loadUser();
        newShoppingItemDialogViewModel.loadAllAmountTypes();
        newShoppingItemDialogViewModel.loadAllCategory();

        setTitle(R.string.new_item_dialog_title);

        amountTypeSpinner = findViewById(R.id.amountTypeSpinner);

        Category category = (Category) getIntent().getSerializableExtra(CATEGORY_FIELD_NAME);

        shoppingItemEditText = findViewById(R.id.shoppingItemEditText);
        amountEditText = findViewById(R.id.amountEditText);

        ImageButton createNewShoppingItemImageButton = findViewById(R.id.acceptNewShoppingItemImageButton);
        createNewShoppingItemImageButton.setOnClickListener(createNewShoppingItemAction);

        categorySpinner = findViewById(R.id.categorySpinner);

        newShoppingItemDialogViewModel.setAmountTypesListLiveDataObserver(this, amountTypes -> {
            amountTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, amountTypes));
        });

        newShoppingItemDialogViewModel.setCategoryListLiveDataObserver(this, categories -> {
            categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
            categorySpinner.setSelection(Optional.of(categories.indexOf(category)).filter(integer -> integer != -1).orElse(0));
        });
    }
}
