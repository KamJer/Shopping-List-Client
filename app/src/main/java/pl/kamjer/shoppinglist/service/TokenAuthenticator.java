package pl.kamjer.shoppinglist.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import pl.kamjer.shoppinglist.model.dto.TokenDto;
import pl.kamjer.shoppinglist.model.user.User;
import pl.kamjer.shoppinglist.service.service.UserService;

@NoArgsConstructor
@Setter
public class TokenAuthenticator implements Authenticator {

    private User user;
    private UserService authApi;
    private Runnable onTokenRefreshed;

    @Nullable
    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {

        if (user == null || authApi == null) {
            return null;
        }

        if (responseCount(response) >= 2) {
            return null;
        }

        synchronized (this) {
            if (responseCount(response) >= 2) {
                return null;
            }

            String currentRefreshToken = user.getPassword();

            retrofit2.Response<TokenDto> refreshResponse = authApi.refreshUser("Bearer " + currentRefreshToken).execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                String newAccessToken = refreshResponse.body().getAccessToken();
                String newRefreshToken = refreshResponse.body().getRefreshToken();

                user.setAccessToken(newAccessToken);
                if (newRefreshToken != null) {
                    user.setPassword(newRefreshToken);
                }

                if (onTokenRefreshed != null) {
                    onTokenRefreshed.run();
                }

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            }
        }

        return null;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}