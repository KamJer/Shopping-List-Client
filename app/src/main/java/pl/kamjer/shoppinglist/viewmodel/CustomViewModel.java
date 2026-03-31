package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.Category;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.model.shopping_list.ShoppingItem;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.exception.NoUserFoundException;
import retrofit2.Response;


/**
 * Abstract base ViewModel class that provides common functionality for all ViewModels.
 * Handles user loading, data synchronization, and DTO conversion.
 */
public abstract class CustomViewModel extends ViewModel {

    /**
     * Repository for shopping data operations.
     */
    protected final ShoppingRepository shoppingRepository;

    /**
     * Repository for shopping service operations (network calls).
     */
    protected final ShoppingServiceRepository shoppingServiceRepository;

    /**
     * Repository for shared data operations.
     */
    protected final SharedRepository sharedRepository;

    /**
     * LiveData containing the current user data.
     */
    protected MutableLiveData<User> userLiveData;

    /**
     * Constructor for CustomViewModel.
     * Initializes the repositories.
     *
     * @param shoppingRepository        Repository for shopping data operations
     * @param shoppingServiceRepository Repository for shopping service operations
     * @param sharedRepository          Repository for shared data operations
     */
    protected CustomViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        this.shoppingRepository = shoppingRepository;
        this.shoppingServiceRepository = shoppingServiceRepository;
        this.sharedRepository = sharedRepository;
    }

    /**
     * Loads the current user data from the repository.
     */
    public void loadUser() {
        userLiveData = shoppingRepository.loadUser(sharedRepository.loadUser());
    }

    /**
     * Gets the current user value from LiveData.
     * Throws NoUserFoundException if no user is found.
     *
     * @return The current user
     * @throws NoUserFoundException if no user is logged in
     */
    public User getUserValue() throws NoUserFoundException {
        return Optional.ofNullable(userLiveData.getValue()).orElseThrow(() -> new NoUserFoundException("No user is logged"));
    }

    /**
     * Initializes the ViewModel by loading the user data.
     */
    public void initialize() {
        loadUser();
    }

    /**
     * Synchronizes data for the currently logged user.
     * If no user is logged, will throw NoUserFoundException.
     * To avoid this, use synchronizeData(User user) method.
     */
    public void synchronizeData() {
        synchronizeData(getUserValue());
    }

    /**
     * Synchronizes data for the specified user.
     * This method collects all data (amount types, categories, shopping items) and sends it to the server.
     *
     * @param user The user for whom to synchronize data
     */
    public void synchronizeData(User user) {
        shoppingRepository.getAllDataAndAct(user, (amountTypes, categories, shoppingItems) -> {
            AllDto allDto = collectEntitiyToAllDto(user, amountTypes, categories, shoppingItems);
            shoppingServiceRepository.websocketSynchronize(allDto, user);
        });
    }

    /**
     * Converts entity objects to DTO objects for synchronization.
     * Determines the modify state (INSERT, UPDATE, DELETE, NONE) based on the entity's state.
     *
     * @param user         The user for whom to collect data
     * @param amountTypes  List of amount types to convert
     * @param categories   List of categories to convert
     * @param shoppingItems List of shopping items to convert
     * @return AllDto containing all converted DTO objects
     */
    protected AllDto collectEntitiyToAllDto(User user, List<AmountType> amountTypes, List<Category> categories, List<ShoppingItem> shoppingItems) {
        List<AmountTypeDto> amountTypesToSend = amountTypes
                .stream()
                .map(amountType -> {
                    if (amountType.isUpdated()) {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.UPDATE);
                    } else if (amountType.isDeleted()) {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.DELETE);
                    } else if (amountType.getAmountTypeId() == 0) {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.INSERT);
                    } else {
                        return ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.NONE);
                    }
                })
                .collect(Collectors.toList());

        List<CategoryDto> categoriesToSend = categories
                .stream()
                .map(category -> {
                    if (category.isUpdated()) {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.UPDATE);
                    } else if (category.isDeleted()) {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.DELETE);
                    } else if (category.getCategoryId() == 0) {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.INSERT);
                    } else {
                        return ServiceUtil.categoryToCategoryDto(category, ModifyState.NONE);
                    }
                })
                .collect(Collectors.toList());

        List<ShoppingItemDto> shoppingItemsToSend = shoppingItems
                .stream()
                .map(shoppingItem -> {
                    if (shoppingItem.isUpdated()) {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.UPDATE);
                    } else if (shoppingItem.isDeleted()) {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.DELETE);
                    } else if (shoppingItem.getShoppingItemId() == 0) {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.INSERT);
                    } else {
                        return ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem, ModifyState.NONE);
                    }
                })
                .collect(Collectors.toList());

        return AllDto.builder()
                .amountTypeDtoList(amountTypesToSend)
                .categoryDtoList(categoriesToSend)
                .shoppingItemDtoList(shoppingItemsToSend)
                .savedTime(user.getSavedTime())
                .build();
    }

    /**
     * Synchronizes data for the specified user with the provided response DTO.
     * This method processes the response DTO and updates the local database.
     *
     * @param user   The user for whom to synchronize data
     * @param responseAllDto The DTO containing the response data from the server
     */
    protected void synchronizeData(User user, AllDto responseAllDto) {
        shoppingRepository.synchronizeData(
                user,
                responseAllDto);
    }

    /**
     * Decodes error message from a Retrofit response.
     * Extracts the error body and returns it, or constructs a default error message if the body is empty.
     *
     * @param response The Retrofit response containing the error
     * @return The decoded error message
     */
    protected String decodeErrorMassage(Response<?> response) {
        return Optional.ofNullable(response.errorBody()).map(responseBody -> {
                    try {
                        return responseBody.string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(s -> !s.isEmpty())
                .orElse(ShoppingServiceRepository.CONNECTION_FAILED_MESSAGE + response.code());
    }
}
