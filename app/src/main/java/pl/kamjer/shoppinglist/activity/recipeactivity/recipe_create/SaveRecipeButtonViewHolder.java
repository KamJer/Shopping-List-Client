package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

public class SaveRecipeButtonViewHolder extends RecyclerView.ViewHolder {

    public SaveRecipeButtonViewHolder(@NonNull View itemView, View.OnClickListener saveRecipeAction) {
        super(itemView);
        itemView.findViewById(R.id.btnSave).setOnClickListener(saveRecipeAction);
    }
}
