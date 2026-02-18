package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.recipe.Tag;

@AllArgsConstructor
@Builder
@Getter
public class TagDto {
    private String tag;

    public static TagDto map(Tag tag) {
        return TagDto.builder().tag(tag.getTag()).build();
    }
}
