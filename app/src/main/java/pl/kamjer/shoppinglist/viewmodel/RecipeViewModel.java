package pl.kamjer.shoppinglist.viewmodel;

import static androidx.lifecycle.ViewModelKt.getViewModelScope;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview.RecipePagingSource;
import pl.kamjer.shoppinglist.activity.recipeactivity.user_recipe.UserRecipePagingSource;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
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

    public LiveData<PagingData<Recipe>> recipesLiveData;
    public MutableLiveData<Recipe> activeRecipeLiveData;
    public LiveData<PagingData<Recipe>> userRecipeLiveData;

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
        if (recipesLiveData == null) recipesLiveData = getRecipesLiveData(new RecipePagingSource(shoppingServiceRepository));
        if (activeRecipeLiveData == null) activeRecipeLiveData = new MutableLiveData<>();
        if (userRecipeLiveData == null)
            userRecipeLiveData = getRecipesForUserLiveData(new UserRecipePagingSource(shoppingServiceRepository));
        loadUser();
    }

    public void setUserRecipeLiveDataObserver(LifecycleOwner owner, Observer<PagingData<Recipe>> recipeObserver) {
        userRecipeLiveData.observe(owner, recipeObserver);
    }

    public void setRecipesLiveDataObserver(LifecycleOwner owner, Observer<PagingData<Recipe>> recipesObserver) {
        recipesLiveData.observe(owner, recipesObserver);
    }

    public void setActiveRecipeLiveDataObserver(LifecycleOwner owner, Observer<Recipe> recipesObserver) {
        activeRecipeLiveData.observe(owner, recipesObserver);
    }

    public void setActiveRecipe(Recipe recipe) {
        activeRecipeLiveData.postValue(recipe);
    }

    private LiveData<PagingData<Recipe>> getRecipesLiveData(RecipePagingSource recipePagingSource) {
        Pager<Integer, Recipe> pager = new Pager<>(
                new PagingConfig(ShoppingServiceRepository.PAGE_SIZE),
                () -> recipePagingSource
        );
        return PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                getViewModelScope(this));
    }

    public void performSearch(SearchMode searchMode, String query) {
        recipesLiveData = getRecipesLiveData(new RecipePagingSource(shoppingServiceRepository, query, searchMode));
    }

    private LiveData<PagingData<Recipe>> getRecipesForUserLiveData(UserRecipePagingSource recipePagingSource) {
        Pager<Integer, Recipe> pager = new Pager<>(
                new PagingConfig(ShoppingServiceRepository.PAGE_SIZE),
                () -> recipePagingSource
        );
        return PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                getViewModelScope(this));
    }

    public void loadRecipeUser() {
        userRecipeLiveData = getRecipesForUserLiveData(new UserRecipePagingSource(shoppingServiceRepository));
    }

    public void deleteRecipe(Recipe recipe) {
        shoppingServiceRepository.deleteRecipe(recipe.getRecipeId(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body()) {
//                            deleteRecipeFromLiveData(recipe);
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

//    public void getRecipesForUser(int page) {
//        shoppingServiceRepository.getRecipesForUser(getUserValue().getUserName(), page, new Callback<>() {
//            @Override
//            public void onResponse(@NonNull Call<Page<RecipeDto>> call, @NonNull Response<Page<RecipeDto>> response) {
//                if (response.code() == 200) {
//                    if (response.body() != null)
//                        userRecipeLiveData.postValue(response.body().map(Recipe::map));
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Page<RecipeDto>> call, @NonNull Throwable t) {
////            TODO: handle failure
//            }
//        });
//    }

//    private Page<Recipe> getUserRecipesLiveDataValue() {
//        return Optional.ofNullable(userRecipeLiveData.getValue()).orElse(new Page<>());
//    }

//    private void postUserRecipesLiveDataValue(Page<Recipe> recipes) {
//        userRecipeLiveData.postValue(recipes);
//    }
//
//    private void deleteRecipeFromLiveData(Recipe recipe) {
//        Page<Recipe> recipes = getUserRecipesLiveDataValue();
//        recipes.remove(recipe);
//        postUserRecipesLiveDataValue(recipes);
//    }
}
