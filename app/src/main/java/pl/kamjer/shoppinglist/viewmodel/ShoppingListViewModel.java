package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

@Getter
@Log
public class ShoppingListViewModel extends CustomViewModel {

    private static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

    private LiveData<List<ShoppingItemWithAmountTypeAndCategory>> allShoppingItemWithAmountTypeAndCategoryLiveData;
    private LiveData<List<Category>> allCategoryLiveData;

    public ShoppingListViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<ShoppingListViewModel> initializer = new ViewModelInitializer<>(ShoppingListViewModel.class, creationExtras ->
            new ShoppingListViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    public void initialize() {
        loadUser();
        loadAllCategory();
        loadAllShoppingItemWithAmountTypeAndCategory();
    }

    public void loadAllShoppingItemWithAmountTypeAndCategory() {
        allShoppingItemWithAmountTypeAndCategoryLiveData = shoppingRepository
                .loadAllShoppingItemsWithAmountTypeAndCategory(getUserValue());
    }

    public void setShoppingItemWithAmountTypeAndCategoryLiveDataObserver(LifecycleOwner owner, Observer<List<ShoppingItemWithAmountTypeAndCategory>> observer) {
        allShoppingItemWithAmountTypeAndCategoryLiveData.observe(owner, observer);
    }

    public List<ShoppingItemWithAmountTypeAndCategory> getAllShoppingItemWithAmountTypeAndCategoryValue() {
        return Optional.ofNullable(allShoppingItemWithAmountTypeAndCategoryLiveData.getValue()).orElse(new ArrayList<>());
    }

    public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingRepository.updateShoppingItems(shoppingItems);
    }

    public void updateShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, () -> synchronizeData(action));
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem, OnFailureAction action) {
        shoppingRepository.deleteShoppingItemSoftDelete(shoppingItem, () -> synchronizeData(action));
    }

    public void loadAllCategory() {
        allCategoryLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    public void setAllCategoryObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }

    public void deleteCategory(Category category, OnFailureAction action) {
        shoppingRepository.deleteCategorySoft(category, () -> synchronizeData(action));
    }

    public void insertCategory(Category category, OnFailureAction action) {
        shoppingRepository.insertCategory(getUserValue(), category, () -> synchronizeData(action));
    }

    public void updateCategory(Category category, OnFailureAction action) {
        shoppingRepository.updateCategoryFlag(category, () -> synchronizeData(action));
    }

    public void testWebsocket() {
        shoppingServiceRepository.sendWensocketTest();
    }
}
