package pl.kamjer.shoppinglist.activity;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;
import java.util.logging.Level;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
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
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
