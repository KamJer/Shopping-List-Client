package pl.kamjer.shoppinglist.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.ModifyState;

@AllArgsConstructor
@Builder
@Getter
public class ShoppingItemDto {

    private Long shoppingItemId;
    private Long itemAmountTypeId;
    private Long itemCategoryId;
    private String itemName;
    private Double amount;
    private boolean bought;
    private boolean deleted;
    private ModifyState modifyState;

}
