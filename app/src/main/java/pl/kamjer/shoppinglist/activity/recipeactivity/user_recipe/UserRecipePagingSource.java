package pl.kamjer.shoppinglist.activity.recipeactivity.user_recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.model.dto.Page;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import retrofit2.Call;
import retrofit2.Response;

@AllArgsConstructor
public class UserRecipePagingSource extends ListenableFuturePagingSource<Integer, Recipe> {

    private ShoppingServiceRepository shoppingServiceRepository;
    private final Executor networkExecutor = Executors.newSingleThreadExecutor();

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, Recipe>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        int page = loadParams.getKey() != null ? loadParams.getKey() : 0;

        SettableFuture<LoadResult<Integer, Recipe>> future = SettableFuture.create();

        networkExecutor.execute(() -> {
            try {
                Call<Page<RecipeDto>> call = shoppingServiceRepository.getRecipeService().getRecipeForUser(page, ShoppingServiceRepository.PAGE_SIZE);

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
}
