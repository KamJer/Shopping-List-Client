package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

public class CreateIngredientButtonViewHolder extends RecyclerView.ViewHolder {

    private Button createIngredientBtn;

    public CreateIngredientButtonViewHolder(@NonNull View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        createIngredientBtn = itemView.findViewById(R.id.btnAddIngredient);
        createIngredientBtn.setOnClickListener(onClickListener);
    }
}
