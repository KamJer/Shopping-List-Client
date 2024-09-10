package pl.kamjer.shoppinglist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import pl.kamjer.shoppinglist.model.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM USER WHERE user_name=:userName")
    LiveData<User> findUserByUserName(String userName);

    @Query("SELECT * FROM USER")
    LiveData<List<User>> findAllUsers();

    @Query("SELECT * FROM USER WHERE user_name=:userName")
    User findUserByUserNameBlock(String userName);

    @Delete
    void deleteUser(User user);
}
