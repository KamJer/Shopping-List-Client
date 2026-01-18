package pl.kamjer.shoppinglist.repository;

import static androidx.core.content.ContextCompat.getString;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.user.User;

@RequiredArgsConstructor
public class SharedRepository {

    private static final String USER_NAME_FIELD_NAME = "userName";
    private static final String TUTORIAL_SEEN_FIELD_NAME = "tutorialSeen";

    private static SharedRepository sharedRepository;

    private SharedPreferences sharedPref;

    @Getter
    private String user;

    public static SharedRepository getSharedRepository() {
        SharedRepository result = sharedRepository;
        if (result != null) {
            return result;
        }
        synchronized (ShoppingRepository.class) {
            if (sharedRepository == null) {
                sharedRepository = new SharedRepository();
            }
            return sharedRepository;
        }
    }

    public void initialize(Context context) {
        sharedPref = context.getSharedPreferences(getString(context, R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public String loadUser() {
        return Optional.ofNullable(user).orElse(user = sharedPref.getString(USER_NAME_FIELD_NAME, ""));
    }

    /**
     * inserts user to the file, sets it as an active user
     * @param user - to save
     */
    public void insertUser(User user) {
        updateUser(user);
        this.user = user.getUserName();
    }

    public void updateUser(User user) {
        sharedPref.edit()
                .putString(USER_NAME_FIELD_NAME, user.getUserName())
                .apply();
    }

    public void deleteUser() {
        sharedPref.edit()
                .putString(USER_NAME_FIELD_NAME, "")
                .apply();
    }

    public void tutorialSeen(boolean seen) {
        sharedPref.edit()
                .putBoolean(TUTORIAL_SEEN_FIELD_NAME, seen)
                .apply();
    }

    public boolean isTutorialSeen() {
        return sharedPref.getBoolean(TUTORIAL_SEEN_FIELD_NAME, false);
    }
}
