package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Tag;

/**
 * Adapter class for managing tag items in a RecyclerView for recipe viewing.
 * Handles the binding of Tag objects to TagViewHolder.
 */
@Getter
public class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {

    /**
     * List of Tag objects to be displayed in the RecyclerView.
     * Contains all the tags for the recipe being viewed.
     */
    private List<Tag> tags = new ArrayList<>();

    /**
     * Creates a new ViewHolder instance for a tag item.
     *
     * @param parent   Parent ViewGroup that will contain the ViewHolder
     * @param viewType View type of the ViewHolder
     * @return New TagViewHolder instance
     */
    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_card_layout, parent, false);
        return new TagViewHolder(view);
    }

    /**
     * Binds data from a Tag object to a ViewHolder.
     *
     * @param holder   TagViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(tags.get(position));
    }

    /**
     * Returns the total number of items in the tag list.
     *
     * @return Number of tags in the list
     */
    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }
}
