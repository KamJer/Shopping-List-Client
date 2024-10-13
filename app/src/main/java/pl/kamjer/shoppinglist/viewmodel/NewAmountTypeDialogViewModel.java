package pl.kamjer.shoppinglist.viewmodel;

import androidx.lifecycle.viewmodel.ViewModelInitializer;

import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.repository.SharedRepository;
import pl.kamjer.shoppinglist.repository.ShoppingRepository;
import pl.kamjer.shoppinglist.repository.ShoppingServiceRepository;
import pl.kamjer.shoppinglist.util.funcinterface.OnFailureAction;

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

    public void insertAmountType(AmountType amountType, OnFailureAction action) {
        shoppingRepository.insertAmountType(getUserValue(), amountType, this::synchronizeData);
    }

    public void updateAmountType(AmountType amountType, OnFailureAction action) {
        shoppingRepository.updateAmountType(amountType, this::synchronizeData);
    }
}
