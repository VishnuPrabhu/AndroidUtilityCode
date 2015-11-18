package com.prokarma.wearpoc.connection;

import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * The service class runs in the wearable and listens for data changes, message changes and node connection changes.
 */
public class WearService extends WearableListenerService {

    private static final String TAG = "WearService";
    /** The Listener to handle the connection between the wearable and the device.*/
    private WearConnectionListener mConnectionListener = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectionListener = new WearConnectionListener(getApplicationContext());
        mConnectionListener.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.d(TAG, "onDataChanged");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(TAG, "onMessageReceived");
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.d(TAG, "onPeerConnected");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.d(TAG, "onPeerDisconnected");
    }

    @Override
    public void onDestroy() {
        try {
            mConnectionListener.disConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
        Log.d(TAG, "Service OnDestroy() called");
    }

    /**
     * Returns the connection listener attached with this service. This connection listener is used to add connection
     * with the Mobile device.
     * @return The Wear Connection Listener.
     */
    protected WearConnectionListener getConnectionListener() {
        return mConnectionListener;
    }
}
