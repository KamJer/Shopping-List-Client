package pl.kamjer.shoppinglist.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import pl.kamjer.shoppinglist.R;

public class AboutActivity extends GenericActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate(R.layout.about_activity_layout, R.id.shopping_list_activity_id);

        createMenuBar(true);
    }
}
