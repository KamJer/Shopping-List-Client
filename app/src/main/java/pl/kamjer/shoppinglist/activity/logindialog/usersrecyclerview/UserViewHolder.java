package pl.kamjer.shoppinglist.activity.logindialog.usersrecyclerview;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.util.funcinterface.DeleteUserAction;

public class UserViewHolder extends RecyclerView.ViewHolder {

    protected ImageButton deleteUserButton;
    protected ImageButton logUserInButton;
    protected TextView userNameTextView;

    protected User user;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.userNameTextView);
        deleteUserButton = itemView.findViewById(R.id.deleteUserButton);
        logUserInButton = itemView.findViewById(R.id.logUserInButton);
    }

    public void bind(User user, DeleteUserAction deleteUserAction, DeleteUserAction logUserAction) {
        this.user = user;
        userNameTextView.setText(user.getUserName());
        deleteUserButton.setOnClickListener(v -> deleteUserAction.action(user));
        logUserInButton.setOnClickListener(v -> logUserAction.action(user));
    }
}
