package com.vishnu.app.currentlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.vishnu.app.MyApplication;
import com.vishnu.app.PreferenceManager;
import com.vishnu.app.constants.Result;
import com.vishnu.app.constants.SharedPreferenceKeys;

import java.lang.ref.WeakReference;

/**
 * The CurrentLocationManager is used to fetch the CurrentLocation By connecting to GooglePlay services. Call {@code #fetchLocation} method to request for current location.
 */
public class CurrentLocationManager extends BaseLocationManager {

    /** The interval for connection timeout*/
    public static final int CONNECTION_TIMEOUT = 30000;     // IN MILLI SECONDS
    /** The Listener to listen for Current Location changes */
    private CurrentLocationListener mListener;

    private static CurrentLocationManager sInstance = null;

    public static CurrentLocationManager getInstance() {
        if (sInstance == null) {
            sInstance = new CurrentLocationManager(EhsApplication.getContext());
        }
        return sInstance;
    }

    /**
     * Parameterized constructor registers the Context to communicate with GooglePlayServices
     *
     * @param context Application Context to initialize the FusedLocationServices Api Client
     */
    private CurrentLocationManager(Context context) {
        super(context);
    }

    public void setLocationCallbacks(CurrentLocationListener currentLocationListener) {
        this.mListener = new WeakReference<CurrentLocationListener>(currentLocationListener).get();
    }

    public void fetchLocation() {
        /* sets a timeout for fetching the location.*/
        setLocationTimeout();
        setStatus(Result.STARTED);
        connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        cancelLocationTimeOut();
        setStatus(Result.SUCCESS);
        setCurrentLocation(location);
        if (mListener != null) {
            mListener.onLocationSuccess(location);
        }
        disConnect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        cancelLocationTimeOut();
        setStatus(Result.FAILED);
        setCurrentLocation(null);
        if (mListener != null) {
            mListener.onLocationFailed(connectionResult.getErrorCode());
        }
        disConnect();
    }

    @Override
    public Location getLastKnownLocation() {
        return super.getLastKnownLocation();
    }

    public static void reset() {
        setStatus(Result.ERROR);
        setCurrentLocation(null);
    }

    public static void setStatus(int status) {
        PreferenceManager.setInt(SharedPreferenceKeys.KEY_CURRENT_LOCATION_STATUS, status);
    }

    public static int getStatus() {
        return PreferenceManager.getInt(SharedPreferenceKeys.KEY_CURRENT_LOCATION_STATUS);
    }

    public static boolean isLocationSuccess() {
        return (getStatus() == Result.SUCCESS);
    }

    public static boolean isLocationInProgress() {
        return (getStatus() == Result.STARTED);
    }

    public static void setCurrentLocation(Location location) {
        PreferenceManager.setObject(SharedPreferenceKeys.KEY_CURRENT_LOCATION, location);
    }

    public static Location getCurrentLocation() {
        if (isLocationSuccess()) {
            return PreferenceManager.getObject(SharedPreferenceKeys.KEY_CURRENT_LOCATION, Location.class);
        }
        return null;
    }

    public static void clear() {
        setStatus(Result.ERROR);
        PreferenceManager.setObject(SharedPreferenceKeys.KEY_CURRENT_LOCATION, null);
    }
}
