package com.prokarma.wearpoc.connection;

import com.google.android.gms.common.ConnectionResult;

/**
 * Interface definition for a callback to be invoked when a Connection callback is triggered.
 */
public interface WearConnectionCallBacks {
    String NETWORK_ERROR = "network_failure";
    String CONNECTION_SUSPENDED = "connection_suspended";
    String METHOD_CALLED_FROM_UI_THREAD = "method_called_from_ui_thread";
    String PATH_NULL_ERROR = "path_null";

    public void onConnectionSuccess();
    public void onConnectionFailed(String cause);
    public void onGooglePlayServiceError(ConnectionResult error);
}
