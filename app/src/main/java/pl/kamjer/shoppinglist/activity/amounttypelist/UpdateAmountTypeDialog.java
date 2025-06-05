package pl.kamjer.shoppinglist.activity.amounttypelist;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.amounttypelist.addnewamounttypedialog.AddNewAmountTypeDialog;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;

public class UpdateAmountTypeDialog extends AddNewAmountTypeDialog {

    public static final String AMOUNT_TYPE_FIELD_NAME = "amountTypeFieldName";

    protected AmountType selectedAmountType;

    @Override
    protected AmountType getAmountType() {
        selectedAmountType.setTypeName(amountTypeNameEditText.getText().toString());
        return selectedAmountType;
    }

    @Override
    protected void acceptingAmountType(AmountType amountType) {
        newAmountTypeDialogViewModel.updateAmountType(amountType);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedAmountType = Optional.ofNullable((AmountType) getIntent().getSerializableExtra(AMOUNT_TYPE_FIELD_NAME)).orElseThrow(() -> new NoUserFoundException(getString(R.string.no_amount_type_selected_message)));
        amountTypeNameEditText.setText(selectedAmountType.getTypeName());
    }
}
