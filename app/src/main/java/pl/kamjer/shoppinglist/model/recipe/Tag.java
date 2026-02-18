package pl.kamjer.shoppinglist.model.recipe;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static Set<Tag> normalizeTags(String tags) {
        return Stream.of(tags.trim().split(",")).map(s -> Tag.builder().tag(s).build()).collect(Collectors.toSet());
    }

    public static String denormalizeTags(Set<Tag> tags) {
        return tags.stream().map(Tag::getTag).collect(Collectors.joining(", "));
    }
}
