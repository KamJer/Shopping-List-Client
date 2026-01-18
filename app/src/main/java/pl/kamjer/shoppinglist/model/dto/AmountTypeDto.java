package pl.kamjer.shoppinglist.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;

@AllArgsConstructor
@Builder
@Getter
public class AmountTypeDto extends Dto {

    private long amountTypeId;
    private String typeName;
    private boolean deleted;
    private ModifyState modifyState;

    private long localId;

    private LocalDateTime savedTime;
}
