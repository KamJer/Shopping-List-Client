package pl.kamjer.shoppinglist.activity;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

@Log
public class GenericActivity extends AppCompatActivity {

    /**
     * Action to be performed when a connection failure occurs.
     * Displays a warning log message and shows a toast with the error details.
     */
    protected OnFailureAction connectionFailedAction =
            (t) -> {
                String tMassage = Optional.ofNullable(t).map(Throwable::getMessage).orElse(getString(R.string.could_not_find_reason_error_massage));
                log.log(Level.WARNING, tMassage);
                createToast(tMassage);
            };

    /**
     * Creates and displays a toast message with the provided text.
     *
     * @param s The message to display in the toast
     */
    protected void createToast(String s) {
        Toast.makeText(getApplicationContext(), Optional.ofNullable(s).orElse(""), Toast.LENGTH_LONG).show();
    }

    /**
     * Inflates a layout and applies window insets to adjust for system UI elements.
     * Sets the content view and pads the root for status and navigation bars (edge-to-edge).
     *
     * @param layout   The layout resource ID to inflate
     * @param layoutId The ID of the root view in the layout
     */
    protected void inflate(int layout, int layoutId) {
        setContentView(layout);
        final View rootView = findViewById(layoutId);
        final int initialPaddingLeft = rootView.getPaddingLeft();
        final int initialPaddingTop = rootView.getPaddingTop();
        final int initialPaddingRight = rootView.getPaddingRight();
        final int initialPaddingBottom = rootView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    initialPaddingLeft + systemBars.left,
                    initialPaddingTop + systemBars.top,
                    initialPaddingRight + systemBars.right,
                    initialPaddingBottom + systemBars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(rootView);
    }

    /**
     * Creates and configures the action bar menu.
     * Sets up the toolbar and configures display options based on whether the home button should be shown.
     *
     * @param displayHomeButton True if the home button should be displayed, false otherwise
     */
    protected void createMenuBar(boolean displayHomeButton) {
        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayShowTitleEnabled(!displayHomeButton));
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(displayHomeButton));

        shoppingListActionBar.create(this);
    }
}
