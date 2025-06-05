package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;

public class BoughtShoppingItemsListViewModel extends CustomViewModel {

    private LiveData<List<ShoppingItemWithAmountTypeAndCategory>> allShoppingItemWithAmountTypeAndCategoryLiveData;
    private LiveData<List<Category>> allCategoryLiveData;

    private LiveData<User> userLiveData;

    public BoughtShoppingItemsListViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<BoughtShoppingItemsListViewModel> initializer = new ViewModelInitializer<>(
            BoughtShoppingItemsListViewModel.class,
            creationExtras ->
                    new BoughtShoppingItemsListViewModel(ShoppingRepository.getShoppingRepository(),
                            ShoppingServiceRepository.getShoppingServiceRepository(),
                            SharedRepository.getSharedRepository())
    );

    public void loadAllShoppingItemWithAmountTypeAndCategoryLiveData() {
        allShoppingItemWithAmountTypeAndCategoryLiveData = shoppingRepository.loadAllShoppingItemsWithAmountTypeAndCategory(getUserValue());
    }

    public void setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItemWithAmountTypeAndCategory>> observer) {
        this.allShoppingItemWithAmountTypeAndCategoryLiveData.observe(owner, observer);
    }

    public void updateShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, this::synchronizeData);
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.deleteShoppingItemSoft(shoppingItem, this::synchronizeData);
    }

    public void loadAllCategory() {
        allCategoryLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    public void setAllCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }
}
