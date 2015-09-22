package com.vishnu.app;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Interface for interacting with {@link Fragment} objects from an activity.
 */
public interface FragmentTransactionManager {

    /**
     * Returns the id of the container where the fragemnt transaction takes place.
     */
    public int getContainerId();

    /**
     * Adds a fragment to the container.
     */
    public void add(Fragment fragment, String tag);

    /**
     * Replaces the fragment in the container. The old fragment will be removed.
     */
    public void replace(Fragment fragment, String tag);

    /**
     * Add the fragment in the container with the BackStack Entry.
     */
    public void addToStack(Fragment fragment, String fragmentTag, String stackTag);

    /**
     * Replaces the fragment in the container and adding to the BackStack Entry. The old fragment is added to backstack.
     */
    public void replaceToStack(Fragment fragment, String fragmentTag, String stackTag);

    /**
     * Finishes the current fragment.
     */
    public void finishFragment();

    /**
     * Display the dialog, adding the fragment to the given FragmentManager.
     */
    public void showDialog(DialogFragment dialog, String tag);
}
