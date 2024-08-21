package pl.kamjer.shoppinglist.model.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import retrofit2.http.Body;

@Builder
@Getter
public class AddDto {
    private Long newId;
    private LocalDateTime savedTime;
}
