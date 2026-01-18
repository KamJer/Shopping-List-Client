package pl.kamjer.shoppinglist.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class RecipeRequestDto {
    List<String> products;
    Integer maxMissing;
}
