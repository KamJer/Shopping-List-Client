package pl.kamjer.shoppinglist.util.sqlCipher;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SqlCipherKeyManager {

    private static final String KEY_ALIAS = "room_key";
    private static final String PREF_ENCRYPTED_KEY = "encrypted_key";
    private static final String PREF_IV = "encryption_iv";

    private final SharedPreferences sharedPreferences;
    private final KeyStore keyStore;

    public SqlCipherKeyManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initialize();
    }

    private void initialize() {
        generateKeystoreKeyIfNeeded();
        if (!sharedPreferences.contains(PREF_ENCRYPTED_KEY)) {
            generateAndEncryptSqlCipherKey();
        }
    }

    private void generateKeystoreKeyIfNeeded() {
        try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {

                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        "AndroidKeyStore"
                );

                KeyGenParameterSpec keyGenSpec =
                        new KeyGenParameterSpec.Builder(
                                KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                        )
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .build();

                keyGenerator.init(keyGenSpec);
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateAndEncryptSqlCipherKey() {
        try {
            SecretKey secretKey = getSecretKey(KEY_ALIAS);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] sqlCipherKey = new byte[32]; // 256-bit key
            new SecureRandom().nextBytes(sqlCipherKey);

            byte[] encryptedKey = cipher.doFinal(sqlCipherKey);
            byte[] iv = cipher.getIV();

            sharedPreferences.edit()
                    .putString(PREF_ENCRYPTED_KEY,
                            Base64.encodeToString(encryptedKey, Base64.NO_WRAP))
                    .putString(PREF_IV,
                            Base64.encodeToString(iv, Base64.NO_WRAP))
                    .apply();

            for (int i = 0; i < sqlCipherKey.length; i++) {
                sqlCipherKey[i] = 0;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getDecryptedSqlCipherKey(String encryptedKeyBase64, String ivBase64) {
        try {
            byte[] encryptedKey = Base64.decode(encryptedKeyBase64, Base64.NO_WRAP);
            byte[] ivBytes = Base64.decode(ivBase64, Base64.NO_WRAP);

            SecretKey secretKey = getSecretKey(KEY_ALIAS);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(128, ivBytes)
            );

            return cipher.doFinal(encryptedKey);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey getSecretKey(String keyAlias) {
        try {
            KeyStore.SecretKeyEntry entry =
                    (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, null);
            return entry.getSecretKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SupportOpenHelperFactory getSupportFactory() {
        String encryptedKey =
                sharedPreferences.getString(PREF_ENCRYPTED_KEY, "");
        String iv =
                sharedPreferences.getString(PREF_IV, "");

        byte[] decryptedKey =
                getDecryptedSqlCipherKey(encryptedKey, iv);

        return new SupportOpenHelperFactory(decryptedKey);
    }
}
