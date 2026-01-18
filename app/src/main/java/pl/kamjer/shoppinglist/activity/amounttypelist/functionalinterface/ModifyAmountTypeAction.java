package pl.kamjer.shoppinglist.activity.amounttypelist.functionalinterface;

import pl.kamjer.shoppinglist.model.shopping_list.AmountType;

@FunctionalInterface
public interface ModifyAmountTypeAction {

    void action(AmountType amountType);
}
