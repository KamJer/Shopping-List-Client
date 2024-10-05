package pl.kamjer.shoppinglist.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import pl.kamjer.shoppinglist.gsonconverter.LocalDateTimeDeserializer;
import pl.kamjer.shoppinglist.gsonconverter.LocalDateTimeSerializer;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.ExceptionDto;
import pl.kamjer.shoppinglist.service.BasicAuthInterceptor;
import pl.kamjer.shoppinglist.service.SSLUtil;
import pl.kamjer.shoppinglist.service.service.UserService;
import pl.kamjer.shoppinglist.service.service.UtilService;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.funcinterface.OnConnectAction;
import pl.kamjer.shoppinglist.websocketconnect.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Log
public class ShoppingServiceRepository {

    public static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

//    private static final String BASE_URL = "https://35.212.210.42";
    private static final String IP = "192.168.0.13";
    private static final String BASE_URL = "https://" + IP;
    private static final String WEBSOCKET_BASE_URL = "wss://" + IP;

    private static ShoppingServiceRepository shoppingServiceRepository;

    private UserService userService;
    private UtilService utilService;

    private WebSocket webSocket;

    @Getter
    @Setter
    private boolean initializedWithUser;

    public static ShoppingServiceRepository getShoppingServiceRepository() {
        ShoppingServiceRepository result = shoppingServiceRepository;
        if (result != null) {
            return result;
        }
        synchronized (ShoppingRepository.class) {
            if (shoppingServiceRepository == null) {
                shoppingServiceRepository = new ShoppingServiceRepository();
            }
            return shoppingServiceRepository;
        }
    }

    public void initialize(Context context) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        OkHttpClient okHttpClient = createClientWithOutUser(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofit.create(UserService.class);
        initializedWithUser = false;
    }

    public void reInitializeWithUser(Context appContext, User user) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        OkHttpClient okHttpClient = createClient(appContext, user);

        webSocket = new WebSocket(WEBSOCKET_BASE_URL + "/ws")
                .basicWebsocketHeader()
                .onOpen((webSocket1, response) -> log.info("open"))
                .onClosed((webSocket1, code, reason) -> log.info("closed"))
                .onFailure((webSocket1, t, response) -> log.info(t.getMessage()))
                .subscribe("test1", "/topic/messages", (webSocket1, text) -> log.info(text))
                .connect(okHttpClient);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofit.create(UserService.class);
        utilService = retrofit.create(UtilService.class);
        initializedWithUser = true;
    }

    public void sendWensocketTest(){
        webSocket.send("/app/send", "text/plain", "test");
    }

    private OkHttpClient createClientWithOutUser(Context context) {
        OkHttpClient.Builder okHttpClientBuilder = SSLUtil.getSSLContext(context);
        return okHttpClientBuilder.build();

    }

    private OkHttpClient createClient(Context appContext, User user) {
        OkHttpClient.Builder okHttpClientBuilder = SSLUtil.getSSLContext(appContext);
        okHttpClientBuilder.addInterceptor(new BasicAuthInterceptor(user));
        return okHttpClientBuilder.build();
    }

    public void insertUser(User user, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = userService.postUser(ServiceUtil.userToUserDto(user));
        call.enqueue(callback);
    }

    public void synchronizeData(AllDto allDto, Callback<AllDto> callback) {
        Call<AllDto> call = utilService.synchronizeData(allDto);
//        call.enqueue(callback);
    }

    public void sendLog(ExceptionDto e, OnConnectAction action) {
        Call<Void> call = utilService.sendLog(e);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                action.action();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                action.action();
            }
        });
    }
}


