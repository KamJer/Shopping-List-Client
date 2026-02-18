package pl.kamjer.shoppinglist.activity.recipeactivity;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;

import androidx.annotation.Nullable;

import lombok.val;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.GenericActivity;

public class RecipeActivity extends GenericActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate(R.layout.recipe_activity_layout, R.id.recipe_activity_id);

        createMenuBar(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        val navController = findNavController(this, R.id.recipe_search_fragment_id);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
