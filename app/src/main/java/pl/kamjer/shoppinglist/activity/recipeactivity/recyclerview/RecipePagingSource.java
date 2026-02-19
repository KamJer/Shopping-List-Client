package pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.Page;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.dto.RecipeRequestDto;
import pl.kamjer.shoppinglist.model.dto.TagDto;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Paging source implementation for loading recipes with pagination support.
 * This class handles loading recipes from the repository with support for different search modes
 * and pagination functionality.
 */
@AllArgsConstructor
@Setter
@Getter
public class RecipePagingSource extends ListenableFuturePagingSource<Integer, Recipe> {

    /**
     * Repository for accessing recipe data through API calls.
     */
    private ShoppingServiceRepository shoppingServiceRepository;

    /**
     * Search query string used for filtering recipes.
     */
    private String query;

    /**
     * Current search mode that determines how recipes are filtered.
     */
    private RecipeViewModel.SearchMode searchMode;

    /**
     * Executor for handling network operations on a separate thread.
     */
    private final Executor networkExecutor = Executors.newSingleThreadExecutor();

    /**
     * Constructor for initializing the paging source with repository and default values.
     *
     * @param shoppingServiceRepository Repository for accessing recipe data
     */
    public RecipePagingSource(ShoppingServiceRepository shoppingServiceRepository) {
        this.shoppingServiceRepository = shoppingServiceRepository;
        this.query = "";
        this.searchMode = RecipeViewModel.SearchMode.NONE;
    }

    /**
     * Loads a page of recipes based on the provided load parameters.
     * This method handles different search modes and executes network requests asynchronously.
     *
     * @param loadParams Parameters containing pagination information and load key
     * @return ListenableFuture containing the loaded recipe data or error
     */
    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, Recipe>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        int page = loadParams.getKey() != null ? loadParams.getKey() : 0;

        SettableFuture<LoadResult<Integer, Recipe>> future = SettableFuture.create();

        networkExecutor.execute(() -> {
            try {
                Call<Page<RecipeDto>> call = shoppingServiceRepository.getRecipeService().getAllRecipes(page, ShoppingServiceRepository.PAGE_SIZE);

                if (searchMode != null) {
                    switch (searchMode) {
                        case NAME -> call = shoppingServiceRepository.getRecipeService().getRecipeByQuery(query, page, ShoppingServiceRepository.PAGE_SIZE);
                        case INGREDIENTS -> call = shoppingServiceRepository.getRecipeService()
                                .getRecipeByProducts(getRequestDtoFromQuery(query, 0), page, ShoppingServiceRepository.PAGE_SIZE);
                        case TAGS -> call = shoppingServiceRepository.getRecipeService().getRecipeByTags(getTagDtoSetFromQuery(query), page, ShoppingServiceRepository.PAGE_SIZE);
                        case TAGS_REQUIRED -> call = shoppingServiceRepository.getRecipeService().getRecipeByTagsRequired(getTagDtoSetFromQuery(query), page, ShoppingServiceRepository.PAGE_SIZE);
                    }
                }

                Response<Page<RecipeDto>> response = call.execute();

                if (!response.isSuccessful() || response.body() == null) {
                    future.set(new LoadResult.Error<>(new Exception("HTTP " + response.code())));
                    return;
                }

                Page<Recipe> pageResult = response.body().map(Recipe::map);

                Integer prevKey = page == 0 ? null : page - 1;
                Integer nextKey = pageResult.isLast() ? null : page + 1;

                future.set(new LoadResult.Page<>(pageResult.getContent(), prevKey, nextKey));

            } catch (Exception e) {
                future.set(new LoadResult.Error<>(e));
            }
        });

        return future;
    }

    /**
     * Determines the key to use for refreshing the paging data.
     * This method helps maintain proper pagination state when refreshing data.
     *
     * @param pagingState Current paging state information
     * @return Key for refresh operation or null if unable to determine
     */
    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Recipe> pagingState) {
        Integer anchorPosition = pagingState.getAnchorPosition();
        if (anchorPosition == null) return null;

        LoadResult.Page<Integer, Recipe> anchorPage =
                pagingState.closestPageToPosition(anchorPosition);

        if (anchorPage == null) return null;

        if (anchorPage.getPrevKey() != null) {
            return anchorPage.getPrevKey() + 1;
        }

        if (anchorPage.getNextKey() != null) {
            return anchorPage.getNextKey() - 1;
        }

        return null;
    }

    /**
     * Converts a query string into a RecipeRequestDto for ingredient-based searching.
     *
     * @param request Query string containing comma-separated ingredients
     * @param maxMissing Maximum number of missing ingredients allowed
     * @return RecipeRequestDto configured with the parsed ingredients
     */
    private RecipeRequestDto getRequestDtoFromQuery(String request, int maxMissing) {
        String[] products = request.trim().toLowerCase(Locale.ROOT).split(",");
        for (int i = 0; i < products.length; i++) {
            products[i] = products[i].trim();
        }
        return RecipeRequestDto.builder()
                .products(List.of(products))
                .maxMissing(maxMissing)
                .build();
    }

    /**
     * Converts a query string into a set of TagDto objects for tag-based searching.
     *
     * @param query Query string containing comma-separated tags
     * @return Set of TagDto objects parsed from the query
     */
    private Set<TagDto> getTagDtoSetFromQuery(String query) {
        String[] tags = query.trim().toLowerCase(Locale.ROOT).split(",");
        return Arrays.stream(tags).sequential().map(s -> TagDto.builder().tag(s).build()).collect(Collectors.toSet());
    }
}
