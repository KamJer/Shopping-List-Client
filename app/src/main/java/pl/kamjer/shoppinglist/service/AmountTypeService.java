package pl.kamjer.shoppinglist.service;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.dto.AddDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AmountTypeService {

    @POST("/amountType")
    Call<AddDto> postAmountType(@Body AmountTypeDto amountType);

    @PUT("/amountType")
    Call<LocalDateTime> putAmountType(@Body AmountTypeDto amountType);

    @DELETE("/amountType")
    Call<LocalDateTime> deleteAmountType(@Query("amountTypeId") Long amountTypeId, @Query("userName") String userName);
}
