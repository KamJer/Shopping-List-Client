package pl.kamjer.shoppinglist.activity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import lombok.Getter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.amounttypelist.AmountTypeListActivity;
import pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.BoughtShoppingItemListActivity;
import pl.kamjer.shoppinglist.activity.logindialog.LoginDialogOptionalLogin;

@Getter
public class ShoppingListActionBar extends AppBarLayout {

    protected ImageButton amountTypeListButton;
    protected ImageButton boughtListButton;
    protected ImageButton loginDialogButton;
    private Toolbar toolbar;

    protected OnClickListener amountTypeListButtonAction = v -> {
        Intent amountTypeListActivityIntent = new Intent(this.getContext(), AmountTypeListActivity.class);
        this.getContext().startActivity(amountTypeListActivityIntent);
    };

    protected OnClickListener boughtListButtonAction = v -> {
        Intent boughtShoppingItemListActivityIntent = new Intent(this.getContext(), BoughtShoppingItemListActivity.class);
        this.getContext().startActivity(boughtShoppingItemListActivityIntent);
    };

    protected OnClickListener loginDialogButtonAction = v -> {
        Intent loginDialogIntent = new Intent(this.getContext(), LoginDialogOptionalLogin.class);
        this.getContext().startActivity(loginDialogIntent);
    };

    public ShoppingListActionBar(@NonNull Context context) {
        super(context);
        init();
    }

    public ShoppingListActionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShoppingListActionBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toolbar_layout, this, true);

        toolbar = findViewById(R.id.toolbar);
        amountTypeListButton = findViewById(R.id.amountTypeListButton);
        amountTypeListButton.setOnClickListener(amountTypeListButtonAction);
        boughtListButton = findViewById(R.id.boughtListButton);
        boughtListButton.setOnClickListener(boughtListButtonAction);
        loginDialogButton = findViewById(R.id.loginDialogButton);
        loginDialogButton.setOnClickListener(loginDialogButtonAction);
    }
}
