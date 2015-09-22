package com.vishnu.app.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Vishnu on 9/15/2015.
 */
public class NetworkUtil {

    public static boolean isNetworkAvailable() {
        Context appContext = MyApplication.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isGpsTurnedOn() {
        Context appContext = MyApplication.getContext();
        LocationManager locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }
}
