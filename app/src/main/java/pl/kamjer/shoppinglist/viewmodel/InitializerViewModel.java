package pl.kamjer.shoppinglist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.exception.NotOkHttpResponseException;
import pl.kamjer.shoppinglist.util.funcinterface.OnConnectAction;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
