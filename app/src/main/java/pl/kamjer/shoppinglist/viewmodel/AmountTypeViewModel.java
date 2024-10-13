package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

public class AmountTypeViewModel extends CustomViewModel {

    private LiveData<List<AmountType>> allAmountTypeLiveData;
    private LiveData<List<ShoppingItem>> allShoppingItemsForAmountTypeLiveData;

    public AmountTypeViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<AmountTypeViewModel> initializer = new ViewModelInitializer<>(
            AmountTypeViewModel.class,
            creationExtras ->
                    new AmountTypeViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    public void loadAllAmountType() {
        allAmountTypeLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    public void setAllAmountTypeLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        allAmountTypeLiveData.observe(owner, observer);
    }

    public void deleteAmountType(AmountType amountType) {
        shoppingRepository.deleteAmountTypeSoft(amountType, () -> synchronizeData());
    }

    public void loadAllShoppingItemsForAmountType(AmountType amountType) {
        allShoppingItemsForAmountTypeLiveData = shoppingRepository.loadAllShoppingItemsForAmountType(getUserValue(), amountType);
    }

    public void setAllShoppingItemsForAmountTypeLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItem>> observer) {
        allShoppingItemsForAmountTypeLiveData.observe(owner, observer);
    }

    public void removeAllShoppingItemsForAmountTypeLiveDataObserver(Observer<List<ShoppingItem>> observer) {
        allShoppingItemsForAmountTypeLiveData.removeObserver(observer);
    }
}
