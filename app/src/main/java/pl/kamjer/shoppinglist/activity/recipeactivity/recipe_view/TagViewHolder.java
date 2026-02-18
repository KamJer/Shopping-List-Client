package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Tag;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvTag;

    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTag = itemView.findViewById(R.id.tvTag);
    }

    public void bind(Tag tag) {
        tvTag.setText(tag.getTag());
    }
}
