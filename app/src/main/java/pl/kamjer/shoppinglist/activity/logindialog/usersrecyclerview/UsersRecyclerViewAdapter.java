package pl.kamjer.shoppinglist.activity.logindialog.usersrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteUserAction;

@AllArgsConstructor
@Setter
@Getter
public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UserViewHolder>{

    protected List<User> users;
    protected DeleteUserAction deleteUserAction;
    protected DeleteUserAction logUserAction;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_view_holder_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position), deleteUserAction, logUserAction);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
}
