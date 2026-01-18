package pl.kamjer.shoppinglist.activity.amounttypelist.addnewamounttypedialog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.viewmodel.NewAmountTypeDialogViewModel;

public class AddNewAmountTypeDialog extends GenericActivity {


    protected NewAmountTypeDialogViewModel newAmountTypeDialogViewModel;

    protected EditText amountTypeNameEditText;

    protected AmountType getAmountType() {
        return AmountType.builder().typeName(amountTypeNameEditText.getText().toString()).build();
    }

    protected View.OnClickListener acceptNewAmountTypeButtonAction = v -> {
        acceptingAmountType(getAmountType());
        this.finish();
    };

    protected void acceptingAmountType(AmountType amountType) {
        newAmountTypeDialogViewModel.insertAmountType(amountType);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_amount_type_dialog_layout);

        newAmountTypeDialogViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(NewAmountTypeDialogViewModel.initializer)
        ).get(NewAmountTypeDialogViewModel.class);

        newAmountTypeDialogViewModel.initialize();

        amountTypeNameEditText = findViewById(R.id.amountTypeNameEditText);
        ImageButton acceptNewAmountTypeButton = findViewById(R.id.acceptNewAmountTypeButton);
        acceptNewAmountTypeButton.setOnClickListener(acceptNewAmountTypeButtonAction);
    }
}
