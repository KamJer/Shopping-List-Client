package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.recipe.Step;

@AllArgsConstructor
@Builder
@Getter
public class StepDto {
    private Integer stepNumber;
    private String instruction;

    public static StepDto map(Step step) {
        return StepDto.builder()
                .stepNumber(step.getStepNumber())
                .instruction(step.getInstruction())
                .build();
    }
}
