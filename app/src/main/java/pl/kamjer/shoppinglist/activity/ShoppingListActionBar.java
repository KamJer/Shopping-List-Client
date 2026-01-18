package pl.kamjer.shoppinglist.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

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

    private ImageButton connectionButtonIndicator;
    private ImageButton menuButton;
    private Toolbar toolbar;

    private ShoppingListActionBarViewModel shoppingListActionBarViewModel;

    private final OnClickListener amountTypeListButtonAction = v -> {
        Intent amountTypeListActivityIntent = new Intent(this.getContext(), AmountTypeListActivity.class);
        this.getContext().startActivity(amountTypeListActivityIntent);
    };

    private final OnClickListener boughtListButtonAction = v -> {
        Intent boughtShoppingItemListActivityIntent = new Intent(this.getContext(), BoughtShoppingItemListActivity.class);
        this.getContext().startActivity(boughtShoppingItemListActivityIntent);
    };

    private final OnClickListener loginDialogButtonAction = v -> {
        Intent loginDialogIntent = new Intent(this.getContext(), LoginDialogOptionalLogin.class);
        this.getContext().startActivity(loginDialogIntent);
    };

    private final OnClickListener menuPopUpAction = v -> {
        PopupMenu menu = new PopupMenu(this.getContext(), v);
        menu.getMenuInflater().inflate(R.menu.main_toolbar_menu, menu.getMenu());
        MenuItem collapseItem = menu.getMenu().findItem(R.id.collapse_category);
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.amount_type_list) {
                amountTypeListAction();
                return true;
            } else if (id ==R.id.bought_list) {
                boughtListAction();
                return true;
            } else if (id == R.id.login_dialog) {
                loginDialogAction();
                return true;
            }else if (id == R.id.recipe_activity) {
                recipeAction();
                return true;
            }
            return false;
        });
        menu.show();
    };

    private final OnClickListener connectionButtonIndicatorAction =
            v -> shoppingListActionBarViewModel.reconnectWebsocket();

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
        menuButton = findViewById(R.id.main_tools_menu);
        menuButton.setOnClickListener(menuPopUpAction);
        connectionButtonIndicator = findViewById(R.id.connectionIndicationImageButton);

        connectionButtonIndicator.setOnClickListener(connectionButtonIndicatorAction);
    }

    public void create(AppCompatActivity activity) {
        shoppingListActionBarViewModel = new ViewModelProvider(
                activity,
                ViewModelProvider.Factory.from(ShoppingListActionBarViewModel.initializer)
        ).get(ShoppingListActionBarViewModel.class);

        shoppingListActionBarViewModel.loadUser();

        changeColorIndicator(shoppingListActionBarViewModel.isConnected());

        shoppingListActionBarViewModel.setOnOpenConnectionAction(this::changeColorIndicator);
    }

    private void changeColorIndicator(boolean connected) {
        Drawable drawable = connectionButtonIndicator.getBackground().mutate();
        if (connected) {
            drawable.setTint(Color.GREEN);
        } else {
            drawable.setTint(Color.RED);
        }
    }

    private void amountTypeListAction() {
        Intent amountTypeListActivityIntent = new Intent(this.getContext(), AmountTypeListActivity.class);
        this.getContext().startActivity(amountTypeListActivityIntent);
    }

    private void boughtListAction() {
        Intent boughtShoppingItemListActivityIntent = new Intent(this.getContext(), BoughtShoppingItemListActivity.class);
        this.getContext().startActivity(boughtShoppingItemListActivityIntent);
    }

    private void loginDialogAction() {
        Intent loginDialogIntent = new Intent(this.getContext(), LoginDialogOptionalLogin.class);
        this.getContext().startActivity(loginDialogIntent);
    }

    private void recipeAction() {
        Intent recipeSearchActivityIntent = new Intent(this.getContext(), RecipeSearchActivity.class);
        this.getContext().startActivity(recipeSearchActivityIntent);
    }
}
