package pl.kamjer.shoppinglist.activity.logindialog;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;

public class LoginDialogForcedLogin extends LoginDialogOptionalLogin {

    protected OnBackPressedCallback onBack = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            moveTaskToBack(true);
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    () -> moveTaskToBack(true)
            );
        } else {
            getOnBackPressedDispatcher().addCallback(this, onBack);
        }
        shoppingListActionBar.setVisibility(View.GONE);
    }
}
