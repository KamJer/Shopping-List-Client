package pl.kamjer.shoppinglist.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import okhttp3.OkHttpClient;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.gsonconverter.LocalDateTimeDeserializer;
import pl.kamjer.shoppinglist.gsonconverter.LocalDateTimeSerializer;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.ExceptionDto;
import pl.kamjer.shoppinglist.service.BasicAuthInterceptor;
import pl.kamjer.shoppinglist.service.SSLUtil;
import pl.kamjer.shoppinglist.service.service.UserService;
import pl.kamjer.shoppinglist.service.service.UtilService;
import pl.kamjer.shoppinglist.util.NetworkReceiver;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import pl.kamjer.shoppinglist.util.funcinterface.OnConnectAction;
import pl.kamjer.shoppinglist.websocketconnect.WebSocket;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnConnectChangeAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnFailureAction;
import pl.kamjer.shoppinglist.websocketconnect.funcIntarface.OnMessageAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Log
public class ShoppingServiceRepository {

    public static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

    private String ip;
    private String baseUrl = "https://";
    private String websocketBaseUrl = "wss://";

    private static ShoppingServiceRepository shoppingServiceRepository;

    private UserService userService;
    private UtilService utilService;

    @Getter
    private WebSocket webSocket;

    @Setter
    private OnMessageAction<AllDto> onMessageActionSynchronize;
    @Setter
    private OnMessageAction<String> onMessageActionPip;
    @Setter
    private OnMessageAction<String> onErrorAction;
    @Setter
    private OnFailureAction onFailureAction;

    private final List<OnConnectChangeAction> onConnectChangeAction = new ArrayList<>();

    @Getter
    @Setter
    private boolean initializedWithUser;

    private OkHttpClient okHttpClient;

    private Gson gson;

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

    public void initialize(Context appContext) {
        ip = appContext.getResources().getString(R.string.ip);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        OkHttpClient okHttpClient = createClientWithOutUser(appContext);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + ip)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofit.create(UserService.class);
        initializedWithUser = false;
    }

    public void reInitializeWithUser(Context appContext, User user) {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        okHttpClient = createClient(appContext, user);

        webSocket = new WebSocket(websocketBaseUrl + ip + "/ws")
                .basicWebsocketHeader()
                .onConnectAction((connected) -> onConnectChangeAction.forEach(onConnectChangeAction1 -> onConnectChangeAction1.action(connected)))
                .onFailure((webSocket1, t, response) -> onFailureAction.action(webSocket1, t, response))
                .onError((webSocket1, errorMessage) -> onErrorAction.action(webSocket1, errorMessage))
                .subscribe(gson, "/synchronizeData", AllDto.class, onMessageActionSynchronize)
                .subscribe(gson, "/{username}/pip", String.class, onMessageActionPip, user.getUserName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + ip)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofit.create(UserService.class);
        utilService = retrofit.create(UtilService.class);
        initializedWithUser = true;

        NetworkReceiver.register(appContext,
                network -> {
                    log.info("connected");
                    webSocket.connect(okHttpClient);
                },
                network -> {
                    log.info("lost");
                    webSocket.disconnect();
                });
    }

    public boolean isConnected() {
//       TODO: fast and dirty fix think about it later
        if (webSocket == null) {
            return false;
        }
        return webSocket.isConnected();
    }

    public void reconnectWebsocket() {
        webSocket.connect(okHttpClient);
    }

    public void websocketSynchronize(AllDto allDto, User user) {
        webSocket.send(gson, "/synchronizeData", allDto, user.getUserName());
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
        call.enqueue(callback);
    }

    public void sendLog(ExceptionDto e, OnConnectAction action) {
        Call<Void> call = utilService.sendLog(e);
        call.enqueue(new Callback<>() {
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

    public void addOnConnectionChangedAction(OnConnectChangeAction action) {
        onConnectChangeAction.add(action);
    }

    public void removeOnConnectionChangedAction(OnConnectChangeAction action) {
        onConnectChangeAction.remove(action);
    }

    public void isUserCorrect(User user, Callback<Boolean> callback) {
        Call<Boolean> call = userService.logUser(ServiceUtil.userToUserDto(user));
        call.enqueue(callback);
    }
}


