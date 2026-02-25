package pl.kamjer.shoppinglist.viewmodel;

import static androidx.lifecycle.ViewModelKt.getViewModelScope;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipe_activity.recipe_recycler_view.RecipePagingSource;
import pl.kamjer.shoppinglist.activity.recipe_activity.user_recipe.UserRecipePagingSource;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnSuccessAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for managing recipe data and search functionality.
 * This ViewModel handles loading, searching, and displaying recipes using pagination.
 */
public class RecipeViewModel extends CustomViewModel {

    /**
     * LiveData for storing the list of recipes from the API.
     */
    private LiveData<PagingData<Recipe>> recipesLiveData;

    /**
     * MutableLiveData for storing the currently active recipe.
     */
    private MutableLiveData<Recipe> activeRecipeLiveData;

    /**
     * LiveData for storing the list of user-specific recipes.
     */
    private LiveData<PagingData<Recipe>> userRecipeLiveData;

    /**
     * LiveData for storing bought shopping items.
     */
    private LiveData<List<ShoppingItem>> boughtShoppingItems;

    /**
     * LiveData for storing all shopping items for the current user.
     */
    private LiveData<List<ShoppingItem>> shoppingItemListLiveData;

    /**
     * MutableLiveData for storing the currently active ingredient.
     */
    private final MutableLiveData<Ingredient> activeIngredient = new MutableLiveData<>();

    /**
     * MediatorLiveData for combining recipe and shopping items data.
     */
    private final MediatorLiveData<Pair<Recipe, List<ShoppingItem>>> recipeWithShoppingItems = new MediatorLiveData<>();

    /**
     * Current recipe being displayed.
     */
    private Recipe currentRecipe;

    /**
     * Current list of shopping items.
     */
    private List<ShoppingItem> currentShoppingItems;

    /**
     * Flag to track if the mediator has been initialized.
     */
    private boolean mediatorInitialized = false;

    /**
     * Returns the current value of the active recipe LiveData wrapped in an Optional.
     *
     * @return Optional containing the active recipe or empty if no recipe is active
     */
    public Optional<Recipe> getActiveRecipeLiveDataValue() {
        return Optional.ofNullable(activeRecipeLiveData.getValue());
    }

    /**
     * Sets up observers for bought shopping items LiveData.
     *
     * @param owner    The LifecycleOwner that will observe the LiveData
     * @param observer The observer that will receive updates
     */
    public void setBoughtShoppingItemsLiveDataObservers(LifecycleOwner owner, Observer<List<ShoppingItem>> observer) {
        boughtShoppingItems.observe(owner, observer);
    }

    /**
     * Removes observers from recipes LiveData.
     *
     * @param owner The LifecycleOwner whose observers should be removed
     */
    public void removeRecipesLiveDataObserver(LifecycleOwner owner) {
        recipesLiveData.removeObservers(owner);
    }

    /**
     * Removes observers from bought shopping items LiveData.
     *
     * @param owner The LifecycleOwner whose observers should be removed
     */
    public void removeBoughtLiveDataObserver(LifecycleOwner owner) {
        boughtShoppingItems.removeObservers(owner);
    }

    /**
     * Refreshes the recipes data by creating new LiveData with updated RecipePagingSource.
     */
    public void refreshRecipesData() {
        recipesLiveData = getRecipesLiveData(new RecipePagingSource(shoppingServiceRepository));
    }

    /**
     * Enum representing different search modes for recipe filtering.
     */
    public enum SearchMode {
        NAME,
        INGREDIENTS,
        TAGS,
        TAGS_REQUIRED,
        NONE;

        /**
         * Converts a string selection to the corresponding SearchMode enum.
         *
         * @param context   The context for accessing string resources
         * @param selection The string representation of the search mode
         * @return The corresponding SearchMode enum value
         */
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

