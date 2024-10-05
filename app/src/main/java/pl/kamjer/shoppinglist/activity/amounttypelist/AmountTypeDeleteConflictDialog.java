package pl.kamjer.shoppinglist.activity.amounttypelist;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
import pl.kamjer.shoppinglist.viewmodel.AmountTypeDeleteConflictDialogViewModel;

public class AmountTypeDeleteConflictDialog extends GenericActivity {

    public static final String SELECTED_AMOUNT_TYPE_FIELD = "selectedAmountType";

    protected AmountType selectedAmountType;

    protected Button deleteItemsButton;
    protected Button cancelDeletingButton;
    protected Button acceptNewAmountTypeButton;
    protected Spinner newAmountTypeSpinner;

    protected AmountTypeDeleteConflictDialogViewModel amountTypeDeleteConflictDialogViewModel;


    protected View.OnClickListener deleteItemsButtonAction = v -> {
        amountTypeDeleteConflictDialogViewModel.deleteShoppingItemsForAmountType(selectedAmountType, connectionFailedAction);
        this.finish();
    };

    protected View.OnClickListener cancelDeletingButtonAction = v -> this.finish();

    protected View.OnClickListener acceptNewAmountTypeButtonAction = v -> {
        amountTypeDeleteConflictDialogViewModel.updateShoppingItemsAmountTypeAndDeleteAmountType(selectedAmountType, (AmountType) newAmountTypeSpinner.getSelectedItem(), connectionFailedAction);
        this.finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount_type_conflict_dialog_layout);

        selectedAmountType = Optional.ofNullable((AmountType) getIntent().getSerializableExtra(SELECTED_AMOUNT_TYPE_FIELD)).orElseThrow(() -> new NoUserFoundException(getString(R.string.selected_amount_type_is_null_massage)));

        amountTypeDeleteConflictDialogViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(AmountTypeDeleteConflictDialogViewModel.initializer)
        ).get(AmountTypeDeleteConflictDialogViewModel.class);

        amountTypeDeleteConflictDialogViewModel.initialize();

        deleteItemsButton = findViewById(R.id.deleteItemsButton);
        deleteItemsButton.setOnClickListener(deleteItemsButtonAction);
        cancelDeletingButton = findViewById(R.id.cancelDeletingButton);
        cancelDeletingButton.setOnClickListener(cancelDeletingButtonAction);
        acceptNewAmountTypeButton = findViewById(R.id.acceptNewAmountTypeButton);
        acceptNewAmountTypeButton.setOnClickListener(acceptNewAmountTypeButtonAction);
        newAmountTypeSpinner = findViewById(R.id.newAmountTypeSpinner);

        amountTypeDeleteConflictDialogViewModel.setAmountTypesLiveDataObserver(this, amountTypes -> {
            amountTypes = amountTypes.stream().filter(amountType -> !amountType.equals(selectedAmountType)).collect(Collectors.toList());
            newAmountTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, amountTypes));
        });
    }
}
