package pl.kamjer.shoppinglist.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Step {
    private Long stepId;
    private Integer stepNumber;
    private String instruction;
}
