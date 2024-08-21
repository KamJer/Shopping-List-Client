package pl.kamjer.shoppinglist.service;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Base64;

import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pl.kamjer.shoppinglist.model.User;

@AllArgsConstructor
public class BasicAuthInterceptor implements Interceptor {

    private User user;

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        // Tworzenie nagłówka Authorization
        String credential = user.getUserName() + ":" + user.getPassword();
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credential.getBytes());

        Request request = chain.request().newBuilder()
                .addHeader("Authorization", basicAuth)
                .build();

        return chain.proceed(request);
    }
}
