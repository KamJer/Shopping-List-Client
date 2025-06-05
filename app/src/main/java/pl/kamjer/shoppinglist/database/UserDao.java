package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

import pl.kamjer.shoppinglist.model.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM USER WHERE user_name=:userName")
    LiveData<User> findUserByUserName(String userName);

    @Query("SELECT * FROM USER")
    LiveData<List<User>> findAllUsers();

    @Query("SELECT * FROM USER WHERE user_name=:userName")
    User findUserByUserNameBlock(String userName);

    @Delete
    void deleteUser(User user);

    @Query("Update USER SET saved_time = :savedTime WHERE user_name=:userName")
    void updateUsersSavedTime(LocalDateTime savedTime, String userName);
}
