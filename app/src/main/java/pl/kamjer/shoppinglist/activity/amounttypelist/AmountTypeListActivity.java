package pl.kamjer.shoppinglist.activity.amounttypelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.ShoppingListActionBar;
import pl.kamjer.shoppinglist.activity.amounttypelist.addnewamounttypedialog.AddNewAmountTypeDialog;
import pl.kamjer.shoppinglist.activity.amounttypelist.amounttyperecyclerview.AmountTypeRecyclerViewAdapter;
import pl.kamjer.shoppinglist.activity.amounttypelist.functionalinterface.ModifyAmountTypeAction;
import pl.kamjer.shoppinglist.activity.shoppinglistactiviti.newcategorydialog.NewCategoryDialog;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.viewmodel.AmountTypeViewModel;

public class AmountTypeListActivity extends AppCompatActivity {

    private AmountTypeViewModel amountTypeViewModel;

    private RecyclerView amountTypeRecyclerView;

    private ActivityResultLauncher<Intent> createNewAmountTypeDialogLauncher;


    private final ModifyAmountTypeAction deleteAmountTypeAction = amountType -> {
        amountTypeViewModel.deleteAmountType(amountType,
                (t) -> createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error")));
    };

    private final ModifyAmountTypeAction updateAmountTypeAction = amountType -> {
//        TODO: create dialog for updating amount type and process data in here
        amountTypeViewModel.updateAmountType(amountType,
                (t) -> createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error")));
    };

    private final View.OnClickListener addAmountTypeAction = v -> {
        Intent createNewAmountTypeIntent = new Intent(this, AddNewAmountTypeDialog.class);
        createNewAmountTypeDialogLauncher.launch(createNewAmountTypeIntent);
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount_type_activity_layout);
        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));

        amountTypeViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.Factory.from(AmountTypeViewModel.initializer)
        ).get(AmountTypeViewModel.class);

//        loading data
        amountTypeViewModel.loadAllAmountType();
        amountTypeViewModel.loadUser();

//        finding views
        amountTypeRecyclerView = findViewById(R.id.amountTypeRecyclerView);
        amountTypeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton addNewAmountTypeDialogButton = findViewById(R.id.addNewAmountTypeDialogButton);
        addNewAmountTypeDialogButton.setOnClickListener(addAmountTypeAction);

//        observers
        amountTypeViewModel.setUserLiveDataObserver(this, user -> {});
        amountTypeViewModel.setAllAmountTypeLiveDataObserver(this,
                amountTypes -> amountTypeRecyclerView
                        .setAdapter(new AmountTypeRecyclerViewAdapter(amountTypes,
                                deleteAmountTypeAction,
                                updateAmountTypeAction)));

//        launchers
        createNewAmountTypeDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    amountTypeViewModel.insertAmountType(AmountType.builder()
                            .typeName(data.getStringExtra(AddNewAmountTypeDialog.AMOUNT_TYPE_FIELD_NAME))
                            .build(),
                            (t) -> createToast(Optional.ofNullable(t).map(Throwable::getMessage).orElse("could not found reason for error")));
                }
            }
        });
    }

    private void createToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
