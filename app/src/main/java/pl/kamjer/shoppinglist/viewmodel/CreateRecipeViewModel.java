package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
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

@Getter
    @Setter
    public class CreateRecipeViewModel extends CustomViewModel{

        private MutableLiveData<List<Ingredient>> ingredientsLiveData;
        private MutableLiveData<List<Step>> stepsLiveData;
        private MutableLiveData<Recipe> activeRecipeLiveData;

        public CreateRecipeViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
            super(shoppingRepository, shoppingServiceRepository, sharedRepository);
        }

        public static final ViewModelInitializer<pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel> initializer =
                new ViewModelInitializer<>(pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel.class,
                        creationExtras -> new pl.kamjer.shoppinglist.viewmodel.CreateRecipeViewModel(
                                ShoppingRepository.getShoppingRepository(),
                                ShoppingServiceRepository.getShoppingServiceRepository(),
                                SharedRepository.getSharedRepository()
                        ));

    @Override
    public void initialize() {
        super.initialize();
        if (ingredientsLiveData == null) ingredientsLiveData = new MutableLiveData<>(new ArrayList<>());
        if (stepsLiveData == null) stepsLiveData = new MutableLiveData<>(new ArrayList<>());
        if (activeRecipeLiveData == null) activeRecipeLiveData = new MutableLiveData<>();
    }

    public void setIngredientLiveDataObserver(LifecycleOwner owner, Observer<List<Ingredient>> ingredientObserver) {
        ingredientsLiveData.observe(owner, ingredientObserver);
    }

    public void setStepLiveDataObserver(LifecycleOwner owner, Observer<List<Step>> stepObserver) {
        stepsLiveData.observe(owner, stepObserver);
    }

    public void insertRecipe(RecipeDto map, PassActiveRecipe passActiveRecipe, OnFailureAction onFailureAction) {
        shoppingServiceRepository.insertRecipe(map, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RecipeDto> call, @NonNull Response<RecipeDto> response) {
                if (response.code() == 200) {
                    if (response.body() != null) passActiveRecipe.passActiveRecipe(Recipe.map(response.body()));
                } else {
                    onFailureAction.action(new NotOkHttpResponseException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeDto> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    public void updateRecipe(RecipeDto recipeDto, OnSuccessAction onSuccessAction, OnFailureAction onFailureAction) {
        shoppingServiceRepository.updateRecipe(recipeDto, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        onSuccessAction.onSuccess();
                    }
                } else {
                    onFailureAction.action(new NotOkHttpResponseException(String.valueOf(response.code())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                onFailureAction.action(t);
            }
        });
    }

    public void setIngredientLiveDataValue(List<Ingredient> ingredients) {
        ingredientsLiveData.postValue(ingredients);
    }

    public void setStepsLiveDataValue(List<Step> steps) {
        stepsLiveData.postValue(steps);
    }
}
