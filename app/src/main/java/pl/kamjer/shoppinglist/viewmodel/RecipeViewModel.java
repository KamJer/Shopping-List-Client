package pl.kamjer.shoppinglist.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.dto.Page;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.dto.RecipeRequestDto;
import pl.kamjer.shoppinglist.model.dto.TagDto;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.funcinterface.OnSuccessAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeViewModel extends CustomViewModel {

    public Optional<Recipe> getActiveRecipeLiveDataValue() {
        return Optional.ofNullable(activeRecipeLiveData.getValue());
    }

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

    public MutableLiveData<Page<Recipe>> recipesLiveData;
    public MutableLiveData<Recipe> activeRecipeLiveData;
    public MutableLiveData<Page<Recipe>> userRecipeLiveData;

    public RecipeViewModel(ShoppingRepository shoppingRepository,
                           ShoppingServiceRepository shoppingServiceRepository,
                           SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<RecipeViewModel> initializer =
            new ViewModelInitializer<>(RecipeViewModel.class,
                    creationExtras -> new RecipeViewModel(
                            ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository()
                    ));


    public void initialize() {
        if (recipesLiveData == null) recipesLiveData = new MutableLiveData<>(new Page<>());
        if (activeRecipeLiveData == null) activeRecipeLiveData = new MutableLiveData<>();
        if (userRecipeLiveData == null)
            userRecipeLiveData = new MutableLiveData<>(new Page<>());
        loadUser();
    }

    public void setUserRecipeLiveDataObserver(LifecycleOwner owner, Observer<Page<Recipe>> recipeObserver) {
        userRecipeLiveData.observe(owner, recipeObserver);
    }

    public void setRecipesLiveDataObserver(LifecycleOwner owner, Observer<Page<Recipe>> recipesObserver) {
        recipesLiveData.observe(owner, recipesObserver);
    }

    public void setActiveRecipeLiveDataObserver(LifecycleOwner owner, Observer<Recipe> recipesObserver) {
        activeRecipeLiveData.observe(owner, recipesObserver);
    }

    public void setActiveRecipe(Recipe recipe) {
        activeRecipeLiveData.postValue(recipe);
    }

    public void performSearch(SearchMode searchMode, String query) {
        switch (searchMode) {
            case NAME -> getRecipesByQuery(query, 0);
            case INGREDIENTS -> getRecipesByProducts(getRequestDtoFromQuery(query, 2));
            case TAGS -> getRecipesByTags(getTagDtoSetFromQuery(query), 0);
            case TAGS_REQUIRED -> getRecipesByTagsRequired(getTagDtoSetFromQuery(query), 0);
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

    public void insertRecipe(RecipeDto recipeDto, OnSuccessAction action) {
        shoppingServiceRepository.insertRecipe(recipeDto, new Callback<RecipeDto>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDto> call, @NonNull Response<RecipeDto> response) {
                action.onSuccess();
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

    public void deleteRecipe(Recipe recipe) {
        shoppingServiceRepository.deleteRecipe(recipe.getRecipeId(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body()) {
                            deleteRecipeFromLiveData(recipe);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                //TODO: handle failure
            }
        });
    }

    public void getRecipesByProducts(RecipeRequestDto requestDto) {
        shoppingServiceRepository.getRecipesByProducts(requestDto, new Callback<Page<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
                if (response.code() == 200)
                    if (response.body() != null) {
                        Page<RecipeDto> recipeDtoPage = response.body();
                        Page<Recipe> recipePage = recipeDtoPage.map(Recipe::map);
                        recipesLiveData.postValue(recipePage);
                    }
            }

            @Override
            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesByQuery(String query, int page) {
        shoppingServiceRepository.getRecipesByQuery(query, page, new Callback<Page<RecipeDto>>() {
            @Override
            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        recipesLiveData.postValue(response.body().map(Recipe::map));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
                Log.i("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void getRecipesByTags(Set<TagDto> tags, int page) {
        shoppingServiceRepository.getRecipesByTags(tags, page,new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        recipesLiveData.postValue(response.body().map(Recipe::map));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesByTagsRequired(Set<TagDto> tags, int page) {
        shoppingServiceRepository.getRecipesByTagsRequired(tags, page, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
                if (response.code() == 200) {
                    if (response.body() != null)
                        recipesLiveData.postValue(response.body().map(Recipe::map));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getRecipesForUser(int page) {
        shoppingServiceRepository.getRecipesForUser(getUserValue().getUserName(), page, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
                if (response.code() == 200) {
                    if (response.body() != null)
                        userRecipeLiveData.postValue(response.body().map(Recipe::map));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void insertRecipeForUser(Long recipeId) {
        shoppingServiceRepository.insertRecipeForUser(recipeId, new Callback<>() {
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
        shoppingServiceRepository.deleteRecipeForUser(recipeId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    public void getAllRecipes(int page) {
        shoppingServiceRepository.getAllRecipes(page, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
                if (response.code() == 200) {
                    if (response.body() != null)
                        recipesLiveData.postValue(response.body().map(Recipe::map));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
//            TODO: handle failure
            }
        });
    }

    private Page<Recipe> getUserRecipesLiveDataValue() {
        return Optional.ofNullable(userRecipeLiveData.getValue()).orElse(new Page<>());
    }

    private void postUserRecipesLiveDataValue(Page<Recipe> recipes) {
        userRecipeLiveData.postValue(recipes);
    }

    private void deleteRecipeFromLiveData(Recipe recipe) {
        Page<Recipe> recipes = getUserRecipesLiveDataValue();
        recipes.remove(recipe);
        postUserRecipesLiveDataValue(recipes);
    }

    private void addRecipeToLiveData(Recipe recipe) {
        Page<Recipe> recipes = getUserRecipesLiveDataValue();
        recipes.add(recipe);
        postUserRecipesLiveDataValue(recipes);
    }
}
