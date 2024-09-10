package pl.kamjer.shoppinglist.service.service;

import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.ExceptionDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UtilService {

    @POST("/util")
    Call<AllDto> synchronizeData(@Body AllDto allDto);

    @POST("/exception")
    Call<Void> sendLog(@Body ExceptionDto e);
}
