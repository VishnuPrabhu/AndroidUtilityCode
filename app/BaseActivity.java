package com.vishnu.app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.vishnu.app.FragmentTransactionManager;
import com.vishnu.app.R;

import java.util.ArrayList;

/**
 * Base Activity for handling the common operations inside an Activity.
 */
public class BaseActivity extends AppCompatActivity implements FragmentTransactionManager {

    private boolean stateLost = false;

    private ArrayList<FragmentTransaction> mPendingTransactionQueue = new ArrayList<FragmentTransaction>();

    private int mContainerId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        mContainerId = getContainerId();

        if (savedInstanceState == null) {
            onCommitActivityLaunchFragment();
        }
    }

    /**
     * Called when the activity has been created for the first time. Sub-Classes can override this method to display
     * a fragment, when the activity is created for the first time. The SuperClass(MbraceActivity) will have no
     * implementation of this method since it is just a base activity.
     */
    public void onCommitActivityLaunchFragment() {}


    @Override
    protected void onStart() {
        super.onStart();
        setStateLost(false);
        commitPendingTransactions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setStateLost(true);
    }

    private void setStateLost(boolean isLost) {
        stateLost = isLost;
    }

    private boolean isStateLost() {
        return stateLost;
    }

    @Override
    public int getContainerId() {
        return R.id.mbrace_fragmentContainer;
    }

    @Override
    public void add(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(mContainerId, fragment, tag);
        commitTransaction(transaction);
    }

    @Override
    public void replace(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right_alone, R.anim.slide_out_left_alone, R.anim.slide_in_left_alone, R.anim.slide_out_right_alone);
        transaction.replace(mContainerId, fragment, tag);
        commitTransaction(transaction);
    }

    @Override
    public void addToStack(Fragment fragment, String fragmentTag, String stackTag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(mContainerId, fragment, fragmentTag);
        transaction.addToBackStack(stackTag);
        commitTransaction(transaction);
    }

    @Override
    public void replaceToStack(Fragment fragment, String fragmentTag, String stackTag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right_alone, R.anim.slide_out_left_alone, R.anim.slide_in_left_alone, R.anim.slide_out_right_alone);
        transaction.replace(mContainerId, fragment, fragmentTag);
        transaction.addToBackStack(stackTag);
        commitTransaction(transaction);
    }

    @Override
    public void finishFragment() {
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStackImmediate();
    }

    // Following is a hack to display Dialog fragments when app is in background / onstop.
    // Hack to fix : java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    // http://stackoverflow.com/a/16206036
    @Override
    public void showDialog(DialogFragment dialog, String tag) {
        try {
            dialog.show(getSupportFragmentManager(), tag);
        } catch (IllegalStateException e) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(dialog, tag);
            addTransactionToQueue(ft);
        }
    }

    private void commitTransaction(FragmentTransaction transaction) {
        if (!isStateLost()) {
            transaction.commit();
        } else {
            addTransactionToQueue(transaction);
        }
    }

    private void addTransactionToQueue(FragmentTransaction transaction) {
        mPendingTransactionQueue.add(transaction);
    }

    private void commitPendingTransactions() {
        if (mPendingTransactionQueue != null) {
            for (FragmentTransaction transaction : mPendingTransactionQueue) {
                transaction.commit();
            }
            mPendingTransactionQueue.clear();
        }
    }
}
