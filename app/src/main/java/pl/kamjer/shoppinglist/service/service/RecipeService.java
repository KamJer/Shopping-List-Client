package pl.kamjer.shoppinglist.service.service;

import java.util.List;
import java.util.Set;

import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.dto.RecipeRequestDto;
import pl.kamjer.shoppinglist.model.dto.TagDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RecipeService {

    @PUT("/recipe")
    Call<RecipeDto> putRecipe(@Body RecipeDto recipeDto);

    @POST("/recipe")
    Call<Boolean> postRecipe(@Body RecipeDto recipeDto);

    @DELETE("/recipe")
    Call<Boolean> deleteRecipe(@Body Long id);

    @PUT("/recipe/for_user/{recipeId}")
    Call<Boolean> putRecipeForUser(@Path("recipeId") Long recipeId);

    @DELETE("/recipe/for_user/{recipeId}")
    Call<Boolean> deleteRecipeForUser(@Path("recipeId") Long recipeId);

    @POST("/recipe/products")
    Call<List<RecipeDto>> getRecipeByProducts(@Body RecipeRequestDto recipeRequestDto);

    @GET("/recipe/name/{query}")
    Call<List<RecipeDto>> getRecipeByQuery(@Path("query") String query);

    @POST("/recipe/tag")
    Call<List<RecipeDto>> getRecipeByTags(@Body Set<TagDto> tags);

    @GET("/recipe/user/{userName}")
    Call<List<RecipeDto>> getRecipeForUser(@Path("userName") String userName);

    @POST("/recipe/tag/required")
    Call<List<RecipeDto>> getRecipeByTagsRequired(@Body Set<TagDto> tags);
}

