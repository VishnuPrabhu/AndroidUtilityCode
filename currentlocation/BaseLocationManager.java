package com.vishnu.app.currentlocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * BaseLocationManager is used as a Base Class for Implementing and Handling the CurrentLocation feature.
 */
public abstract class BaseLocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /** The GoogleApiClient to connect to GooglePlayServices to access the Current Location */
    private final GoogleApiClient mLocationClient;
    /** The context of the application */
    private final Context mContext;
    /** Location Connection TimeOut Handler */
    private CountDownTimer mTimeOut;


    /**
     * Parameterized constructor registers the Activity to send callBack if GooglePlayServices is not available.
     *
     * @param context Application Context to initialize the LocationServices Api Client
     */
    public BaseLocationManager(Context context) {
        if (context == null) {
            throw new NullPointerException("Initialize CurrentLocationListener with the application/activity context");
        }

        this.mContext = context;
        this.mLocationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Connects the {@link #mLocationClient} to Google Play services. This method returns immediately,
     * and connects to the service in the background. If the connection is successful,
     * onConnected(Bundle) is called and enqueued items are executed. On a failure,
     * onConnectionFailed(ConnectionResult) is called.
     */
    public void connect() {
        if (isConnected()) {
            startPeriodicUpdates();
        } else if (!isConnecting()) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

            if (resultCode == ConnectionResult.SUCCESS) {
                mLocationClient.connect();
            } else {
                onConnectionFailed(new ConnectionResult(resultCode, null));
            }
        }
    }

    /**
     * Checks if the {@link #mLocationClient} is currently connected to the service, so that requests to other methods
     * will succeed. Applications should guard client actions caused by the user with a call to this method.
     * @return true if the client is connected to the service, false otherwise
     */
    public boolean isConnected() {
        return mLocationClient.isConnected();
    }

    /**
     * Checks if the client is attempting to connect to the service.
     * @return true if the client is attempting to connect to the service, false otherwise
     */
    public boolean isConnecting() {
        return mLocationClient.isConnecting();
    }

    /**
     * Closes the connection to Google Play services. No calls can be made using this {@link #mLocationClient} after
     * calling this method. Any method calls that haven't executed yet will be canceled. That is onResult(SearchItem)
     * won't be called, if connection to the service hasn't been established yet all calls already made will be
     * canceled.
     */
    public void disConnect() {
        stopPeriodicUpdates();
        mLocationClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        stopPeriodicUpdates();
        onConnectionFailed(new ConnectionResult(ConnectionResult.NETWORK_ERROR, null));
    }

    /**
     * Invokes the location services and calls {@link #onLocationChanged(Location)} once location is
     * retrieved
     */
    private void startPeriodicUpdates() {
        /** The LocationRequest required to retrieve location*/
        LocationRequest requestParams = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, requestParams, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (!status.isSuccess()) {
                            Log.e("BaseLocationManager", "Unable to start updates of Current location");
                        }
                    }
                });
    }

    /**
     * Creates a LocationRequest with default Location Retrieving Conditions
     * @return The Location Request to fetch the current location.
     */
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);                                                  // in milliseconds
        locationRequest.setFastestInterval(500);                                            // in milliseconds
        // below code is commented because we are using CountDownTimer for handling conenction timeout.
        // locationRequest.setExpirationDuration(CurrentLocationManager.CONNECTION_TIMEOUT);   // in milliseconds
        locationRequest.setNumUpdates(1);                                           // No of Location Updates wanted

        return locationRequest;
    }

    /**
     * When {@link #startPeriodicUpdates()} is called and {@link #onLocationChanged(Location)}
     * Location is not fetched. and the connection goes to Error within the {@code
     * LocationRequest#setExpirationDuration} or Client is disconnected. remove the location updates.
     */
    private void stopPeriodicUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (!status.isSuccess()) {
                            Log.e("BaseLocationManager", "Unable to remove updates of Current location");
                        }
                    }
                });
    }

    /**
     * Returns the best most recent location currently available.
     * @return Location instance
     */
    public Location getLastKnownLocation() {
        Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if (lastKnownLocation == null) {
            lastKnownLocation = new Location("Earth zero lat long");
        }
        return lastKnownLocation;
    }

    /** Sets the timeout until a connection is etablished. */
    protected void setLocationTimeout() {
        if (mTimeOut == null) {
            mTimeOut = new CountDownTimer(CurrentLocationManager.CONNECTION_TIMEOUT, DateUtils.SECOND_IN_MILLIS) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    onConnectionFailed(new ConnectionResult(ConnectionResult.TIMEOUT, null));
                }
            };
        }
        mTimeOut.start();
    }

    /** Cancels the timeout used for establishing a connection.*/
    protected void cancelLocationTimeOut() {
        if (mTimeOut != null) {
            mTimeOut.cancel();
        }
    }

}
