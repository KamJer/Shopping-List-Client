package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorMessage {
    private String field;
    private String message;
}
