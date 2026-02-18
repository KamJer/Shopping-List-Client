package pl.kamjer.shoppinglist.activity.recipeactivity.recipe_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.recipe.Step;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

public class RecipeFragment extends Fragment {

    private RecipeViewModel recipeSearchViewModel;

    private TextView recipeTitle;
    private TextView recipeDesc;
    private TextView recipeSource;
    private RecyclerView ingredientRecyclerView;
    private RecyclerView stepRecyclerView;
    private RecyclerView tagRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment_layout, container, false);

        loadViewModel();

        findViews(view);
        loadRecipe();
        return view;
    }

    private void loadViewModel() {
        recipeSearchViewModel = new ViewModelProvider(
                requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
        recipeSearchViewModel.initialize();
    }

    private void findViews(View view) {
        this.recipeTitle = view.findViewById(R.id.tvRecipeTitle);
        this.recipeDesc = view.findViewById(R.id.tvRecipeDescription);
        this.ingredientRecyclerView = view.findViewById(R.id.rvIngredients);
        this.stepRecyclerView = view.findViewById(R.id.rvSteps);
        this.tagRecyclerView = view.findViewById(R.id.rvTags);
        this.recipeSource = view.findViewById(R.id.source_text_view);
    }

    private void loadRecipe() {
        recipeSearchViewModel.setActiveRecipeLiveDataObserver(this.getViewLifecycleOwner(), recipe -> {
            this.recipeTitle.setText(recipe.getName());
            this.recipeDesc.setText(recipe.getDescription());

            IngredientAdapter ingredientAdapter = new IngredientAdapter(recipe.getIngredients());
            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            this.ingredientRecyclerView.setAdapter(ingredientAdapter);

            StepAdapter stepAdapter = new StepAdapter(recipe.getSteps().stream().sorted(Comparator.comparing(Step::getStepNumber)).collect(Collectors.toList()));
            stepRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            this.stepRecyclerView.setAdapter(stepAdapter);

            TagAdapter tagAdapter = new TagAdapter(new ArrayList<>(recipe.getTags()));
            tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            tagRecyclerView.setAdapter(tagAdapter);

            this.recipeSource.setText(recipe.getSource());
        });
    }
}
