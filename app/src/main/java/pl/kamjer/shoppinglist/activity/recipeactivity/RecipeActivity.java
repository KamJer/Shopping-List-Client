package pl.kamjer.shoppinglist.activity.recipeactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;
import pl.kamjer.shoppinglist.model.recipe.Recipe;
import pl.kamjer.shoppinglist.model.recipe.Step;

public class RecipeActivity extends GenericActivity {

    private TextView recipeTitle;
    private TextView recipeDesc;
    private RecyclerView ingredientRecyclerView;
    private RecyclerView stepRecyclerView;
    private RecyclerView tagRecyclerView;

    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate(R.layout.recipe_acticity_layout, R.id.recipe_activity_id);
        createMenuBar(true);

        findViews();
        loadRecipe();
        setViewsValue();
    }

    private void findViews() {
        this.recipeTitle = this.findViewById(R.id.tvRecipeTitle);
        this.recipeDesc = this.findViewById(R.id.tvRecipeDescription);
        this.ingredientRecyclerView = this.findViewById(R.id.rvIngredients);
        this.stepRecyclerView = this.findViewById(R.id.rvSteps);
        this.tagRecyclerView = this.findViewById(R.id.rvTags);
    }

    private void loadRecipe() {
        recipe = new Gson().fromJson(this.getIntent().getStringExtra("recipe"), Recipe.class);
    }

    private void setViewsValue() {
        this.recipeTitle.setText(recipe.getName());
        this.recipeDesc.setText(recipe.getDescription());

        IngredientAdapter ingredientAdapter = new IngredientAdapter(recipe.getIngredients());
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.ingredientRecyclerView.setAdapter(ingredientAdapter);

        StepAdapter stepAdapter = new StepAdapter(recipe.getSteps().stream().sorted(Comparator.comparing(Step::getStepNumber)).collect(Collectors.toList()));
        stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.stepRecyclerView.setAdapter(stepAdapter);

        TagAdapter tagAdapter = new TagAdapter(new ArrayList<>(recipe.getTags()));
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setAdapter(tagAdapter);

    }
}
