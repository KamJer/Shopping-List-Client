package pl.kamjer.shoppinglist.activity.recipe_activity.user_recipe;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.activity.recipe_activity.recipe_recycler_view.RecipeComparator;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.EditRecipeAction;
import pl.kamjer.shoppinglist.util.funcinterface.PassActiveRecipe;
import pl.kamjer.shoppinglist.viewmodel.RecipeViewModel;

public class UserRecipeFragment extends Fragment {

    private RecyclerView userRecipeRecyclerView;
    private RecipeViewModel recipeViewModel;
    private UserRecipeAdapter userRecipeAdapter;
    private ImageButton addRecipeBtn;
    private TextView emptyView;
    private ProgressBar loadingDataProgressData;



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
        emptyView = view.findViewById(R.id.emptyView);
        loadingDataProgressData = view.findViewById(R.id.loadingDataProgressBar);
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

        userRecipeAdapter.addLoadStateListener(combinedLoadStates -> {
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading && userRecipeAdapter.getItemCount() == 0) {
                loadingDataProgressData.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading) {
                loadingDataProgressData.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else if (combinedLoadStates.getRefresh() instanceof LoadState.Loading) {
                loadingDataProgressData.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
            return null;
        });

        setupObservers();
    }

    private void setupObservers() {
        recipeViewModel.setUserRecipeLiveDataObserver(this.getViewLifecycleOwner(), recipes -> {
            userRecipeAdapter.submitData(getLifecycle(), recipes);
        });
    }

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
        return recipe -> recipeViewModel.deleteRecipe(recipe,
                t -> Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show(),
                this::setupObservers);
    }
}
