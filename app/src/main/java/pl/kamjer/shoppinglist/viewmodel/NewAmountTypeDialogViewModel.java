package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.viewmodel.ViewModelInitializer;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;
import pl.kamjer.shoppinglist.model.shopping_list.ModifyState;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.ServiceUtil;

public class NewAmountTypeDialogViewModel extends CustomViewModel{

    public NewAmountTypeDialogViewModel(ShoppingRepository shoppingRepository, ShoppingServiceRepository shoppingServiceRepository, SharedRepository sharedRepository) {
        super(shoppingRepository, shoppingServiceRepository, sharedRepository);
    }

    public static final ViewModelInitializer<NewAmountTypeDialogViewModel> initializer = new ViewModelInitializer<>(NewAmountTypeDialogViewModel.class, creationExtras ->
            new NewAmountTypeDialogViewModel(ShoppingRepository.getShoppingRepository(),
                    ShoppingServiceRepository.getShoppingServiceRepository(),
                    SharedRepository.getSharedRepository()));

    public void initialize() {
        loadUser();
    }

    public void insertAmountType(AmountType amountType) {
        shoppingRepository.insertAmountType(getUserValue(), amountType, () -> putAmountTypeServer(amountType));
    }

    public void updateAmountType(AmountType amountType) {
        shoppingRepository.updateAmountTypeSoft(amountType, () -> postAmountTypeServer(amountType));
    }

    private void putAmountTypeServer(AmountType amountType) {
        shoppingServiceRepository.websocketPutAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.INSERT), getUserValue());
    }

    private void postAmountTypeServer(AmountType amountType) {
        shoppingServiceRepository.websocketPostAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType, ModifyState.UPDATE), getUserValue());
    }
}
