package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.model.dto.RecipeDto;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.model.recipe.Tag;
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

    private MutableLiveData<Set<Tag>> tagsLiveData;

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
     */
    @Override
    public void initialize() {
        super.initialize();
        if (tagsLiveData == null) tagsLiveData = new MutableLiveData<>(new HashSet<>());
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
                    onFailureAction.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDto> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
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
                    onFailureAction.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    public void loadAllTags(OnFailureAction onFailureAction) {
        shoppingServiceRepository.getAllTags(new Callback<>() {

            @Override
            public void onResponse(Call<Set<String>> call, Response<Set<String>> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        tagsLiveData.setValue(response.body().stream()
                                .map(t -> Tag.builder().tag(t).build())
                                .collect(Collectors.toSet()));
                    }
                } else {
                    onFailureAction.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(Call<Set<String>> call, Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    public void setTagsLiveDataObserver(LifecycleOwner owner, Observer<Set<Tag>> observer) {
        tagsLiveData.observe(owner, observer);
    }
}
