package pl.kamjer.shoppinglist.activity.amounttypelist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.amounttypelist.addnewamounttypedialog.AddNewAmountTypeDialog;
import pl.kamjer.shoppinglist.activity.amounttypelist.amounttyperecyclerview.AmountTypeRecyclerViewAdapter;
import pl.kamjer.shoppinglist.activity.amounttypelist.functionalinterface.ModifyAmountTypeAction;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.viewmodel.AmountTypeViewModel;

public class AmountTypeListActivity extends GenericActivity {

    private AmountTypeViewModel amountTypeViewModel;

    private RecyclerView amountTypeRecyclerView;

    private ActivityResultLauncher<Intent> createNewAmountTypeDialogLauncher;

    private final ModifyAmountTypeAction deleteAmountTypeAction = amountType -> {
//        loading data for that amountType
        amountTypeViewModel.loadAllShoppingItemsForAmountType(amountType);
//        observer for data
        amountTypeViewModel.setAllShoppingItemsForAmountTypeLiveDataObserver(this, new Observer<List<ShoppingItem>>() {
            @Override
            public void onChanged(List<ShoppingItem> shoppingItems) {
//            if data for that amountType is empty ust delete amountType if it is not as user what to do with data data
                if (shoppingItems.isEmpty()) {
                    amountTypeViewModel.deleteAmountType(amountType);
                } else {
                    Intent createNewCategoryIntent = new Intent(AmountTypeListActivity.this, AmountTypeDeleteConflictDialog.class);
                    createNewCategoryIntent.putExtra(AmountTypeDeleteConflictDialog.SELECTED_AMOUNT_TYPE_FIELD, amountType);
                    startActivity(createNewCategoryIntent);
//                    removing observer so that it does not fire after deleting data
                    amountTypeViewModel.removeAllShoppingItemsForAmountTypeLiveDataObserver(this);
                }
            }
        });

    };

    private final ModifyAmountTypeAction updateAmountTypeAction = amountType -> {
        Intent createNewAmountTypeIntent = new Intent(this, UpdateAmountTypeDialog.class);
        createNewAmountTypeIntent.putExtra(UpdateAmountTypeDialog.AMOUNT_TYPE_FIELD_NAME, amountType);
        startActivity(createNewAmountTypeIntent);
    };

    private final View.OnClickListener addAmountTypeAction = v -> {
        Intent createNewAmountTypeIntent = new Intent(this, AddNewAmountTypeDialog.class);
        startActivity(createNewAmountTypeIntent);
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount_type_activity_layout);

        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayShowTitleEnabled(false));

        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));

        amountTypeViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(AmountTypeViewModel.initializer)
        ).get(AmountTypeViewModel.class);

//        loading data
        amountTypeViewModel.loadUser();
        amountTypeViewModel.loadAllAmountType();

        shoppingListActionBar.create(this);

//        finding views
        amountTypeRecyclerView = findViewById(R.id.amountTypeRecyclerView);
        amountTypeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton addNewAmountTypeDialogButton = findViewById(R.id.addNewAmountTypeDialogButton);
        addNewAmountTypeDialogButton.setOnClickListener(addAmountTypeAction);

//        observers
        amountTypeViewModel.setAllAmountTypeLiveDataObserver(this,
                amountTypes -> amountTypeRecyclerView
                        .setAdapter(new AmountTypeRecyclerViewAdapter(amountTypes,
                                deleteAmountTypeAction,
                                updateAmountTypeAction)));
    }
}
