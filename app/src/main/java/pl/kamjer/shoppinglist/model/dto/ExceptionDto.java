package pl.kamjer.shoppinglist.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExceptionDto extends Dto{
    private String massage;
    private StackTraceElement[] stackTrace;
}
