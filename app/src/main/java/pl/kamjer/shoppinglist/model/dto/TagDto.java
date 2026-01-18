package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class TagDto {
    private String tag;
}
