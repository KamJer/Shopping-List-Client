package pl.kamjer.shoppinglist.model.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.StepDto;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Step {
    private Long stepId;
    private Integer stepNumber;
    private String instruction;

    public static Step map(StepDto stepDto) {
        return Step.builder()
                .stepNumber(stepDto.getStepNumber())
                .instruction(stepDto.getInstruction())
                .build();
    }
}
