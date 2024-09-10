package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

public class NewShoppingItemDialogViewModel extends CustomViewModel{

    public LiveData<List<AmountType>> amountTypesListLiveData;
    public LiveData<List<Category>> categoryListLiveData;

    public NewShoppingItemDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<NewShoppingItemDialogViewModel> initializer = new ViewModelInitializer<>(NewShoppingItemDialogViewModel.class, creationExtras ->
            new NewShoppingItemDialogViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));


    public void loadAllAmountTypes() {
        amountTypesListLiveData = shoppingRepository.loadAllAmountType(getUserValue());
    }

    public void setAmountTypesListLiveDataObserver(LifecycleOwner owner, Observer<List<AmountType>> observer) {
        amountTypesListLiveData.observe(owner, observer);
    }

    public void loadAllCategory() {
        categoryListLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    public void setCategoryListLiveDataObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        categoryListLiveData.observe(owner, observer);
    }

    public void insertShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.insertShoppingItem(getUserValue(), shoppingItem, () ->
                synchronizeData(action));
    }

    public void updateShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, () ->
                synchronizeData(action));
    }
}
