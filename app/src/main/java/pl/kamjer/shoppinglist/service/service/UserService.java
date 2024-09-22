package pl.kamjer.shoppinglist.service.service;

import java.time.LocalDateTime;

import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @POST("/user")
    Call<LocalDateTime> postUser(@Body UserDto user);

    @GET("/user/log/{userName}")
    Call<AllDto> logUser(@Path("userName") String userName);
}
