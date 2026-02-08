package pl.kamjer.shoppinglist.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.dto.RecipeRequestDto;
import pl.kamjer.shoppinglist.model.dto.TagDto;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeSearchViewModel extends CustomViewModel {

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

    public MutableLiveData<List<Recipe>> recipesLiveData;

    public RecipeSearchViewModel(ShoppingRepository shoppingRepository,
                                 ShoppingServiceRepository shoppingServiceRepository,
                                 SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<RecipeSearchViewModel> initializer =
            new ViewModelInitializer<>(RecipeSearchViewModel.class,
                    creationExtras -> new RecipeSearchViewModel(
                            ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository()
                    ));


    public void initialize() {
        recipesLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public void setRecipesLiveDataObserver(LifecycleOwner owner, Observer<List<Recipe>> recipesObserver) {
        recipesLiveData.observe(owner, recipesObserver);
    }

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
        shoppingServiceRepository.insertRecipe(recipeDto, new Callback<RecipeDto>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDto> call, @NonNull Response<RecipeDto> response) {
                //TODO: handle success
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDto> call, @NonNull Throwable t) {
                //TODO: handle failure
            }
        });
    }

    public void updateRecipe(RecipeDto recipeDto) {
        shoppingServiceRepository.updateRecipe(recipeDto, new Callback<Boolean>() {
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
        shoppingServiceRepository.deleteRecipe(id, new Callback<Boolean>() {
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
        shoppingServiceRepository.getRecipesByProducts(requestDto, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                assert response.body() != null;
                recipesLiveData.postValue(response.body().stream().map(Recipe::map).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesByQuery(String query) {
        shoppingServiceRepository.getRecipesByQuery(query, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                assert response.body() != null;
                recipesLiveData.postValue(response.body().stream().map(Recipe::map).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
                Log.i("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void getRecipesByTags(Set<TagDto> tags) {
        shoppingServiceRepository.getRecipesByTags(tags, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                assert response.body() != null;
                recipesLiveData.postValue(response.body().stream().map(Recipe::map).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesByTagsRequired(Set<TagDto> tags) {
        shoppingServiceRepository.getRecipesByTagsRequired(tags, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                assert response.body() != null;
                recipesLiveData.postValue(response.body().stream().map(Recipe::map).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesForUser(String userName) {
        shoppingServiceRepository.getRecipesForUser(userName, new Callback<List<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeDto>> call, @NonNull Response<List<RecipeDto>> response) {
                assert response.body() != null;
                recipesLiveData.postValue(response.body().stream().map(Recipe::map).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void insertRecipeForUser(Long recipeId) {
        shoppingServiceRepository.insertRecipeForUser(recipeId, new Callback<Boolean>() {
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
        shoppingServiceRepository.deleteRecipeForUser(recipeId, new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public Optional<List<Recipe>> getRecipesLiveDataValue() {
        return Optional.ofNullable(recipesLiveData.getValue());
    }

}
