package pl.kamjer.shoppinglist.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AllDto extends Dto {

    private List<AmountTypeDto> amountTypeDtoList;
    private List<CategoryDto> categoryDtoList;
    private List<ShoppingItemDto> shoppingItemDtoList;
    private LocalDateTime savedTime;
    private Boolean dirty;
}
