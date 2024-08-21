package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.kamjer.shoppinglist.R;

public class NewCategoryDialog extends AppCompatActivity {

    public static final String NEW_CATEGORY_NAME = "newCategoryName";

    private EditText newCategoryNameEditText;
    private ImageButton acceptNewCategoryNameImageButton;

    private final View.OnClickListener acceptNewCategoryNameAction = v -> {
        getIntent().putExtra(NEW_CATEGORY_NAME, newCategoryNameEditText.getText().toString());
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_category_dialog_layout);
        newCategoryNameEditText = findViewById(R.id.newCategoryNameEditText);
        acceptNewCategoryNameImageButton = findViewById(R.id.acceptNewCategoryNameImageButton);
        acceptNewCategoryNameImageButton.setOnClickListener(acceptNewCategoryNameAction);
    }
}
