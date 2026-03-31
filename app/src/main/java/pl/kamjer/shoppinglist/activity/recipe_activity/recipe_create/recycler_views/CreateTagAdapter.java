package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Tag;

@AllArgsConstructor
public class CreateTagAdapter extends RecyclerView.Adapter<CreateTagsViewHolder> {

    private List<Tag> tags;
    private List<Tag> tagHints;

    @NonNull
    @Override
    public CreateTagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_tag_card_layout, parent, false);
        return new CreateTagsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateTagsViewHolder holder, int position) {
        holder.bind(tags.get(position), view -> {
                    if (tags.size() != position) {
                        tags.remove(position);
                        notifyDataSetChanged();
                    }
                },
                tagHints);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void addEmptyTag() {
        tags.add(new Tag());
        notifyDataSetChanged();
    }

    public Set<Tag> getData() {
        return tags.stream().filter(tag -> !tag.getTag().isEmpty()).collect(Collectors.toSet());
    }

    public void setData(Set<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }
}
