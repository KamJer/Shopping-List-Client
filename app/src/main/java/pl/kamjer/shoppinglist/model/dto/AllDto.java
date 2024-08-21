package pl.kamjer.shoppinglist.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AllDto {

    private List<AmountTypeDto> amountTypeDtoList;
    private List<CategoryDto> categoryDtoList;
    private List<ShoppingItemDto> shoppingItemDtoList;
    private LocalDateTime savedTime;
}
