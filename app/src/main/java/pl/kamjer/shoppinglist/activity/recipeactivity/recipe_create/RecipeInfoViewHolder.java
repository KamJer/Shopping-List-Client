package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_create;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;

/**
 * ViewHolder implementation for managing recipe information fields in a RecyclerView.
 * This class handles the UI binding and data synchronization for recipe metadata fields
 * including name, source, description, publication status, and tags.
 */
public class RecipeInfoViewHolder extends RecyclerView.ViewHolder {

    /**
     * EditText field for entering the recipe name.
     * Displays and allows editing of the recipe's title.
     */
    private final EditText etName;

    /**
     * EditText field for entering the recipe source.
     * Contains information about where the recipe originates (e.g., book, website, author).
     */
    private final EditText etSource;

    /**
     * EditText field for entering the recipe description.
     * Provides a detailed overview or summary of the recipe.
     */
    private final EditText etDescription;

    /**
     * SwitchCompat widget for controlling the recipe's publication status.
     * Determines whether the recipe is published and visible to users.
     */
    private final SwitchCompat switchPublished;

    /**
     * EditText field for entering recipe tags.
     * Allows categorization and filtering of recipes through keyword tags.
     */
    private final EditText etTags;

    /**
     * Data holder object that maintains the current state of recipe information.
     * Serves as the intermediary between UI components and the underlying data model.
     */
    private RecipeInfoAdapter.DataHolder dataHolder;

    /**
     * Constructor for the ViewHolder.
     * Initializes all UI components and sets up text change listeners for data binding.
     *
     * @param itemView The root view of the recipe info item layout
     * @param dataHolder The data holder containing the recipe information to display and edit
     */
    public RecipeInfoViewHolder(@NonNull View itemView, RecipeInfoAdapter.DataHolder dataHolder) {
        super(itemView);

        etName = itemView.findViewById(R.id.etRecipeName);
        etSource = itemView.findViewById(R.id.etSource);
        etDescription = itemView.findViewById(R.id.etDescription);
        switchPublished = itemView.findViewById(R.id.switchPublished);
        etTags = itemView.findViewById(R.id.etTags);

        this.dataHolder = dataHolder;

        setupListeners();
    }

    /**
     * Binds the current recipe data to the UI components.
     * Populates all input fields with the values from the data holder.
     */
    public void bind() {
        etName.setText(dataHolder.getRecipeName());
        etSource.setText(dataHolder.getSource());
        etDescription.setText(dataHolder.getDescription());
        switchPublished.setChecked(dataHolder.isPublished());
        etTags.setText(dataHolder.getTags());
    }

    /**
     * Sets up text change listeners for all input fields and the switch widget.
     * Ensures that user input is immediately synchronized with the data holder,
     * maintaining data consistency between UI and model layers.
     */
    private void setupListeners() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                dataHolder.setRecipeName(s.toString());
            }
        });

        etSource.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                dataHolder.setSource(s.toString());
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                dataHolder.setDescription(s.toString());
            }
        });

        switchPublished.setOnCheckedChangeListener((buttonView, isChecked) -> dataHolder.setPublished(isChecked));

        etTags.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                dataHolder.setTags(s.toString());
            }
        });
    }
}
