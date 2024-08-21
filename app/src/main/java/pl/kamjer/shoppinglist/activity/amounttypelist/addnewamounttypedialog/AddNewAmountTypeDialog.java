package pl.kamjer.shoppinglist.activity.amounttypelist.addnewamounttypedialog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.kamjer.shoppinglist.R;

public class AddNewAmountTypeDialog extends AppCompatActivity {

    public static final String AMOUNT_TYPE_FIELD_NAME = "amountTypeFieldName";

    private EditText amountTypeNameEditText;

    private final View.OnClickListener acceptNewAmountTypeButtonAction = v -> {
      getIntent().putExtra(AMOUNT_TYPE_FIELD_NAME, amountTypeNameEditText.getText().toString());
      setResult(RESULT_OK, getIntent());
      this.finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_amount_type_dialog_layout);

        amountTypeNameEditText = findViewById(R.id.amountTypeNameEditText);
        ImageButton acceptNewAmountTypeButton = findViewById(R.id.acceptNewAmountTypeButton);
        acceptNewAmountTypeButton.setOnClickListener(acceptNewAmountTypeButtonAction);
    }
}
