package pl.kamjer.shoppinglist.util.validation;

import java.util.HashMap;

import pl.kamjer.shoppinglist.model.User;

public class UserValidator {

    public static boolean isUserValid(User user) {
        return !user.getUserName().isEmpty();
    }
}
