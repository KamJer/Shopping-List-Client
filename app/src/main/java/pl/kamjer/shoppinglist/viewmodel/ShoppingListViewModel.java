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
import pl.kamjer.shoppinglist.model.ModifyState;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.ShoppingItemWithAmountTypeAndCategory;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

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

    public void updateShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.updateShoppingItemFlag(shoppingItem, () -> updateShoppingItemServer(shoppingItem));
    }

    public void updateShoppingItemServer(ShoppingItem shoppingItem) {
        shoppingServiceRepository.websocketPostShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.UPDATE), getUserValue());
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        shoppingRepository.deleteShoppingItemSoft(shoppingItem, () -> deleteShoppingItemServer(shoppingItem));
    }

    public void deleteShoppingItemServer(ShoppingItem shoppingItem) {
        shoppingServiceRepository.websocketDeleteShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.DELETE), getUserValue());
    }

    public void loadAllCategory() {
        allCategoryLiveData = shoppingRepository.loadAllCategory(getUserValue());
    }

    public void setAllCategoryObserver(LifecycleOwner owner, Observer<List<Category>> observer) {
        allCategoryLiveData.observe(owner, observer);
    }

    public void deleteCategory(Category category) {
        shoppingRepository.deleteCategorySoft(category, () -> deleteCategoryServer(category));
    }

    public void insertCategory(Category category) {
        shoppingRepository.insertCategory(getUserValue(), category, () -> insertCategoryServer(category));
    }

    public void updateCategory(Category category) {
        shoppingRepository.updateCategoryFlag(category, () -> updateCategoryServer(category));
    }

    /**
     * Updates category in database with out setting up a flag for it to be updated on a server
     * @param category - to update
     */
    public void updateLocalCategory(Category category) {
        shoppingRepository.updateCategoryLocal(category);
    }

    private void insertCategoryServer(Category category) {
        shoppingServiceRepository.websocketPutCategory(ServiceUtil.categoryToCategoryDto(category, ModifyState.INSERT), getUserValue());
    }

    private void updateCategoryServer(Category category) {
        shoppingServiceRepository.websocketPostCategory(ServiceUtil.categoryToCategoryDto(category, ModifyState.UPDATE), getUserValue());
    }

    private void deleteCategoryServer(Category category) {
        shoppingServiceRepository.websocketDeleteCategory(ServiceUtil.categoryToCategoryDto(category, ModifyState.DELETE), getUserValue());
    }

    /**
     * @return size of category list
     */
    public int getSizeCategory() {
        return Optional.ofNullable(getAllCategoryLiveData().getValue()).orElse(new ArrayList<>()).size();
    }

    public boolean isTutorialSeen() {
        return sharedRepository.isTutorialSeen();
    }

    public void tutorialSeen(boolean seen) {
        sharedRepository.tutorialSeen(seen);
    }
}
