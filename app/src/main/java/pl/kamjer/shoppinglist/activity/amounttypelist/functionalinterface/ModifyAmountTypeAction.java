package pl.kamjer.shoppinglist.activity.amounttypelist.functionalinterface;

import pl.kamjer.shoppinglist.model.AmountType;

@FunctionalInterface
public interface ModifyAmountTypeAction {

    void action(AmountType amountType);
}
