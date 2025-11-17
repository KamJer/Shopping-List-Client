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
import pl.kamjer.shoppinglist.model.dto.AmountTypeDto;
import pl.kamjer.shoppinglist.model.dto.CategoryDto;
import pl.kamjer.shoppinglist.model.dto.ExceptionDto;
import pl.kamjer.shoppinglist.model.dto.ShoppingItemDto;
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
    private String shopping_List_port;
    private String user_port;

    private final static String BASE_URL = "https://";
    private final static String WEBSOCKET_BASE_URL = "wss://";

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
    private OnMessageAction<AmountTypeDto> onMessageActionAddAmountType;
    @Setter
    private OnMessageAction<AmountTypeDto> onMessageActionUpdateAmountType;
    @Setter
    private OnMessageAction<AmountTypeDto> onMessageActionDeleteAmountType;
    @Setter
    private OnMessageAction<CategoryDto> onMessageActionAddCategory;
    @Setter
    private OnMessageAction<CategoryDto> onMessageActionUpdateCategory;
    @Setter
    private OnMessageAction<CategoryDto> onMessageActionDeleteCategory;
    @Setter
    private OnMessageAction<ShoppingItemDto> onMessageActionAddShoppingItem;
    @Setter
    private OnMessageAction<ShoppingItemDto> onMessageActionUpdateShoppingItem;
    @Setter
    private OnMessageAction<ShoppingItemDto> onMessageActionDeleteShoppingItem;
    @Setter
    private OnMessageAction<String> onErrorAction;
    @Setter
    private OnFailureAction onFailureAction;

    private final List<OnConnectChangeAction> onConnectChangeAction = new ArrayList<>();

    @Getter
    @Setter
    private boolean initializedWithUser;

    private OkHttpClient okHttpClientShopping;
    private OkHttpClient okHttpClientUser;

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
        shopping_List_port = appContext.getResources().getString(R.string.shopping_port);
        user_port = appContext.getResources().getString(R.string.user_port);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        OkHttpClient okHttpClientUser = createClientWithOutUser();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + ip + ":" + user_port)
                .client(okHttpClientUser)
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
        okHttpClientShopping = createClient(user);
        okHttpClientUser = createClient(user);

        webSocket = new WebSocket(WEBSOCKET_BASE_URL + ip + "/ws")
                .basicWebsocketHeader()
                .onConnectAction((connected) -> onConnectChangeAction.forEach(onConnectChangeAction1 -> onConnectChangeAction1.action(connected)))
                .onFailure((webSocket1, t, response) -> onFailureAction.action(webSocket1, t, response))
                .onError((webSocket1, errorMessage) -> onErrorAction.action(webSocket1, errorMessage))
//                registering util endpoints
                .subscribe(gson, "/synchronizeData", AllDto.class, onMessageActionSynchronize)
                .subscribe(gson, "/{userName}/pip", String.class, onMessageActionPip, user.getUserName())
//                registering amount type endpoints
                .subscribe(gson, "/{userName}/putAmountType", AmountTypeDto.class, onMessageActionAddAmountType, user.getUserName())
                .subscribe(gson, "/{userName}/postAmountType", AmountTypeDto.class, onMessageActionUpdateAmountType, user.getUserName())
                .subscribe(gson, "/{userName}/deleteAmountType", AmountTypeDto.class, onMessageActionDeleteAmountType, user.getUserName())
//                registering category endpoints
                .subscribe(gson, "/{userName}/putCategory", CategoryDto.class, onMessageActionAddCategory, user.getUserName())
                .subscribe(gson, "/{userName}/postCategory", CategoryDto.class, onMessageActionUpdateCategory, user.getUserName())
                .subscribe(gson, "/{userName}/deleteCategory", CategoryDto.class, onMessageActionDeleteCategory, user.getUserName())
//                registering shopping item endpoints
                .subscribe(gson, "/{userName}/putShoppingItem", ShoppingItemDto.class, onMessageActionAddShoppingItem, user.getUserName())
                .subscribe(gson, "/{userName}/postShoppingItem", ShoppingItemDto.class, onMessageActionUpdateShoppingItem, user.getUserName())
                .subscribe(gson, "/{userName}/deleteShoppingItem", ShoppingItemDto.class, onMessageActionDeleteShoppingItem, user.getUserName())
                ;
        Retrofit retrofitUser = new Retrofit.Builder()
                .baseUrl(BASE_URL + ip + ":" + user_port)
                .client(okHttpClientUser)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Retrofit retrofitShoppingList = new Retrofit.Builder()
                .baseUrl(BASE_URL + ip + ":" + shopping_List_port)
                .client(okHttpClientShopping)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofitUser.create(UserService.class);
        utilService = retrofitShoppingList.create(UtilService.class);
        initializedWithUser = true;

        NetworkReceiver.register(appContext,
                network -> {
                    log.info("connected");
                    webSocket.connect(okHttpClientShopping);
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

    public void disconnect() {
        if (webSocket == null) {
            return;
        }
        webSocket.disconnect();
    }

    public void reconnectWebsocket() {
        webSocket.connect(okHttpClientShopping);
    }

    public void websocketSynchronize(AllDto allDto, User user) {
        webSocket.send(gson, "/synchronizeData", allDto, user.getUserName());
    }

    public void websocketPutAmountType(AmountTypeDto amountTypeDto, User user) {
        webSocket.send(gson, "/{userName}/putAmountType", amountTypeDto, user.getUserName());
    }

    public void websocketPostAmountType(AmountTypeDto amountTypeDto, User user) {
        webSocket.send(gson, "/{userName}/postAmountType", amountTypeDto, user.getUserName());
    }

    public void websocketDeleteAmountType(AmountTypeDto amountTypeDto, User user) {
        webSocket.send(gson, "/{userName}/deleteAmountType", amountTypeDto, user.getUserName());
    }

    public void websocketPutCategory(CategoryDto categoryDto, User user) {
        webSocket.send(gson, "/{userName}/putCategory", categoryDto, user.getUserName());
    }

    public void websocketPostCategory(CategoryDto categoryDto, User userValue) {
        webSocket.send(gson, "/{userName}/postCategory", categoryDto, userValue.getUserName());
    }

    public void websocketDeleteCategory(CategoryDto categoryDto, User userValue) {
        webSocket.send(gson, "/{userName}/deleteCategory", categoryDto, userValue.getUserName());
    }

    public void websocketPutShoppingItem(ShoppingItemDto shoppingItemDto, User user) {
        webSocket.send(gson, "/{userName}/putShoppingItem", shoppingItemDto, user.getUserName());
    }

    public void websocketPostShoppingItem(ShoppingItemDto shoppingItemDto, User userValue) {
        webSocket.send(gson, "/{userName}/postShoppingItem", shoppingItemDto, userValue.getUserName());
    }

    public void websocketDeleteShoppingItem(ShoppingItemDto shoppingItemDto, User userValue) {
        webSocket.send(gson, "/{userName}/deleteShoppingItem", shoppingItemDto, userValue.getUserName());
    }

    private OkHttpClient createClientWithOutUser() {
        OkHttpClient.Builder okHttpClientBuilder = SSLUtil.getSSLContext();
        return okHttpClientBuilder.build();

    }

    private OkHttpClient createClient(User user) {
        OkHttpClient.Builder okHttpClientBuilder = SSLUtil.getSSLContext();
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

    public void isUserCorrect(User user, Callback<Boolean> callback) {
        Call<Boolean> call = userService.logUser(ServiceUtil.userToUserDto(user));
        call.enqueue(callback);
    }


}


