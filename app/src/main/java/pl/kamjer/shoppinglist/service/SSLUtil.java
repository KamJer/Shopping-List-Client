package pl.kamjer.shoppinglist.service;

import okhttp3.OkHttpClient;

public class SSLUtil {

    public static OkHttpClient.Builder getSSLContext() {
        OkHttpClient.Builder okHttpClient;
            okHttpClient = new OkHttpClient.Builder();
            okHttpClient.hostnameVerifier((s, sslSession) -> true);
        return okHttpClient;
    }
}
