package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.ModifyState;

@AllArgsConstructor
@Builder
@Getter
public class AmountTypeDto {

    private long amountTypeId;
    private String typeName;
    private boolean deleted;
    private ModifyState modifyState;

    private long localId;
}
