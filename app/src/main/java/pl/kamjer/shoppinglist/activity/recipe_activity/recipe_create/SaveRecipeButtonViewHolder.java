package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

/**
 * ViewHolder class for the "Save Recipe" button in recipe creation.
 * Manages the button's UI element and its click listener.
 */
public class SaveRecipeButtonViewHolder extends RecyclerView.ViewHolder {

    /**
     * Constructor for SaveRecipeButtonViewHolder.
     *
     * @param itemView           View containing the save recipe button layout
     * @param saveRecipeAction   Click listener for the save recipe button
     */
    public SaveRecipeButtonViewHolder(@NonNull View itemView, View.OnClickListener saveRecipeAction) {
        super(itemView);
        itemView.findViewById(R.id.btnSave).setOnClickListener(saveRecipeAction);
    }
}
