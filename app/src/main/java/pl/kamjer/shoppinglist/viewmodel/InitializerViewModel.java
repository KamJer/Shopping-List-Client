package pl.kamjer.shoppinglist.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;
import pl.kamjer.shoppinglist.util.loadManager.ServerMessageCoordinator;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;

@Log
public class InitializerViewModel extends CustomViewModel {

    /**
     * MutableLiveData that holds the initialization progress label text
     * Used to communicate the current initialization step to the UI
     */
    private final MutableLiveData<String> initializerLabelLiveData;

    /**
     * Constructor for InitializerViewModel
     * @param shoppingRepository Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository Repository for shared preferences and application data
     * @param initializerLabelLiveData MutableLiveData to track initialization progress
     */
    public InitializerViewModel(ShoppingRepository shoppingRepository,
                                ShoppingServiceRepository shoppingServiceRepository,
                                SharedRepository sharedRepository,
                                MutableLiveData<String> initializerLabelLiveData) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
        this.initializerLabelLiveData = initializerLabelLiveData;
    }

    /**
     * ViewModel initializer that creates a new instance of InitializerViewModel
     * with proper dependencies using singleton repositories
     */
    public static final ViewModelInitializer<InitializerViewModel> initializer =
            new ViewModelInitializer<>(InitializerViewModel.class,
                    creationExtras -> new InitializerViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository(),
                            new MutableLiveData<>()));

    /**
     * Initializes the application components in the correct order
     * @param appContext Application context for initialization
     * @throws InvalidAlgorithmParameterException if cryptographic algorithm parameter is invalid
     * @throws NoSuchAlgorithmException if cryptographic algorithm is not available
     * @throws NoSuchProviderException if cryptographic provider is not available
     */
    public void initialize(Context appContext) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        // Set up global exception handler for uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(
                appContext,
                ShoppingServiceRepository.getShoppingServiceRepository(),
                ShoppingRepository.getShoppingRepository().getExecutorService()));

        // Set initialization progress label to connection initialization
        setInitializerLabelLiveDataValue(appContext.getString(R.string.initializing_connection_to_server_label));
        shoppingServiceRepository.initialize(appContext);

        // Set initialization progress label to inner files initialization
        setInitializerLabelLiveDataValue(appContext.getString(R.string.initializing_inner_files_label));
        sharedRepository.initialize(appContext);

        // Set initialization progress label to database initialization
        setInitializerLabelLiveDataValue(appContext.getString(R.string.initializing_database_label));
        shoppingRepository.initialize(
                appContext,
                ShoppingServiceRepository.getShoppingServiceRepository());
    }

    /**
     * Adds an observer to the user live data to monitor user changes
     * @param observer Observer that will be notified when user data changes
     */
    public void setUserLiveDataObserver(Observer<User> observer) {
        userLiveData.observeForever(observer);
    }

    /**
     * Updates the initialization progress label value
     * @param value The new progress label text to display
     */
    public void setInitializerLabelLiveDataValue(String value) {
        initializerLabelLiveData.postValue(value);
    }

    /**
     * Adds an observer to the initialization progress label live data
     * @param owner Lifecycle owner that will observe the progress updates
     * @param observer Observer that will be notified when progress changes
     */
    public void setInitializerLabelLiveDataObserver(LifecycleOwner owner, Observer<String> observer) {
        initializerLabelLiveData.observe(owner, observer);
    }

    /**
     * Logs the user off by clearing the logged user and deleting the user data
     * @param user The user to log off
     */
    public void logUserOff(User user) {
        shoppingRepository.setLoggedUser(null);
        shoppingRepository.deleteUser(user);
    }

    /**
     * Initializes WebSocket message handling for the specified user
     * @param user The user for whom to register WebSocket message handling
     * @param onErrorAction Action to perform when an error occurs during message handling
     * @param failure Action to perform when a failure occurs during connection
     */
    public void initializeOnMessageAction(User user,
                                          OnMessageAction<String> onErrorAction,
                                          OnFailureAction failure) {
        ServerMessageCoordinator serverMessageCoordinator = new ServerMessageCoordinator(shoppingRepository, shoppingServiceRepository);
        serverMessageCoordinator.register(user, onErrorAction, failure);
    }

    /**
     * Disconnects the WebSocket connection
     */
    public void websocketDisconnect() {
        shoppingServiceRepository.disconnect();
    }
}
