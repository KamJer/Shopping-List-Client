package pl.kamjer.shoppinglist.model.recipe;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.TagDto;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Tag {
    private String tag;

    public static Tag map(TagDto tagDto) {
        return Tag.builder()
                .tag(tagDto.getTag())
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return tag;
    }
}
