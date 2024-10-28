package pl.kamjer.shoppinglist.activity.shoppinglistactiviti.shoppingitemrecyclerview;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ShoppingItemLinearLayoutMenager extends LinearLayoutManager {
    public ShoppingItemLinearLayoutMenager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
