package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

public class AmountTypeDeleteConflictDialogViewModel extends CustomViewModel{

    protected LiveData<List<AmountType>> amountTypesLiveData;

    public static final ViewModelInitializer<AmountTypeDeleteConflictDialogViewModel> initializer = new ViewModelInitializer<>(
            AmountTypeDeleteConflictDialogViewModel.class,
            creationExtras ->
                    new AmountTypeDeleteConflictDialogViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    public AmountTypeDeleteConflictDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public void initialize() {
        loadUser();
        loadAllAmountTypes();
    }

    public void loadAllAmountTypes() {
        amountTypesLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    public void setAmountTypesLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        amountTypesLiveData.observe(owner, observer);
    }

    public void deleteShoppingItemsForAmountType(AmountType amountType) {
        shoppingRepository.deleteShoppingItemsSoftDeleteAndDeleteAmountType(amountType, this::synchronizeData);
    }

    public void updateShoppingItemsAmountTypeAndDeleteAmountType(AmountType amountTypeToDelete, AmountType amountTypeToChange) {
        shoppingRepository.updateShoppingItemsAmountTypeAndDeleteAmountType(amountTypeToDelete, amountTypeToChange, this::synchronizeData);
    }
}
