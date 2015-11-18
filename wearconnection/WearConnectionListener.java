package com.prokarma.wearpoc.connection;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listener used to handle the connection between the application and the wear.
 */
public class WearConnectionListener implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * The GoogleApiClient to connect to GooglePlayServices to access the Wearable
     */
    private GoogleApiClient mGoogleApiClient = null;
    /**
     * The listener to register for DataItem changed callback
     */
    private DataApi.DataListener mDataListener = null;
    /**
     * The listener to register for Message received callback
     */
    private MessageApi.MessageListener mMessageListener = null;
    /**
     * The listener to register for Node connected callback
     */
    private NodeApi.NodeListener mNodeListener = null;
    /**
     * The Listener used to dispatch Connection callback events
     */
    private WearConnectionCallBacks mConnectionCallBacks = null;


    /**
     * Default Constructor. Initializing using this default constructor does not register any
     * listeners or callbacks.
     */
    public WearConnectionListener(Context context) {
        if (context != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(
                    Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(
                    this).build();
        }
    }

    /**
     * Parameterized constructor registers the corresponding listeners and callbacks.
     *
     * @param callBacks       The Listener used to dispatch Connection callback events
     * @param dataListener    The listener to respond to dataChanges
     * @param messageListener The listener to respond to message received
     * @param nodeListener    The listener to respond to node connected
     */
    public WearConnectionListener(Context context, WearConnectionCallBacks callBacks,
            DataApi.DataListener dataListener,
            MessageApi.MessageListener messageListener,
            NodeApi.NodeListener nodeListener) {
        this(context);
        this.mConnectionCallBacks = callBacks;
        this.mDataListener = dataListener;
        this.mMessageListener = messageListener;
        this.mNodeListener = nodeListener;
    }

    /**
     * Returns the GoogleApiClient attached with this listener
     *
     * @return GoogleApiClient
     */
    public GoogleApiClient getClient() {
        return mGoogleApiClient;
    }

    /**
     * Connects the {@link #mGoogleApiClient} to Google Play services. This method returns
     * immediately,
     * and connects to the service in the background. If the connection is successful,
     * onConnected(Bundle) is called and enqueued items are executed. On a failure,
     * onConnectionFailed(ConnectionResult) is called.
     */
    public void connect() {
        mGoogleApiClient.connect();
    }

    /**
     * Checks if the {@link #mGoogleApiClient} is currently connected to the service, so that
     * requests to other methods
     * will succeed. Applications should guard client actions caused by the user with a call to
     * this method.
     *
     * @return true if the client is connected to the service, false otherwise
     */
    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    /**
     * Checks if the client is attempting to connect to the service.
     *
     * @return true if the client is attempting to connect to the service, false otherwise
     */
    public boolean isConnecting() {
        return mGoogleApiClient.isConnecting();
    }

    /**
     * Closes the connection to Google Play services. No calls can be made using this {@link
     * #mGoogleApiClient} after
     * calling this method. Any method calls that haven't executed yet will be canceled. That is
     * onResult(Result)
     * won't be called, if connection to the service hasn't been established yet all calls
     * already made will be
     * canceled.
     */
    public void disConnect() {
        try {
            if (mDataListener != null) {
                Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
            }
            if (mMessageListener != null) {
                Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
            }
            if (mNodeListener != null) {
                Wearable.NodeApi.removeListener(mGoogleApiClient, mNodeListener);
            }

            if ((mGoogleApiClient != null) &&
                    (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mDataListener != null) {
            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
        }
        if (mMessageListener != null) {
            Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
        }
        if (mNodeListener != null) {
            Wearable.NodeApi.addListener(mGoogleApiClient, mNodeListener);
        }

        // TODO  Vishnu : check this callback is needed
        if (mConnectionCallBacks != null) {
            mConnectionCallBacks.onConnectionSuccess();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (mConnectionCallBacks != null) {
            mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.CONNECTION_SUSPENDED);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mConnectionCallBacks != null) {
            mConnectionCallBacks.onGooglePlayServiceError(connectionResult);
        }
    }

    /**
     * Returns the data item modified in this event.
     * An event of TYPE_DELETED will only have its {DataItem#getUri} populated.
     *
     * @param dataEvents The data buffer
     * @param dataPath   The data path
     * @return The data item corresponding to the data path or null.
     */
    public DataItem getData(DataEventBuffer dataEvents, String dataPath) {
        if (dataPath == null) {
            if (mConnectionCallBacks != null) {
                mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.PATH_NULL_ERROR);
            }
            return null;
        }

        List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.release();
        for (DataEvent event : events) {
            String path = event.getDataItem().getUri().getPath();
            if (dataPath.equalsIgnoreCase(path)) {
                return event.getDataItem();
            }
        }
        return null;
    }

    /**
     * Returns the message received from the callback
     *
     * @param messageEvent The message event that contains the message
     * @param messagePath  The path of the message intended to
     * @return The string message or null
     */
    public String getMessage(MessageEvent messageEvent, String messagePath) {
        if (messagePath == null) {
            if (mConnectionCallBacks != null) {
                mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.PATH_NULL_ERROR);
            }
            return null;
        }
        String path = messageEvent.getPath();

        if (messagePath.equalsIgnoreCase(path)) {
            return new String(messageEvent.getData());
        } else {
            return null;
        }
    }

    /**
     * Adds DataItem to the Android Wear network. The updated item is synchronized across all
     * devices.
     *
     * @param dataPath The path to the data
     * @param data     The data to send
     */
    public void sendData(String dataPath, DataMap data) {
        sendData(dataPath, data, null, true, true);
    }

    /**
     * Adds DataItem to the Android Wear network. The updated item is synchronized across all
     * devices.
     *
     * @param dataPath The path to the data
     * @param data     The data to send
     * @param callBack The callback to receive the response
     */
    public void sendData(String dataPath, DataMap data,
            ResultCallback<DataApi.DataItemResult> callBack) {
        sendData(dataPath, data, callBack, true, true);
    }

    /**
     * Adds DataItem to the Android Wear network. The updated item is synchronized across all
     * devices.
     *
     * @param dataPath The path to the data
     * @param data     The data to send
     * @param sendImmediately source : http://android-developers.blogspot.in/2015/11/whats-new-in-google-play-services-83.html
     *                        With Google Play services 8.3, we’ve updated the DataApi to allow for urgency in how
     *                        data items are synced. Now, a priority can be added to the data item to determine when
     *                        it should be synced. For example, if you are building an app that requires immediate
     *                        syncing, such as a remote control app, it can still be done immediately by calling
     *                        setUrgent(), but for something such as updating your contacts, you could tolerate some
     *                        delay. Non-urgent DataItems may be delayed for up to 30 minutes, but you can expect
     *                        that in most cases they will be delivered within a few minutes. Low priority is now the
     *                        default, so setUrgent() is needed to obtain the previous timing.
     */
    public void sendData(String dataPath, DataMap data, boolean sendImmediately) {
        sendData(dataPath, data, null, true, sendImmediately);
    }

    /**
     * Adds DataItem to the Android Wear network. The updated item is synchronized across all
     * devices.
     *
     * @param dataPath The path to the data
     * @param data     The data to send
     * @param callBack The callback to receive the response
     * @param sendImmediately source : http://android-developers.blogspot.in/2015/11/whats-new-in-google-play-services-83.html
     *                        With Google Play services 8.3, we’ve updated the DataApi to allow for urgency in how
     *                        data items are synced. Now, a priority can be added to the data item to determine when
     *                        it should be synced. For example, if you are building an app that requires immediate
     *                        syncing, such as a remote control app, it can still be done immediately by calling
     *                        setUrgent(), but for something such as updating your contacts, you could tolerate some
     *                        delay. Non-urgent DataItems may be delayed for up to 30 minutes, but you can expect
     *                        that in most cases they will be delivered within a few minutes. Low priority is now the
     *                        default, so setUrgent() is needed to obtain the previous timing.
     */
    public void sendData(String dataPath, DataMap data,
            ResultCallback<DataApi.DataItemResult> callBack, boolean sendImmediately) {
        sendData(dataPath, data, callBack, true, sendImmediately);
    }

    /**
     * Adds DataItem to the Android Wear network. The updated item is synchronized across all
     * devices.
     *
     * @param dataPath        The path to the data
     * @param data            The data to send
     * @param callBack        The callback to receive the response
     * @param isAsynchronous  send data asynchronously
     * @param sendImmediately source : http://android-developers.blogspot.in/2015/11/whats-new-in-google-play-services-83.html
     *                        With Google Play services 8.3, we’ve updated the DataApi to allow for urgency in how
     *                        data items are synced. Now, a priority can be added to the data item to determine when
     *                        it should be synced. For example, if you are building an app that requires immediate
     *                        syncing, such as a remote control app, it can still be done immediately by calling
     *                        setUrgent(), but for something such as updating your contacts, you could tolerate some
     *                        delay. Non-urgent DataItems may be delayed for up to 30 minutes, but you can expect
     *                        that in most cases they will be delivered within a few minutes. Low priority is now the
     *                        default, so setUrgent() is needed to obtain the previous timing.
     */
    public void sendData(String dataPath, DataMap data, ResultCallback<DataApi.DataItemResult> callBack,
            boolean isAsynchronous, boolean sendImmediately) {
        if (!isConnected()) {
            if (mConnectionCallBacks != null) {
                mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.NETWORK_ERROR);
            }
            return;
        } else if (dataPath == null) {
            if (mConnectionCallBacks != null) {
                mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.PATH_NULL_ERROR);
            }
            return;
        } else if (data == null) {
            Log.d("Send DataMap", "Data cannot be null");
            return;
        }

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(dataPath);
        putDataMapRequest.getDataMap().putAll(data);

        /** Current time is also sent with data, just to make it a new data*/
        putDataMapRequest.getDataMap().putString(WearConnectionConstants.KEY.CURRENT_TIME,
                String.valueOf(System.currentTimeMillis()));

        PutDataRequest request = putDataMapRequest.asPutDataRequest();

        // update from google play service 8.3. refer comments above
        if (sendImmediately) { request.setUrgent(); }

        if (isAsynchronous) {
            /** You will get callback after data is sent use the below code */
            PendingResult<DataApi.DataItemResult> dataResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request);
            if (callBack != null) {
                dataResult.setResultCallback(callBack);
            }
        } else {
            if (isRunningOnMainThread()) {
                if (mConnectionCallBacks != null) {
                    mConnectionCallBacks.onConnectionFailed(
                            WearConnectionCallBacks.METHOD_CALLED_FROM_UI_THREAD);
                }
                return;
            }
            Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
        }
    }

    /**
     * Sends byte[] data to the specified node.
     *
     * @param messagePath The path of the message
     * @param message     The message to send
     */
    public void sendMessage(String messagePath, String message) {
        sendMessage(messagePath, message, null, true);
    }

    /**
     * Sends byte[] data to the specified node.
     *
     * @param messagePath The path of the message
     * @param message     The message to send
     * @param callback    The callback to receive the response
     */
    public void sendMessage(String messagePath, String message,
            ResultCallback<MessageApi.SendMessageResult> callback) {
        sendMessage(messagePath, message, callback, true);
    }

    /**
     * Sends byte[] data to the specified node.
     *
     * @param messagePath    The path of the message
     * @param message        The message to send
     * @param callback       The callback to receive the response
     * @param isAsynchronous send data asynchronously
     */
    public void sendMessage(final String messagePath, final String message,
            final ResultCallback<MessageApi.SendMessageResult> callback,
            final boolean isAsynchronous) {
        if (!isConnected()) {
            if (mConnectionCallBacks != null) {
                mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.NETWORK_ERROR);
            }
            return;
        } else if (messagePath == null) {
            if (mConnectionCallBacks != null) {
                mConnectionCallBacks.onConnectionFailed(WearConnectionCallBacks.PATH_NULL_ERROR);
            }
            return;
        }

        if (isAsynchronous) {
            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<NodeApi
                            .GetConnectedNodesResult>() {
                        @Override
                        public void onResult(
                                NodeApi.GetConnectedNodesResult connectedNodesResult) {
                            List<Node> connectedNodes = connectedNodesResult.getNodes();

                            for (Node node : connectedNodes) {

                                String nodeId = node.getId();
                                PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi
                                        .sendMessage(mGoogleApiClient, nodeId, messagePath,
                                                message.getBytes());
                                if (callback != null) {
                                    messageResult.setResultCallback(callback);
                                }
                            }
                        }
                    });
        } else {
            if (isRunningOnMainThread()) {
                if (mConnectionCallBacks != null) {
                    mConnectionCallBacks.onConnectionFailed(
                            WearConnectionCallBacks.METHOD_CALLED_FROM_UI_THREAD);
                }
                return;
            }
            NodeApi.GetConnectedNodesResult connectedNodesResult = Wearable.NodeApi
                    .getConnectedNodes(mGoogleApiClient).await();
            List<Node> connectedNodes = connectedNodesResult.getNodes();

            for (Node node : connectedNodes) {

                String nodeId = node.getId();
                Wearable.MessageApi
                        .sendMessage(mGoogleApiClient, nodeId, messagePath, message.getBytes())
                        .await();
            }
        }

    }

    /**
     * Returns TRUE if the current thread is Main / Ui thread and FALSE otherwise.
     *
     * @return whether the current thread is Main thread or not.
     */
    private boolean isRunningOnMainThread() {
        return ((Looper.myLooper() != null) && (Looper.myLooper() == Looper.getMainLooper()));
    }

    /**
     * This should be used only in WearService.
     * Connects the client to Google Play services. Blocks the connection for a timeout of {@code
     * waitTime} either
     * until succeeds or fails. This is not allowed on the UI thread.
     *
     * @return the result of the connection
     */
    public boolean waitForConnection(int waitTime) {
        ConnectionResult connectionResult = mGoogleApiClient
                .blockingConnect(waitTime, TimeUnit.SECONDS);
        return connectionResult.isSuccess();
    }
}
