package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newshoppingitemdialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.util.NewItemDialogDataValidator;
import pl.kamjer.shoppinglist.util.exception.ShoppingItemNameException;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;

public class NewShoppingItemDialog extends AppCompatActivity {

    public final static String CATEGORY_FIELD_NAME = "categoryFieldName";
    public final static String SHOPPING_ITEM_FIELD_NAME = "shoppingItemNameFieldName";
    public final static String AMOUNT_TYPE_FIELD_NAME = "amountTypeFieldName";
    public final static String AMOUNT_FIELD_NAME = "amountFieldName";
    public final static String CATEGORY_LIST_FIELD_NAME = "categoryListFieldName";
    public final static String AMOUNT_TYPE_LIST_FIELD_NAME = "amountTypeListFieldName";

    private EditText shoppingItemEditText;
    private EditText amountEditText;
    private Spinner amountTypeSpinner;
    private Spinner categorySpinner;

    private final View.OnClickListener createNewCategoryAction = v -> {
//        Validating if passed data is correct
        if (NewItemDialogDataValidator.isShoppingItemNameValid(shoppingItemEditText.getText().toString())) {
            getIntent().putExtra(SHOPPING_ITEM_FIELD_NAME, shoppingItemEditText.getText().toString());
        } else {
            Toast.makeText(this, R.string.shopping_item_error_massege, Toast.LENGTH_SHORT).show();
            return;
        }
        if (NewItemDialogDataValidator.isShoppingItemWeightValid(amountEditText.getText().toString())) {
            getIntent().putExtra(AMOUNT_FIELD_NAME, Double.parseDouble(amountEditText.getText().toString()));
        }

        getIntent().putExtra(AMOUNT_TYPE_FIELD_NAME, (AmountType) amountTypeSpinner.getSelectedItem());
        getIntent().putExtra(CATEGORY_FIELD_NAME, (Category) categorySpinner.getSelectedItem());
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shopping_item_dialog_layout);
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(this));

        List<Category> categoryList = Optional.ofNullable((ArrayList<Category>) getIntent().getSerializableExtra(CATEGORY_LIST_FIELD_NAME)).orElse(new ArrayList<>());
        List<AmountType> amountTypeList = Optional.ofNullable((ArrayList<AmountType>) getIntent().getSerializableExtra(AMOUNT_TYPE_LIST_FIELD_NAME)).orElse(new ArrayList<>());
        Category category = (Category) getIntent().getSerializableExtra(CATEGORY_FIELD_NAME);

        shoppingItemEditText = findViewById(R.id.shoppingItemEditText);
        amountEditText = findViewById(R.id.amountEditText);

        ImageButton createNewCategoryImageButton = findViewById(R.id.acceptNewShoppingItemImageButton);
        createNewCategoryImageButton.setOnClickListener(createNewCategoryAction);

        amountTypeSpinner = findViewById(R.id.amountTypeSpinner);
        amountTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, amountTypeList));

        categorySpinner = findViewById(R.id.categorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList));
        categorySpinner.setSelection(Optional.of(categoryList.indexOf(category)).filter(integer -> integer != -1).orElse(0));

    }
}
