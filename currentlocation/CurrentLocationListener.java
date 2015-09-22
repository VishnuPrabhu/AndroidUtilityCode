package com.vishnu.app.currentlocation;

import android.location.Location;

/**
 * Used for receiving notifications from the CurrentLocationManager when the location has changed. These methods are
 * called if the LocationListener has been registered with the CurrentLocationManager.
 */
public interface CurrentLocationListener {
    /**
     * Called when the location has changed.
     * @param currentLocation The new location, as a Location object.
     */
    void onLocationSuccess(Location currentLocation);

    /**
     * Called when the Location is failed or Timed out
     * @param errorCode The error for Location Failure.
     */
    void onLocationFailed(int errorCode);
}
