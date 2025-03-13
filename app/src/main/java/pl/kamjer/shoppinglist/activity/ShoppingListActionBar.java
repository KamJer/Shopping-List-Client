package pl.kamjer.shoppinglist.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;

import lombok.Getter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.amounttypelist.AmountTypeListActivity;
import pl.kamjer.shoppinglist.activity.boughtshoppingitemlist.BoughtShoppingItemListActivity;
import pl.kamjer.shoppinglist.activity.logindialog.LoginDialogOptionalLogin;
import pl.kamjer.shoppinglist.viewmodel.ShoppingListActionBarViewModel;

@Getter
public class ShoppingListActionBar extends AppBarLayout {

    private ImageButton amountTypeListButton;
    private ImageButton boughtListButton;
    private ImageButton loginDialogButton;
    private ImageButton connectionButtonIndicator;
    private Toolbar toolbar;

    private ShoppingListActionBarViewModel shoppingListActionBarViewModel;

    private OnClickListener amountTypeListButtonAction = v -> {
        Intent amountTypeListActivityIntent = new Intent(this.getContext(), AmountTypeListActivity.class);
        this.getContext().startActivity(amountTypeListActivityIntent);
    };

    private OnClickListener boughtListButtonAction = v -> {
        Intent boughtShoppingItemListActivityIntent = new Intent(this.getContext(), BoughtShoppingItemListActivity.class);
        this.getContext().startActivity(boughtShoppingItemListActivityIntent);
    };

    private OnClickListener loginDialogButtonAction = v -> {
        Intent loginDialogIntent = new Intent(this.getContext(), LoginDialogOptionalLogin.class);
        this.getContext().startActivity(loginDialogIntent);
    };

    private OnClickListener connectionButtonIndicatorAction = v -> shoppingListActionBarViewModel.reconnectWebsocket();

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

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toolbar_layout, this, true);

        toolbar = findViewById(R.id.toolbar);
        amountTypeListButton = findViewById(R.id.amountTypeListButton);
        amountTypeListButton.setOnClickListener(amountTypeListButtonAction);
        boughtListButton = findViewById(R.id.boughtListButton);
        boughtListButton.setOnClickListener(boughtListButtonAction);
        loginDialogButton = findViewById(R.id.loginDialogButton);
        loginDialogButton.setOnClickListener(loginDialogButtonAction);
        connectionButtonIndicator = findViewById(R.id.connectionIndicationImageButton);

        connectionButtonIndicator.setOnClickListener(connectionButtonIndicatorAction);
    }

    public void create(AppCompatActivity activity) {
        shoppingListActionBarViewModel = new ViewModelProvider(
                activity,
                ViewModelProvider.Factory.from(ShoppingListActionBarViewModel.initializer)
        ).get(ShoppingListActionBarViewModel.class);

        shoppingListActionBarViewModel.loadUser();

        Drawable drawable = connectionButtonIndicator.getBackground().mutate();
        if (shoppingListActionBarViewModel.isConnected()) {
            drawable.setTint(Color.GREEN);
        } else {
            drawable.setTint(Color.RED);
        }

        shoppingListActionBarViewModel.setOnOpenConnectionAction((connected) -> {
            Drawable drawable1 = connectionButtonIndicator.getBackground().mutate();
            if (connected) {
                drawable1.setTint(Color.GREEN);
            } else {
                drawable1.setTint(Color.RED);
            }
        });
    }
}
