package pl.kamjer.shoppinglist.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
import pl.kamjer.shoppinglist.activity.recipe_activity.RecipeActivity;
import pl.kamjer.shoppinglist.viewmodel.ShoppingListActionBarViewModel;

/**
 * Custom AppBarLayout implementation for the shopping list application's action bar.
 * This component manages the toolbar, connection indicator, and menu functionality.
 */
@Getter
public class ShoppingListActionBar extends AppBarLayout {

    /**
     * ImageButton that indicates the connection status to the server.
     */
    private ImageButton connectionButtonIndicator;

    /**
     * ImageButton that displays the main menu popup.
     */
    private ImageButton menuButton;

    /**
     * Toolbar component that contains the action bar elements.
     */
    private Toolbar toolbar;

    /**
     * ViewModel for managing the action bar's connection state and user data.
     */
    private ShoppingListActionBarViewModel shoppingListActionBarViewModel;

    /**
     * OnClickListener for the amount type list button action.
     * Navigates to the AmountTypeListActivity.
     */
    private final OnClickListener amountTypeListButtonAction = v -> {
        Intent amountTypeListActivityIntent = new Intent(this.getContext(), AmountTypeListActivity.class);
        this.getContext().startActivity(amountTypeListActivityIntent);
    };

    /**
     * OnClickListener for the bought list button action.
     * Navigates to the BoughtShoppingItemListActivity.
     */
    private final OnClickListener boughtListButtonAction = v -> {
        Intent boughtShoppingItemListActivityIntent = new Intent(this.getContext(), BoughtShoppingItemListActivity.class);
        this.getContext().startActivity(boughtShoppingItemListActivityIntent);
    };

    /**
     * OnClickListener for the login dialog button action.
     * Navigates to the LoginDialogOptionalLogin activity.
     */
    private final OnClickListener loginDialogButtonAction = v -> {
        Intent loginDialogIntent = new Intent(this.getContext(), LoginDialogOptionalLogin.class);
        this.getContext().startActivity(loginDialogIntent);
    };

    /**
     * OnClickListener for the menu popup button.
     * Displays a popup menu with various application options.
     */
    private final OnClickListener menuPopUpAction = v -> {
        PopupMenu menu = new PopupMenu(this.getContext(), v);
        menu.getMenuInflater().inflate(R.menu.main_toolbar_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.amount_type_list) {
                amountTypeListAction();
                return true;
            } else if (id == R.id.bought_list) {
                boughtListAction();
                return true;
            } else if (id == R.id.login_dialog) {
                loginDialogAction();
                return true;
            }else if (id == R.id.recipe_activity) {
                recipeAction();
                return true;
            } else if (id == R.id.about_app) {
                aboutAppAction();
                return true;
            }
            return false;
        });
        menu.show();
    };

    /**
     * OnClickListener for the connection indicator button.
     * Triggers a reconnection attempt to the websocket.
     */
    private final OnClickListener connectionButtonIndicatorAction =
            v -> shoppingListActionBarViewModel.reconnectWebsocket();

    /**
     * Constructor for creating the action bar with context only.
     *
     * @param context The context in which the action bar is created
     */
    public ShoppingListActionBar(@NonNull Context context) {
        super(context);
        init();
    }

    /**
     * Constructor for creating the action bar with context and attributes.
     *
     * @param context The context in which the action bar is created
     * @param attrs The attributes of the XML tag that is inflating the action bar
     */
    public ShoppingListActionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor for creating the action bar with context, attributes, and defStyleAttr.
     *
     * @param context The context in which the action bar is created
     * @param attrs The attributes of the XML tag that is inflating the action bar
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource
     */
    public ShoppingListActionBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the action bar by inflating the layout and setting up UI components.
     */
    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toolbar_layout, this, true);

        toolbar = findViewById(R.id.toolbar);
        menuButton = findViewById(R.id.main_tools_menu);
        menuButton.setOnClickListener(menuPopUpAction);
        connectionButtonIndicator = findViewById(R.id.connectionIndicationImageButton);

        connectionButtonIndicator.setOnClickListener(connectionButtonIndicatorAction);
    }

    /**
     * Creates and initializes the action bar with the provided activity.
     *
     * @param activity The AppCompatActivity that hosts this action bar
     */
    public void create(AppCompatActivity activity) {
        shoppingListActionBarViewModel = new ViewModelProvider(
                activity,
                ViewModelProvider.Factory.from(ShoppingListActionBarViewModel.initializer)
        ).get(ShoppingListActionBarViewModel.class);

        shoppingListActionBarViewModel.loadUser();

        changeColorIndicator(shoppingListActionBarViewModel.isConnected());

        shoppingListActionBarViewModel.setOnOpenConnectionAction(this::changeColorIndicator);
    }

    /**
     * Changes the color of the connection indicator based on the connection status.
     *
     * @param connected True if connected, false otherwise
     */
    private void changeColorIndicator(boolean connected) {
        Drawable drawable = connectionButtonIndicator.getBackground().mutate();
        if (connected) {
            drawable.setTint(Color.GREEN);
        } else {
            drawable.setTint(Color.RED);
        }
    }

    /**
     * Handles the amount type list menu action by starting the AmountTypeListActivity.
     */
    private void amountTypeListAction() {
        Intent amountTypeListActivityIntent = new Intent(this.getContext(), AmountTypeListActivity.class);
        this.getContext().startActivity(amountTypeListActivityIntent);
    }

    /**
     * Handles the bought list menu action by starting the BoughtShoppingItemListActivity.
     */
    private void boughtListAction() {
        Intent boughtShoppingItemListActivityIntent = new Intent(this.getContext(), BoughtShoppingItemListActivity.class);
        this.getContext().startActivity(boughtShoppingItemListActivityIntent);
    }

    /**
     * Handles the login dialog menu action by starting the LoginDialogOptionalLogin activity.
     */
    private void loginDialogAction() {
        Intent loginDialogIntent = new Intent(this.getContext(), LoginDialogOptionalLogin.class);
        this.getContext().startActivity(loginDialogIntent);
    }

    /**
     * Handles the recipe activity menu action by starting the RecipeActivity.
     */
    private void recipeAction() {
        Intent recipeSearchActivityIntent = new Intent(this.getContext(), RecipeActivity.class);
        this.getContext().startActivity(recipeSearchActivityIntent);
    }

    /**
     * Handles the about app menu action by starting the AboutActivity.
     */
    private void aboutAppAction() {
        Intent aboutAppIntent = new Intent(this.getContext(), AboutActivity.class);
        this.getContext().startActivity(aboutAppIntent);
    }
}
