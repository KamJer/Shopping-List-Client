package pl.kamjer.shoppinglist.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import pl.kamjer.shoppinglist.gsonconverter.LocalDateTimeDeserializer;
import pl.kamjer.shoppinglist.gsonconverter.LocalDateTimeSerializer;
import pl.kamjer.shoppinglist.model.AmountType;
import pl.kamjer.shoppinglist.model.Category;
import pl.kamjer.shoppinglist.model.ShoppingItem;
import pl.kamjer.shoppinglist.model.User;
import pl.kamjer.shoppinglist.model.dto.AddDto;
import pl.kamjer.shoppinglist.model.dto.AllDto;
import pl.kamjer.shoppinglist.model.dto.AllIdDto;
import pl.kamjer.shoppinglist.service.AmountTypeService;
import pl.kamjer.shoppinglist.service.BasicAuthInterceptor;
import pl.kamjer.shoppinglist.service.CategoryService;
import pl.kamjer.shoppinglist.service.SSLUtil;
import pl.kamjer.shoppinglist.service.ShoppingItemService;
import pl.kamjer.shoppinglist.service.UserService;
import pl.kamjer.shoppinglist.service.UtilService;
import pl.kamjer.shoppinglist.util.ServiceUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShoppingServiceRepository {

    public static final String CONNECTION_FAILED_MESSAGE = "Connection failed: Http code:";

//    private static final String BASE_URL = "https://35.212.210.42";
    private static final String BASE_URL = "https://10.0.2.2:8443";
    private static ShoppingServiceRepository shoppingServiceRepository;

    private UserService userService;
    private AmountTypeService amountTypeService;
    private CategoryService categoryService;
    private ShoppingItemService shoppingItemService;
    private UtilService utilService;

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
    }

    public void reInitializeWithUser(Context context, User user) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
        OkHttpClient okHttpClient = createClient(context, user);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofit.create(UserService.class);
        amountTypeService = retrofit.create(AmountTypeService.class);
        categoryService = retrofit.create(CategoryService.class);
        shoppingItemService = retrofit.create(ShoppingItemService.class);
        utilService = retrofit.create(UtilService.class);
    }

    private OkHttpClient createClientWithOutUser(Context context) {
        OkHttpClient.Builder okHttpClientBuilder = SSLUtil.getSSLContext(context);
        return okHttpClientBuilder.build();

    }

    private OkHttpClient createClient(Context context, User user) {
        OkHttpClient.Builder okHttpClientBuilder = SSLUtil.getSSLContext(context);
        okHttpClientBuilder.addInterceptor(new BasicAuthInterceptor(user));
        return okHttpClientBuilder.build();

    }

    public void insertUser(User user, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = userService.postUser(ServiceUtil.userToUserDto(user));
        call.enqueue(callback);
    }

    public void logUser(User user, Callback<Boolean> callback) {
        Call<Boolean> call = userService.logUser(user.getUserName());
        call.enqueue(callback);
    }

    public void insertAmountType(AmountType amountType, Callback<AddDto> callback) {
        Call<AddDto> call = amountTypeService.postAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType));
        call.enqueue(callback);
    }

    public void updateAmountType(AmountType amountType, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = amountTypeService.putAmountType(ServiceUtil.amountTypeToAmountTypeDto(amountType));
        call.enqueue(callback);
    }

    public void deleteAmountType(User user, AmountType amountType, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = amountTypeService.deleteAmountType(amountType.getAmountTypeId(), user.getUserName());
        call.enqueue(callback);
    }

    public void insertCategory(Category category, Callback<AddDto> callback) {
        Call<AddDto> call = categoryService.postCategory(ServiceUtil.categoryToCategoryDto(category));
        call.enqueue(callback);
    }

    public void updateCategory(Category category, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = categoryService.putCategory(ServiceUtil.categoryToCategoryDto(category));
        call.enqueue(callback);
    }

    public void deleteCategory(Category category, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = categoryService.deleteCategory(category.getCategoryId());
        call.enqueue(callback);
    }

    public void insertShoppingItem(ShoppingItem shoppingItem, Callback<AddDto> callback) {
        Call<AddDto> call = shoppingItemService.postShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem));
        call.enqueue(callback);
    }

    public void updateShoppingItem(ShoppingItem shoppingItem, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = shoppingItemService.putShoppingItem(ServiceUtil.shoppingItemToShoppingItemDto(shoppingItem));
        call.enqueue(callback);
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem, Callback<LocalDateTime> callback) {
        Call<LocalDateTime> call = shoppingItemService.deleteShoppingItem(shoppingItem.getShoppingItemId());
        call.enqueue(callback);
    }

//    public void loadAllElements(LocalDateTime savedTime, Callback<AllDto> callback) {
//        Call<AllDto> call = utilService.getAllDto(savedTime);
//        call.enqueue(callback);
//    }
//
//    public void loadAllElements(Callback<AllDto> callback) {
//        Call<AllDto> call = utilService.getAllDto();
//        call.enqueue(callback);
//    }
//
//    public void insertAllElements(AllDto elements, Callback<AllIdDto> callback) {
//        Call<AllIdDto> call = utilService.postAllElements(elements);
//        call.enqueue(callback);
//    }
//
//    public void updateAllElements(AllDto elements, Callback<LocalDateTime> callback) {
//        Call<LocalDateTime> call = utilService.putAllElements(elements);
//        call.enqueue(callback);
//    }
//
//    public void deleteAllElements(AllDto allDto, Callback<LocalDateTime> callback) {
//        Call<LocalDateTime> call = utilService.deleteAllDto(allDto);
//        call.enqueue(callback);
//    }

    public void synchronizeData(AllDto allDto, Callback<AllDto> callback) {
        Call<AllDto> call = utilService.synchronizeData(allDto);
        call.enqueue(callback);
    }
}


