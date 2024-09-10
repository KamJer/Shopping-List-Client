package pl.kamjer.shoppinglist.util.funcinterface;

import pl.kamjer.shoppinglist.model.User;

@FunctionalInterface
public interface DeleteUserAction {

    void action(User user);
}
