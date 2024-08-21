package pl.kamjer.shoppinglist.service;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import pl.kamjer.shoppinglist.R;
import pl.kamjer.shoppinglist.model.User;

public class SSLUtil {

    public static OkHttpClient.Builder getSSLContext(Context context) {
        OkHttpClient.Builder okHttpClient;
        try {
            InputStream keystoreStream = context.getResources().openRawResource(R.raw.public_key);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) cf.generateCertificate(keystoreStream);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null); // Inicjalizuj pusty keystore
            keyStore.setCertificateEntry("ca", caCert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Inicjalizacja SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Tworzenie OkHttpClient z SSL
            okHttpClient = new OkHttpClient.Builder();
//          TODO: delete it before production, this is for testing
            okHttpClient.hostnameVerifier((hostname, session) -> true);
            okHttpClient.sslSocketFactory(sslContext.getSocketFactory(),
                    (X509TrustManager) trustManagerFactory.getTrustManagers()[0]);

        } catch (CertificateException | IOException | NoSuchAlgorithmException |
                 KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return okHttpClient;
    }
}
