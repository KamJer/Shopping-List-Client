package pl.kamjer.shoppinglist.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.Authenticator;
import okhttp3.HttpUrl;
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
    private Runnable onRefreshTokenPersisted;
    private Runnable onRefreshFailed;

    @Nullable
    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {

        if (user == null || authApi == null) {
            return null;
        }

        if (responseCount(response) >= 2) {
            return null;
        }

//        if the refresh endpoint itself returns 401 it means the refresh token is no longer valid,
//        trying to refresh again would only cause unbounded recursion
        if (response.request().url().toString().contains("/refresh")) {
            if (onRefreshFailed != null) {
                onRefreshFailed.run();
            }
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

                if (onRefreshTokenPersisted != null) {
                    onRefreshTokenPersisted.run();
                }

                if (onTokenRefreshed != null) {
                    onTokenRefreshed.run();
                }

                HttpUrl url = response.request().url();
                Request.Builder requestBuilder = response.request().newBuilder();

                if (url.queryParameter("token") != null) {
//                    WebSocket handshake to the shopping server: the token is passed only via the "token" query
//                    parameter, the server ignores the Authorization header, so it is removed from the request
                    requestBuilder
                            .removeHeader("Authorization")
                            .url(url.newBuilder()
                                    .setQueryParameter("token", newAccessToken)
                                    .build());
                } else {
//                    REST calls to the sec/recipe servers read the token from the Authorization header
                    requestBuilder.header("Authorization", "Bearer " + newAccessToken);
                }

                return requestBuilder.build();
            }
//            the refresh endpoint itself responded with 401/403, which means the refresh token is no longer
//            valid (expired or revoked). There is nothing to retry with, so the user has to log in again.
            else if (refreshResponse.code() == 401 || refreshResponse.code() == 403) {
                if (onRefreshFailed != null) {
                    onRefreshFailed.run();
                }
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
