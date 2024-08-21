package pl.kamjer.shoppinglist.service;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.dto.AddDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {

    @PUT("/category")
    Call<LocalDateTime> putCategory(@Body CategoryDto category);

    @POST("/category")
    Call<AddDto> postCategory(@Body CategoryDto amountType);

    @DELETE("/category")
    Call<LocalDateTime> deleteCategory(@Query("categoryId") Long categoryId);
}
