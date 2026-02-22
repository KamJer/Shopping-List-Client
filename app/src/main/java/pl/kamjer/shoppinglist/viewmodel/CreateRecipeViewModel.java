package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import okhttp3.ResponseBody;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.recipe.Ingredient;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.model.recipe.Step;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnSuccessAction;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel class for managing recipe creation and editing functionality.
 * Handles data binding between the UI and repository layers for recipes, ingredients, and steps.
 */
@Getter
@Setter
public class CreateRecipeViewModel extends CustomViewModel {

    /**
     * MutableLiveData for holding the list of ingredients.
     * Observers can listen for changes to the ingredients list.
     */
    private MutableLiveData<List<Ingredient>> ingredientsLiveData;

    /**
     * MutableLiveData for holding the list of steps.
     * Observers can listen for changes to the steps list.
     */
    private MutableLiveData<List<Step>> stepsLiveData;

    /**
     * MutableLiveData for holding the active recipe.
     * Observers can listen for changes to the active recipe.
     */
    private MutableLiveData<Recipe> activeRecipeLiveData;

    /**
     * Constructor for CreateRecipeViewModel.
     * Initializes the ViewModel with required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared preferences operations
     */
    public CreateRecipeViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModelInitializer for CreateRecipeViewModel.
     * Provides initialization logic for creating instances of CreateRecipeViewModel.
     */
    public static final ViewModelInitializer<pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel> initializer =
            new ViewModelInitializer<>(pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel.class,
                    creationExtras -> new pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel(
                            ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository()
                    ));

    /**
     * Initializes the ViewModel.
     * Sets up the MutableLiveData objects if they haven't been initialized yet.
     */
    @Override
    public void initialize() {
        super.initialize();
        if (ingredientsLiveData == null) ingredientsLiveData = new MutableLiveData<>(new ArrayList<>());
        if (stepsLiveData == null) stepsLiveData = new MutableLiveData<>(new ArrayList<>());
        if (activeRecipeLiveData == null) activeRecipeLiveData = new MutableLiveData<>();
    }

    /**
     * Sets up an observer for the ingredients LiveData.
     *
     * @param owner           LifecycleOwner to observe the LiveData
     * @param ingredientObserver Observer to be notified of changes to ingredients
     */
    public void setIngredientLiveDataObserver(LifecycleOwner owner, Observer<List<Ingredient>> ingredientObserver) {
        ingredientsLiveData.observe(owner, ingredientObserver);
    }

    /**
     * Sets up an observer for the steps LiveData.
     *
     * @param owner        LifecycleOwner to observe the LiveData
     * @param stepObserver Observer to be notified of changes to steps
     */
    public void setStepLiveDataObserver(LifecycleOwner owner, Observer<List<Step>> stepObserver) {
        stepsLiveData.observe(owner, stepObserver);
    }

    /**
     * Inserts a new recipe using the shopping service repository.
     *
     * @param map               RecipeDto object containing recipe data to insert
     * @param passActiveRecipe  Callback to pass the active recipe after successful insertion
     * @param onFailureAction   Action to perform when the operation fails
     */
    public void insertRecipe(RecipeDto map, PassActiveRecipe passActiveRecipe, OnFailureAction onFailureAction) {
        shoppingServiceRepository.insertRecipe(map, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDto> call, @NonNull Response<RecipeDto> response) {
                if (response.code() == 200) {
                    if (response.body() != null) passActiveRecipe.passActiveRecipe(Recipe.map(response.body()));
                } else {
                    onFailureAction.action(new NotOkHttpResponseException(extractErrorMessage(response.errorBody())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDto> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    /**
     * Extracts error message from ResponseBody.
     *
     * @param responseBody ResponseBody containing error information
     * @return String containing the extracted error message
     */
    private String extractErrorMessage(ResponseBody responseBody) {
        String errorMessage = "";

        try (ResponseBody errorBody = responseBody){
            if (errorBody != null) {
                errorMessage = errorBody.string();
            }
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    /**
     * Updates an existing recipe using the shopping service repository.
     *
     * @param recipeDto       RecipeDto object containing updated recipe data
     * @param onSuccessAction Action to perform when the operation succeeds
     * @param onFailureAction Action to perform when the operation fails
     */
    public void updateRecipe(RecipeDto recipeDto, OnSuccessAction onSuccessAction, OnFailureAction onFailureAction) {
        shoppingServiceRepository.updateRecipe(recipeDto, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        onSuccessAction.onSuccess();
                    }
                } else {
                    onFailureAction.action(new NotOkHttpResponseException(extractErrorMessage(response.errorBody())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    /**
     * Sets the value of ingredients LiveData.
     *
     * @param ingredients List of ingredients to set
     */
    public void setIngredientLiveDataValue(List<Ingredient> ingredients) {
        ingredientsLiveData.postValue(ingredients);
    }

    /**
     * Sets the value of steps LiveData.
     *
     * @param steps List of steps to set
     */
    public void setStepsLiveDataValue(List<Step> steps) {
        stepsLiveData.postValue(steps);
    }
}
