package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.util.validation.NewItemDialogDataValidator;
import pl.kamjer.shoppinglist.viewmodel.NewShoppingItemDialogViewModel;

/**
 * Activity for creating new shopping items.
 * This dialog allows users to input item details including name, amount, amount type, and category.
 */
public class NewShoppingItemDialog extends GenericActivity {

    /**
     * Key for passing category data through intent extras.
     */
    public final static String CATEGORY_FIELD_NAME = "categoryFieldName";

    /**
     * EditText for entering the shopping item name.
     */
    protected EditText shoppingItemEditText;

    /**
     * EditText for entering the shopping item amount.
     */
    protected EditText amountEditText;

    /**
     * Spinner for selecting the amount type.
     */
    protected Spinner amountTypeSpinner;

    /**
     * Spinner for selecting the category.
     */
    protected Spinner categorySpinner;

    /**
     * ArrayAdapter for the category spinner.
     */
    protected ArrayAdapter<Category> categorySpinnerAdapter;

    /**
     * ArrayAdapter for the amount type spinner.
     */
    protected ArrayAdapter<AmountType> amountTypeSpinnerAdapter;

    /**
     * ImageButton for creating a new shopping item.
     */
    protected ImageButton createNewShoppingItemImageButton;

    /**
     * ViewModel for managing the new shopping item dialog data.
     */
    protected NewShoppingItemDialogViewModel newShoppingItemDialogViewModel;

    /**
     * Processes the created shopping item data.
     * This method can be overridden by subclasses to customize behavior.
     *
     * @param shoppingItem The shopping item to process
     */
    protected void actOnData(ShoppingItem shoppingItem) {
        newShoppingItemDialogViewModel.insertShoppingItem(shoppingItem);
    }

    /**
     * Called when the activity is created.
     * Initializes UI components, sets up data observers, and configures the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shopping_item_dialog_layout);

        initViewModel();

        setTitle(R.string.new_item_dialog_title);

        amountTypeSpinner = findViewById(R.id.amountTypeSpinner);

        @SuppressWarnings("deprecation")
        Category category = (Category) getIntent().getSerializableExtra(CATEGORY_FIELD_NAME);

        shoppingItemEditText = findViewById(R.id.shoppingItemEditText);
        amountEditText = findViewById(R.id.amountEditText);

        createNewShoppingItemImageButton = findViewById(R.id.acceptNewShoppingItemImageButton);
        createNewShoppingItemImageButton.setOnClickListener(v -> {
            setOnclickListener();
        });

        categorySpinner = findViewById(R.id.categorySpinner);


        amountTypeSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        amountTypeSpinner.setAdapter(amountTypeSpinnerAdapter);
        newShoppingItemDialogViewModel.setAmountTypesListLiveDataObserver(this, this::setupAmountTypeSpinnerAction);

        categorySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        categorySpinner.setAdapter(categorySpinnerAdapter);

        newShoppingItemDialogViewModel.setCategoryListLiveDataObserver(this, categories -> {
            categorySpinnerAdapter.clear();
            categorySpinnerAdapter.addAll(categories);
            categorySpinnerAdapter.notifyDataSetChanged();
            categorySpinner.setSelection(Optional.of(categories.indexOf(category)).filter(integer -> integer != -1).orElse(0));
        });
    }

    /**
     * Sets up the amount type spinner with the provided list of amount types.
     *
     * @param amountTypes List of amount types to display in the spinner
     */
    protected void setupAmountTypeSpinnerAction(List<AmountType> amountTypes) {
        amountTypeSpinnerAdapter.clear();
        amountTypeSpinnerAdapter.addAll(amountTypes);
    }

    /**
     * Handles the click listener for creating a new shopping item.
     * Validates input data and creates a new shopping item if validation passes.
     */
    protected void setOnclickListener() {
        ShoppingItem.ShoppingItemBuilder shoppingItemToInsert = ShoppingItem.builder();

        // Validating if passed data is correct
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
    }

    /**
     * Initializes the ViewModel for the new shopping item dialog.
     * Loads user data, amount types, and categories.
     */
    protected void initViewModel() {
        newShoppingItemDialogViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(NewShoppingItemDialogViewModel.initializer)
        ).get(NewShoppingItemDialogViewModel.class);

        newShoppingItemDialogViewModel.loadUser();
        newShoppingItemDialogViewModel.loadAllAmountTypes();
        newShoppingItemDialogViewModel.loadAllCategory();
    }
}
