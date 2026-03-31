package pl.kamjer.shoppinglist.service.service;

import java.time.LocalDateTime;

import pl.kamjer.shoppinglist.model.dto.TokenDto;
import pl.kamjer.shoppinglist.model.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {

    @POST("/user")
    Call<LocalDateTime> postUser(@Body UserDto user);

    @POST("/user/log")
    Call<TokenDto> loginUser(@Body UserDto user);

    @GET("/user/refresh")
    Call<TokenDto> refreshUser();

    @GET("/user/refresh")
    Call<TokenDto> refreshUser(@Header("Authorization") String token);
}
