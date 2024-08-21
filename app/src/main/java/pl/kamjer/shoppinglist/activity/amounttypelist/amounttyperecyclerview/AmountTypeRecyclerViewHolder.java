package pl.kamjer.shoppinglist.activity.amounttypelist.amounttyperecyclerview;

import android.media.Image;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.amounttypelist.functionalinterface.ModifyAmountTypeAction;
import pl.kamjer.shoppinglist.model.AmountType;

public class AmountTypeRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView amountTypeTextView;
    private ImageButton deleteAmountTypeButton;

    public AmountTypeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        amountTypeTextView = itemView.findViewById(R.id.amountTypeTextView);
        deleteAmountTypeButton = itemView.findViewById(R.id.deleteAmountTypeButton);
    }

    public void bind(AmountType amountType, ModifyAmountTypeAction deleteAmountTypeAction, ModifyAmountTypeAction updateAmountTypeAction) {
        amountTypeTextView.setText(amountType.getTypeName());
        deleteAmountTypeButton.setOnClickListener(v -> deleteAmountTypeAction.action(amountType));
    }
}
