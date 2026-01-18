package pl.kamjer.shoppinglist.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.dto.RecipeRequestDto;
import pl.kamjer.shoppinglist.model.dto.TagDto;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class RecipeViewModel extends ViewModel {

    public enum SearchMode {
        NAME,
        INGREDIENTS,
        TAGS,
        TAGS_REQUIRED,
        NONE;

        public static SearchMode getModeBySelection(Context context, String selection) {

           if (context.getString(R.string.with_name_search_menu_txt).equals(selection)) {
               return NAME;
           } else if (context.getString(R.string.with_ingredients_search_menu_txt).equals(selection)) {
                return INGREDIENTS;
           } else if (context.getString(R.string.with_tags_search_menu_txt).equals(selection)) {
               return TAGS;
           } else if (context.getString(R.string.with_tags_required_search_menu_txt).equals(selection)) {
               return TAGS_REQUIRED;
           }
           return NONE;
        }
    }
    private final ShoppingServiceRepository repository;
    private final ObjectMapper objectMapper;

    public RecipeViewModel(ShoppingServiceRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
    }

    public static final ViewModelInitializer<RecipeViewModel> initializer =
            new ViewModelInitializer<>(RecipeViewModel.class,
                    creationExtras -> new RecipeViewModel(
                            ShoppingServiceRepository.getShoppingServiceRepository()));

    public MutableLiveData<Recipe> recipeLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();

    public void performSearch(SearchMode searchMode, String query) {
        switch (searchMode) {
            case NAME -> getRecipesByQuery(query);
            case INGREDIENTS -> getRecipesByProducts(getRequestDtoFromQuery(query, 2));
            case TAGS -> getRecipesByTags(getTagDtoSetFromQuery(query));
            case TAGS_REQUIRED -> getRecipesByTagsRequired(getTagDtoSetFromQuery(query));
        }
    }

    private RecipeRequestDto getRequestDtoFromQuery(String request, int maxMissing) {
        String[] products = request.trim().toLowerCase(Locale.ROOT).split(",");
        return RecipeRequestDto.builder()
                .products(List.of(products))
                .maxMissing(maxMissing)
                .build();
    }

    private Set<TagDto> getTagDtoSetFromQuery(String query) {
        String[] tags = query.trim().toLowerCase(Locale.ROOT).split(",");
        return Arrays.stream(tags).sequential().map(s -> TagDto.builder().tag(s).build()).collect(Collectors.toSet());
    }

    public void insertRecipe(RecipeDto recipeDto) {
        repository.insertRecipe(recipeDto, new Callback<RecipeDto>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDto> call, @NonNull Response<RecipeDto> response) {
                recipeLiveData.postValue(objectMapper.convertValue(response.body(), Recipe.class));
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDto> call, @NonNull Throwable t) {
                //TODO: handle failure
            }
        });
    }

    public void updateRecipe(RecipeDto recipeDto) {
        repository.updateRecipe(recipeDto, new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                //TODO: handle failure
            }
        });
    }

    public void deleteRecipe(Long id) {
        repository.deleteRecipe(id, new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                //TODO: handle failure
            }
        });
    }

    public void getRecipesByProducts(RecipeRequestDto requestDto) {
        repository.getRecipesByProducts(requestDto, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                recipesLiveData.postValue(objectMapper.convertValue(response.body(), new TypeReference<List<Recipe>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesByQuery(String query) {
        repository.getRecipesByQuery(query, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                recipesLiveData.postValue(response.body().stream().map(recipeDto -> objectMapper.convertValue(recipeDto, Recipe.class)).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
                Log.i("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void getRecipesByTags(Set<TagDto> tags) {
        repository.getRecipesByTags(tags, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                recipesLiveData.postValue(objectMapper.convertValue(response.body(), new TypeReference<List<Recipe>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesByTagsRequired(Set<TagDto> tags) {
        repository.getRecipesByTagsRequired(tags, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                recipesLiveData.postValue(objectMapper.convertValue(response.body(), new TypeReference<List<Recipe>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesForUser(String userName) {
        repository.getRecipesForUser(userName, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                recipesLiveData.postValue(objectMapper.convertValue(response.body(), new TypeReference<List<Recipe>>() {
                }));
            }

            @Override
            public void onFailure(Call<List<RecipeDto>> call, Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void insertRecipeForUser(Long recipeId) {
        repository.insertRecipeForUser(recipeId, new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void deleteRecipeForUser(Long recipeId) {
        repository.deleteRecipeForUser(recipeId, new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }
}
