/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ambulance.corporate.Interfaces.NetworkChangeInterface;

/**
 * Created by sumit on 09-Apr-18.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    NetworkChangeInterface networkChangeInterface;

    /*
    * Manifest entry for the receiver
    *
    * <receiver android:name=".Receiver.NetworkChangeReceiver"
            android:enabled="true">
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>
    * */

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (networkChangeInterface != null) {
            networkChangeInterface.onNetworkChange(isConnected);
        }
    }
}
