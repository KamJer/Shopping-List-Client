package pl.kamjer.shoppinglist.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AllIdDto {
    private List<Long> amountTypeIds;
    private List<Long> categoriesIds;
    private List<Long> shoppingItemsIds;

    private LocalDateTime savedTime;

}
