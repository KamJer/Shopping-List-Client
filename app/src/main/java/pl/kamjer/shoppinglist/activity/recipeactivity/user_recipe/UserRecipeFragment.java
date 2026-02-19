package pl.kamjer.shoppinglist.activity.recipeactivity.user_recipe;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipeactivity.recyclerview.RecipeComparator;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.EditRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

public class UserRecipeFragment extends Fragment {

    private RecyclerView userRecipeRecyclerView;
    private RecipeViewModel recipeViewModel;
    private UserRecipeAdapter userRecipeAdapter;
    private ImageButton addRecipeBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_recipes_list_fragment_layout, container, false);

        loadViewModel();
        findViews(view);
        setupRecyclerView();
        setBtnAction();

        recipeViewModel.loadRecipeUser();

        return view;
    }

    private void loadViewModel() {
        recipeViewModel = new ViewModelProvider(
                requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)
        ).get(RecipeViewModel.class);
    }

    private void findViews(View view) {
        userRecipeRecyclerView = view.findViewById(R.id.recycler_view_user_recipes);
        addRecipeBtn = view.findViewById(R.id.add_recipe_button);
    }

    private void setBtnAction() {
        addRecipeBtn.setOnClickListener(v -> {
            recipeViewModel.setActiveRecipe(null);
            findNavController(UserRecipeFragment.this).navigate(R.id.action_user_recipe_to_create_user_recipe);
        });
    }

    private void setupRecyclerView() {
        userRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecipeAdapter = new UserRecipeAdapter(new RecipeComparator(),
                getPassActiveRecipe(),
                getEditRecipeAction(),
                getDeleteRecipeAction());
        userRecipeRecyclerView.setAdapter(userRecipeAdapter);
        recipeViewModel.setUserRecipeLiveDataObserver(this.getViewLifecycleOwner(), recipes -> {
            userRecipeAdapter.submitData(getLifecycle(), recipes);
        });
//                recipes -> userRecipeRecyclerView.setAdapter(new UserRecipeAdapter(recipes.getContent(),
//                        recipe -> {
//                            recipeViewModel.setActiveRecipe(recipe);
//                            findNavController(UserRecipeFragment.this).navigate(R.id.action_user_recipes_to_recipe);
//                        },
//                        recipe -> {
//                            recipeViewModel.setActiveRecipe(recipe);
//                            findNavController(UserRecipeFragment.this).navigate(R.id.action_user_recipe_to_create_user_recipe);
//                        },
//                        recipe -> recipeViewModel.deleteRecipe(recipe))));
    }

//    private void loadUserRecipe() {
//        recipeViewModel.getRecipesForUser(0);
//    }

    private PassActiveRecipe getPassActiveRecipe() {
        return recipe -> {
            recipeViewModel.setActiveRecipe(recipe);
            findNavController(UserRecipeFragment.this).navigate(R.id.action_user_recipes_to_recipe);
        };
    }

    private EditRecipeAction getEditRecipeAction() {
        return recipe -> {
            recipeViewModel.setActiveRecipe(recipe);
            findNavController(UserRecipeFragment.this).navigate(R.id.action_user_recipe_to_create_user_recipe);
        };
    }

    private DeleteRecipeAction getDeleteRecipeAction() {
        return recipe -> recipeViewModel.deleteRecipe(recipe);
    }
}
