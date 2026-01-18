package pl.kamjer.shoppinglist.util.validation;

import pl.kamjer.shoppinglist.model.user.User;

public class UserValidator {

    public static boolean isUserValid(User user) {
        return !user.getUserName().isEmpty();
    }
}
