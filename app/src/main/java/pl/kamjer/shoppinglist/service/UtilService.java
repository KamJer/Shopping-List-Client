package pl.kamjer.shoppinglist.service;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AllIdDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UtilService {

//    @GET("/util/time")
//    Call<AllDto> getAllDto(@Query("savedTime") LocalDateTime savedTime);
//
//    @GET("/util")
//    Call<AllDto> getAllDto();
//
//    @POST("/util/post")
//    Call<AllIdDto> postAllElements(@Body AllDto elements);
//
//    @PUT("/util")
//    Call<LocalDateTime> putAllElements(@Body AllDto elements);
//
//    @POST("/util/delete")
//    Call<LocalDateTime> deleteAllDto(@Body AllDto allDto);

    @POST("/util")
    Call<AllDto> synchronizeData(@Body AllDto allDto);
}
