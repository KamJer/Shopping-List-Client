package pl.kamjer.shoppinglist.service;

import androidx.annotation.NonNull;

import java.io.IOException;

import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pl.kamjer.shoppinglist.model.user.User;

@AllArgsConstructor
public class JwtTokenInterceptor implements Interceptor {

    private final User user;

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        if (chain.request().url().toString().contains("/log")) {
            return chain.proceed(request.build());
        }
//        Validating request
        if (!chain.request().url().toString().contains("/refresh")
                && (user.getAccessToken() == null
                || user.getAccessToken().isEmpty())) {
            throw new IOException("JWT token is missing");
        }
        String token = "";
        if (chain.request().url().toString().contains("/refresh")) token = user.getPassword();
        else token = user.getAccessToken();

        String bearerAuth = "Bearer " + token;

        request.addHeader("Authorization", bearerAuth);

        return chain.proceed(request.build());
    }
}
