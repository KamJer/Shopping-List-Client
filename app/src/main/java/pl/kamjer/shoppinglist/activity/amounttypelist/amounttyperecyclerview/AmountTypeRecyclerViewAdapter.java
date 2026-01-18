package pl.kamjer.shoppinglist.activity.amounttypelist.amounttyperecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.amounttypelist.functionalinterface.ModifyAmountTypeAction;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;

@AllArgsConstructor
public class AmountTypeRecyclerViewAdapter extends RecyclerView.Adapter<AmountTypeRecyclerViewHolder>{

    private List<AmountType> amountTypeList;
    private ModifyAmountTypeAction deleteAmountTypeAction;
    private ModifyAmountTypeAction updateAmountTypeAction;

    @NonNull
    @Override
    public AmountTypeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.amount_type_view_holder_layout, parent, false);
        return new AmountTypeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmountTypeRecyclerViewHolder holder, int position) {
        holder.bind(amountTypeList.get(position), deleteAmountTypeAction, updateAmountTypeAction);
    }

    @Override
    public int getItemCount() {
        return amountTypeList.size();
    }
}
