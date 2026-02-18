package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.R;

/**
 * RecyclerView adapter for managing recipe information fields.
 * This adapter handles the display and editing of recipe metadata including name, source,
 * description, publication status, and tags within a single-item RecyclerView.
 */
public class RecipeInfoAdapter extends RecyclerView.Adapter<RecipeInfoViewHolder>{

    /**
     * Data holder object that maintains the current state of recipe information.
     * Serves as the central data repository for all recipe metadata fields.
     */
    private DataHolder dataHolder = new DataHolder();

    /**
     * Creates a new ViewHolder instance for the recipe information card.
     * Inflates the recipe info layout and initializes the ViewHolder with the data holder.
     *
     * @param parent The parent ViewGroup that will contain the ViewHolder
     * @param viewType The view type of the new ViewHolder
     * @return A new RecipeInfoViewHolder instance
     */
    @NonNull
    @Override
    public RecipeInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_info_card_layout, parent, false);
        return new RecipeInfoViewHolder(view, dataHolder);
    }

    /**
     * Binds the current recipe data to the ViewHolder's UI components.
     * This method is called by the RecyclerView to display the recipe information.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeInfoViewHolder holder, int position) {
        holder.bind();
    }

    /**
     * Returns the number of items in the data set.
     * Since this adapter manages a single recipe information card, it always returns 1.
     *
     * @return The total number of items in this adapter
     */
    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * Updates the recipe data with new values and notifies the adapter of the change.
     * This method allows external components to set initial recipe information or update it.
     *
     * @param recipeName The name of the recipe
     * @param source The source of the recipe
     * @param description The description of the recipe
     * @param published The publication status of the recipe
     * @param tags The tags associated with the recipe
     */
    public void setData(String recipeName, String source, String description, boolean published, String tags) {
        dataHolder.recipeName = recipeName;
        dataHolder.source = source;
        dataHolder.description = description;
        dataHolder.published = published;
        dataHolder.tags = tags;
        notifyItemChanged(0);
    }

    /**
     * Returns the current data holder containing all recipe information.
     * Provides external components access to the current recipe metadata.
     *
     * @return The DataHolder object containing current recipe data
     */
    public DataHolder getData() {
        return dataHolder;
    }

    /**
     * Data holder class that encapsulates all recipe metadata fields.
     * This inner class provides a structured way to manage recipe information
     * and is used by the ViewHolder to synchronize UI with data.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class DataHolder {
        /**
         * The name/title of the recipe.
         */
        private String recipeName;

        /**
         * The source where the recipe originates (e.g., book, website, author).
         */
        private String source;

        /**
         * A detailed description or summary of the recipe.
         */
        private String description;

        /**
         * Indicates whether the recipe is published and visible to users.
         */
        private boolean published;

        /**
         * Tags associated with the recipe for categorization and filtering.
         */
        private String tags;
    }
}
