package pl.kamjer.shoppinglist.model.recipe;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Tag {
    private String tag;

    @NonNull
    @Override
    public String toString() {
        return tag;
    }
}
