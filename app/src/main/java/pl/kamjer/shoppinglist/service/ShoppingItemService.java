package pl.kamjer.shoppinglist.service;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.dto.AddDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShoppingItemService {

    @POST("/shoppingItem")
    Call<AddDto> postShoppingItem(@Body ShoppingItemDto amountType);

    @PUT("/shoppingItem")
    Call<LocalDateTime> putShoppingItem(@Body ShoppingItemDto amountType);

    @DELETE("/shoppingItem")
    Call<LocalDateTime> deleteShoppingItem(@Query("shoppingItemId") Long shoppingItemId);
}
