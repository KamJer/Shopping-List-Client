package pl.kamjer.shoppinglist.service;

import java.time.LocalDateTime;

import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("/user")
    Call<LocalDateTime> postUser(@Body UserDto user);

    @DELETE("/user/{userName}")
    Call<Void> deleteUser(@Path("userName") String userName);

    @GET("/user")
    Call<LocalDateTime> getLastSaveTime(@Query("userName") String userName);

    @GET("/user/log/{userName}")
    Call<Boolean> logUser(@Path("userName") String userName);
}
