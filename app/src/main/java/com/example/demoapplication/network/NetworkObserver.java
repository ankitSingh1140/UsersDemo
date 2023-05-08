package com.example.demoapplication.network;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

public class NetworkObserver {
    public static boolean isConnected(Activity a){
        ConnectivityManager connectivityManager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void getConnect(Activity a, NetworkObserverCallback callback){
        ConnectivityManager manager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull android.net.Network network) {
                callback.onAvailable();
            }

            @Override
            public void onLost(@NonNull android.net.Network network) {
                callback.onLost();
            }
        };
        manager.requestNetwork(networkRequest, networkCallback);
    }

}
