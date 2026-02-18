package pl.kamjer.shoppinglist.activity;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

@Log
public class GenericActivity extends AppCompatActivity {

    protected OnFailureAction connectionFailedAction =
            (t) -> {
                String tMassage = Optional.ofNullable(t).map(Throwable::getMessage).orElse(getString(R.string.could_not_find_reason_error_massage));
                log.log(Level.WARNING, tMassage);
                createToast(tMassage);
            };

    protected void createToast(String s) {
        Toast.makeText(getApplicationContext(), Optional.ofNullable(s).orElse(""), Toast.LENGTH_LONG).show();
    }

    protected void inflate(int layout, int layoutId) {
        setContentView(layout);
        final View rootView = findViewById(layoutId);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(view.getPaddingLeft(),
                    statusBarHeight,
                    view.getPaddingRight(),
                    view.getPaddingBottom());
            return insets;
        });
    }

    protected void createMenuBar(boolean displayHomeButton) {
        ShoppingListActionBar shoppingListActionBar = findViewById(R.id.appBar);
        setSupportActionBar(shoppingListActionBar.getToolbar());
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayShowTitleEnabled(!displayHomeButton));
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(displayHomeButton));

        shoppingListActionBar.create(this);
    }
}
