package pl.kamjer.shoppinglist.service.service;

import java.util.Set;

import pl.kamjer.shoppinglist.model.dto.Page;
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
import retrofit2.http.Query;

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
    Call<Page<RecipeDto>> getRecipeByProducts(@Body RecipeRequestDto recipeRequestDto, @Query("page") int page, @Query("size") int size);

    @GET("/recipe/name/{query}")
    Call<Page<RecipeDto>> getRecipeByQuery(
            @Path("query") String query,
            @Query("page") int page,
            @Query("size") int size);

    @POST("/recipe/tag")
    Call<Page<RecipeDto>> getRecipeByTags(@Body Set<TagDto> tags, @Query("page") int page, @Query("size") int size);

    @GET("/recipe/user")
    Call<Page<RecipeDto>> getRecipeForUser(@Query("page") int page, @Query("size") int size);

    @POST("/recipe/tag/required")
    Call<Page<RecipeDto>> getRecipeByTagsRequired(@Body Set<TagDto> tags, @Query("page") int page, @Query("size") int size);

    @GET("/recipe")
    Call<Page<RecipeDto>> getAllRecipes(@Query("page") int page, @Query("size") int size);
}

