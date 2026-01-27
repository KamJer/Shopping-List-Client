package pl.kamjer.shoppinglist.util.sqlCipher;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import net.zetetic.database.sqlcipher.SupportOpenHelperFactory;

public class DisposableKeySupportFactory extends SupportOpenHelperFactory {
    private final byte[] decryptedKey;

    public DisposableKeySupportFactory(byte[] decryptedKey) {
        super(decryptedKey);
        this.decryptedKey = decryptedKey;
    }

    @NonNull
    @Override
    public SupportSQLiteOpenHelper create(@NonNull SupportSQLiteOpenHelper.Configuration configuration) {
        SupportSQLiteOpenHelper helper = super.create(configuration);

        for (int i = 0; i < decryptedKey.length; i++) {
            decryptedKey[i] = 0;
        }
        return helper;
    }
}
