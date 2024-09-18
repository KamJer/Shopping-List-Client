package pl.kamjer.shoppinglist.activity.logindialog;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, onBack);
        shoppingListActionBar.setVisibility(View.GONE);
    }
}
