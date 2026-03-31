package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.dto.TokenDto;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * ViewModel for managing the login dialog functionality.
 * Handles user authentication, user creation, and user management operations.
 */
public class LoginDialogViewModel extends CustomViewModel {

    /**
     * LiveData containing all users from the database.
     * Used to observe changes in the users data.
     */
    private LiveData<List<User>> usersLiveData;

    /**
     * Constructor for LoginDialogViewModel.
     * Initializes the ViewModel with the required repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    public LoginDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    /**
     * ViewModel initializer for creating instances of LoginDialogViewModel.
     * Provides the necessary repositories for dependency injection.
     */
    public static final ViewModelInitializer<LoginDialogViewModel> initializer = new ViewModelInitializer<>(LoginDialogViewModel.class, creationExtras ->
            new LoginDialogViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    /**
     * Inserts a new user into the system by making a network call to the server.
     * If successful, the user's saved time is set and the user is inserted locally.
     * If unsuccessful, the failure action is executed with an appropriate exception.
     *
     * @param user   The user to be inserted
     * @param action The action to be performed on failure
     */
    public void insertUser(User user, OnFailureAction action) {
        shoppingServiceRepository.insertUser(user, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LocalDateTime> call, @NonNull Response<LocalDateTime> response) {
                if (response.isSuccessful()) {
                    user.setSavedTime(response.body());
                    insertUser(user);
                } else {
                    action.action(new NotOkHttpResponseException(decodeErrorMassage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocalDateTime> call, @NonNull Throwable t) {
                action.action(t);
            }
        });
    }

    /**
     * Inserts a user into both shared preferences and the local database.
     * This method should be called after successfully creating a user on the server.
     *
     * @param user The user to be inserted locally
     */
    public void insertUser(User user) {
        sharedRepository.insertUser(user);
        shoppingRepository.insertUser(user);
    }

    /**
     * Loads all users from the database.
     * Sets up the usersLiveData with the loaded data.
     */
    public void loadAllUsers() {
        usersLiveData = shoppingRepository.loadAllUsers();
    }

    /**
     * Sets an observer for the users LiveData.
     * The observer will be notified when the users data changes.
     *
     * @param owner   The LifecycleOwner for the observer
     * @param observer The observer to be set
     */
    public void setOnUsersObserver(LifecycleOwner owner, Observer<List<User>> observer) {
        usersLiveData.observe(owner, observer);
    }

    /**
     * Deletes a user from both shared preferences and the local database.
     * This method should be called when a user wants to log out or delete their account.
     *
     * @param user The user to be deleted
     */
    public void deleteUser(User user) {
        sharedRepository.deleteUser();
        shoppingRepository.deleteUser(user);
    }

    /**
     * Initializes the shopping service repository with the application context.
     * This method should be called before making any network calls.
     *
     */
    public void initializeShoppingServiceRepository() {
        shoppingServiceRepository.initialize();
    }

    /**
     * Checks if a user's credentials are correct by making a network call to the server.
     * The result is returned through the callback.
     *
     * @param user     The user whose credentials need to be verified
     * @param callback The callback to receive the result
     */
    public void loginUser(User user, Callback<TokenDto> callback) {
        shoppingServiceRepository.loginUser(user, callback);
    }
}