    /**
     * Constructor for RecipeViewModel.
     *
     * @param shoppingRepository        Repository for shopping data
     * @param shoppingServiceRepository Repository for shopping service data
     * @param sharedRepository          Repository for shared preferences
     */
    public RecipeViewModel(ShoppingRepository shoppingRepository,
                           ShoppingServiceRepository shoppingServiceRepository,
                           SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating RecipeViewModel instances.
     */
    public static final ViewModelInitializer<RecipeViewModel> initializer =
            new ViewModelInitializer<>(RecipeViewModel.class,
                    creationExtras -> new RecipeViewModel(
                            ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository()
                    ));

    /**
     * Initializes the ViewModel by setting up LiveData objects if they haven't been initialized yet.
     */
    @Override
    public void initialize() {
        super.initialize();

        // Initialize recipes LiveData if not already done
        if (recipesLiveData == null)
            recipesLiveData = getRecipesLiveData(new RecipePagingSource(shoppingServiceRepository));

        // Initialize active recipe LiveData if not already done
        if (activeRecipeLiveData == null)
            activeRecipeLiveData = new MutableLiveData<>();

        // Initialize user recipe LiveData if not already done
        if (userRecipeLiveData == null)
            userRecipeLiveData = getRecipesForUserLiveData(new UserRecipePagingSource(shoppingServiceRepository));

        // Initialize bought shopping items LiveData if not already done
        if (boughtShoppingItems == null)
            boughtShoppingItems = shoppingRepository.loadBoughtShoppingItem(getUserValue());

        // Initialize shopping item list LiveData if not already done
        if (shoppingItemListLiveData == null)
            shoppingItemListLiveData = shoppingRepository.loadAllShoppingItemForUser(getUserValue());

        // Initialize mediator if not already done
        if (!mediatorInitialized) {
            recipeWithShoppingItems.addSource(activeRecipeLiveData, recipe -> {
                currentRecipe = recipe;
                combine();
            });

            recipeWithShoppingItems.addSource(shoppingItemListLiveData, items -> {
                currentShoppingItems = items;
                combine();
            });
            mediatorInitialized = true;
        }
    }


    /**
     * Sets up an observer for the user recipe LiveData.
     *
     * @param owner          The LifecycleOwner that will observe the LiveData
     * @param recipeObserver The observer that will receive updates
     */
    public void setUserRecipeLiveDataObserver(LifecycleOwner owner, Observer<PagingData<Recipe>> recipeObserver) {
        userRecipeLiveData.observe(owner, recipeObserver);
    }

    /**
     * Sets up an observer for the recipes LiveData.
     *
     * @param owner           The LifecycleOwner that will observe the LiveData
     * @param recipesObserver The observer that will receive updates
     */
    public void setRecipesLiveDataObserver(LifecycleOwner owner, Observer<PagingData<Recipe>> recipesObserver) {
        recipesLiveData.observe(owner, recipesObserver);
    }

    /**
     * Sets up an observer for the active recipe LiveData.
     *
     * @param owner           The LifecycleOwner that will observe the LiveData
     * @param recipesObserver The observer that will receive updates
     */
    public void setActiveRecipeLiveDataObserver(LifecycleOwner owner, Observer<Recipe> recipesObserver) {
        activeRecipeLiveData.observe(owner, recipesObserver);
    }

    /**
     * Sets the active recipe in the MutableLiveData.
     *
     * @param recipe The recipe to set as active
     */
    public void setActiveRecipe(Recipe recipe) {
        activeRecipeLiveData.postValue(recipe);
    }

    /**
     * Reloads the active recipe by retrieving it from LiveData and setting it again.
     */
    public void reloadActiveRecipe() {
        Recipe recipe = getActiveRecipeLiveDataValue().orElse(new Recipe());
        setActiveRecipe(recipe);
    }

    /**
     * Creates LiveData for recipes using the provided RecipePagingSource.
     *
     * @param recipePagingSource The paging source for recipes
     * @return LiveData containing the paginated recipe data
     */
    private LiveData<PagingData<Recipe>> getRecipesLiveData(RecipePagingSource recipePagingSource) {
        Pager<Integer, Recipe> pager = new Pager<>(
                new PagingConfig(ShoppingServiceRepository.PAGE_SIZE),
                () -> recipePagingSource
        );
        return PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                getViewModelScope(this));
    }

    /**
     * Performs a search with the specified search mode and query.
     *
     * @param searchMode The mode to search by (NAME, INGREDIENTS, etc.)
     * @param query      The search query string
     */
    public void performSearch(SearchMode searchMode, String query) {
        recipesLiveData = getRecipesLiveData(new RecipePagingSource(shoppingServiceRepository, query, searchMode));
    }

    /**
     * Creates LiveData for user-specific recipes using the provided UserRecipePagingSource.
     *
     * @param recipePagingSource The paging source for user recipes
     * @return LiveData containing the paginated user recipe data
     */
    private LiveData<PagingData<Recipe>> getRecipesForUserLiveData(UserRecipePagingSource recipePagingSource) {
        Pager<Integer, Recipe> pager = new Pager<>(
                new PagingConfig(ShoppingServiceRepository.PAGE_SIZE),
                () -> recipePagingSource
        );
        return PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                getViewModelScope(this));
    }

    /**
     * Loads recipes for the current user.
     */
    public void loadRecipeUser() {
        userRecipeLiveData = getRecipesForUserLiveData(new UserRecipePagingSource(shoppingServiceRepository));
    }

    /**
     * Deletes a recipe from the server.
     *
     * @param recipe The recipe to delete
     * @param onFailureAction Action to perform on failure
     * @param onSuccessAction Action to perform on success
     */
    public void deleteRecipe(Recipe recipe, OnFailureAction onFailureAction, OnSuccessAction onSuccessAction) {
        shoppingServiceRepository.deleteRecipe(recipe.getRecipeId(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body()) {
                            recipesLiveData = getRecipesLiveData(new RecipePagingSource(shoppingServiceRepository));
                            userRecipeLiveData = getRecipesForUserLiveData(new UserRecipePagingSource(shoppingServiceRepository));
                            onSuccessAction.onSuccess();
                        }
                    }
                } else {
                    onFailureAction.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    /**
     * Combines current recipe and shopping items data into a Pair and updates the MediatorLiveData.
     */
    private void combine() {
        if (currentRecipe != null && currentShoppingItems != null) {
            recipeWithShoppingItems.setValue(
                    new Pair<>(currentRecipe, currentShoppingItems)
            );
        }
    }

    /**
     * Sets up an observer for the combined recipe and shopping items LiveData.
     *
     * @param owner  The LifecycleOwner that will observe the LiveData
     * @param observer The observer that will receive updates
     */
    public void setRecipeWithShoppingItemsObserver(LifecycleOwner owner, Observer<Pair<Recipe, List<ShoppingItem>>> observer) {
        recipeWithShoppingItems.observe(owner, observer);
    }

    /**
     * Sets the active ingredient in the MutableLiveData.
     *
     * @param ingredient The ingredient to set as active
     */
    public void setActiveIngredientValue(Ingredient ingredient) {
        activeIngredient.postValue(ingredient);
    }

    /**
     * Sets up an observer for the active ingredient LiveData.
     *
     * @param owner  The LifecycleOwner that will observe the LiveData
     * @param observer The observer that will receive updates
     */
    public void setUpActiveIngredientObserver(LifecycleOwner owner, Observer<Ingredient> observer) {
        activeIngredient.observe(owner, observer);
    }
}
