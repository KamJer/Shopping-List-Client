package pl.kamjer.shoppinglist.util;


import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import pl.kamjer.shoppinglist.util.funcinterface.ConnectionStateChange;

@Log
@AllArgsConstructor
public class NetworkReceiver {

    public static void register(Context context,
                                 ConnectionStateChange connectAction,
                                 ConnectionStateChange failureAction) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                connectAction.change(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                failureAction.change(network);
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

}
