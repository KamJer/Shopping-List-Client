package pl.kamjer.shoppinglist.activity.recipe_activity.recipe_create.recycler_views;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Tag;

public class CreateTagsViewHolder extends RecyclerView.ViewHolder {

    private final AutoCompleteTextView etTag;
    private final ImageButton btnDeleteTag;
    private Tag tag;

    private final ArrayAdapter<Tag> tagAdapter;

    public CreateTagsViewHolder(@NonNull View itemView) {
        super(itemView);
        etTag = itemView.findViewById(R.id.tag_edit_text);
        tagAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        etTag.setAdapter(tagAdapter);
        btnDeleteTag = itemView.findViewById(R.id.delete_tag_button);

        setupListeners();
    }

    public void bind(Tag tag, View.OnClickListener deleteTagAction, List<Tag> tagsForHints) {
        this.tag = tag;
        tagAdapter.clear();
        tagAdapter.addAll(tagsForHints);
        tagAdapter.notifyDataSetChanged();
        etTag.setOnClickListener(view -> {
            etTag.showDropDown();
        });
        etTag.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etTag.showDropDown();
            }
        });

        etTag.setText(tag.getTag());
        btnDeleteTag.setOnClickListener(deleteTagAction);
    }

    private void setupListeners() {
        etTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tag.setTag(s.toString());
            }
        });
    }
}
