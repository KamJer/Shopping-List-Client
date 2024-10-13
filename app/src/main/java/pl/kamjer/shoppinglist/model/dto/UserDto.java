package pl.kamjer.shoppinglist.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserDto extends Dto{

    private String userName;
    private String password;
}
