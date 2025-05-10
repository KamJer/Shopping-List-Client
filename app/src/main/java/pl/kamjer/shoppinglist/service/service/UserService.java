package pl.kamjer.shoppinglist.service.service;

import java.time.LocalDateTime;

import pl.kamjer.shoppinglist.model.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("/user")
    Call<LocalDateTime> postUser(@Body UserDto user);

    @POST("/user/log")
    Call<Boolean> logUser(@Body UserDto user);
}
