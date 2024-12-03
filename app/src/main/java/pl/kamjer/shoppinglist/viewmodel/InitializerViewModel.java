package pl.kamjer.shoppinglist.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.handler.ShoppingListExceptionHandler;

@Log
public class InitializerViewModel extends CustomViewModel {

    private final MutableLiveData<String> initializerLabelLiveData;

    public InitializerViewModel(ShoppingRepository shoppingRepository,
                                ShoppingServiceRepository shoppingServiceRepository,
                                SharedRepository sharedRepository,
                                MutableLiveData<String> initializerLabelLiveData) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
        this.initializerLabelLiveData = initializerLabelLiveData;
    }

    public static final ViewModelInitializer<InitializerViewModel> initializer =
            new ViewModelInitializer<>(InitializerViewModel.class,
                    creationExtras -> new InitializerViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository(),
                            new MutableLiveData<>()));

    public void initialize(Context appContext) {
        Thread.setDefaultUncaughtExceptionHandler(new ShoppingListExceptionHandler(
                appContext,
                ShoppingServiceRepository.getShoppingServiceRepository(),
                ShoppingRepository.getShoppingRepository().getExecutorService()));

        setInitializerLabelLiveDataValue(appContext.getString(R.string.initializing_connection_to_server_label));
        shoppingServiceRepository.initialize(appContext);
        setInitializerLabelLiveDataValue(appContext.getString(R.string.initializing_inner_files_label));
        sharedRepository.initialize(appContext);
        setInitializerLabelLiveDataValue(appContext.getString(R.string.initializing_database_label));
        shoppingRepository.initialize(
                appContext,
                ShoppingServiceRepository.getShoppingServiceRepository());
    }

    public void setUserLiveDataObserver(Observer<User> observer) {
        userLiveData.observeForever(observer);
    }

    public void setInitializerLabelLiveDataValue(String value) {
        initializerLabelLiveData.postValue(value);
    }

    public void setInitializerLabelLiveDataObserver(LifecycleOwner owner, Observer<String> observer) {
        initializerLabelLiveData.observe(owner, observer);
    }


}
