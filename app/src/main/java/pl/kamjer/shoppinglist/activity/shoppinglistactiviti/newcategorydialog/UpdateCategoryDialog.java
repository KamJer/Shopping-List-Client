package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.util.exception.NoResourceFoundException;

public class UpdateCategoryDialog extends NewCategoryDialog {

    public static final String CATEGORY_FIELD_NAME = "categoryFieldName";

    private Category category;

    private final View.OnClickListener updateCategoryAction = v -> {
        String newCategoryName = newCategoryNameEditText.getText().toString();
        category.setCategoryName(newCategoryName);
        getIntent().putExtra(CATEGORY_FIELD_NAME, category);
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acceptNewCategoryNameImageButton.setOnClickListener(updateCategoryAction);

        try {
            category = Optional.ofNullable((Category) getIntent().getSerializableExtra(CATEGORY_FIELD_NAME)).orElseThrow(() -> new NoResourceFoundException(getString(R.string.no_category_found_massage)));
            newCategoryNameEditText.setText(category.getCategoryName());
        } catch (NoResourceFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
