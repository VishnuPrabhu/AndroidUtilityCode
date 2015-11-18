package com.themesoft.togonosh.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

/**
 * Created by Vishnu on 9/15/2015.
 */
public class DialogFragmentManager {

    private FragmentManager mManager = null;

    private ProgressDialog mProgressDialog = null;

    private AlertDialog mAlertDialog = null;

    private Activity mActivityContext = null;


    public DialogFragmentManager(Fragment fragment) {
        if (fragment != null) {
            mActivityContext = fragment.getActivity();
            mManager = fragment.getActivity().getSupportFragmentManager();
        }
    }

    public DialogFragmentManager(FragmentActivity activity) {
        if (activity != null) {
            mActivityContext = activity;
            mManager = activity.getSupportFragmentManager();
        }
    }

    // Following is a hack to display Dialog fragments when app is in background / onstop.
    // Hack to fix : java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    // http://stackoverflow.com/a/16206036
    public void showDialog(DialogFragment dialog, String tag) {
        try {
            dialog.show(mManager, tag);
        } catch (IllegalStateException e) {
            FragmentTransaction ft = mManager.beginTransaction();
            ft.add(dialog, tag);
            ft.commitAllowingStateLoss();
        }
    }

    public void showConfirmationDialog(String title, String message, String positiveBtnText, String negativeBtnText, DialogInterface.OnClickListener positiveBtnClickListener, DialogInterface.OnCancelListener dialogCancelListener) {
        // dismiss old dialog if showing
        if (mAlertDialog != null) {
            dismissConfirmationDialog();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivityContext);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(positiveBtnText) && positiveBtnClickListener != null) {
            builder.setPositiveButton(positiveBtnText, positiveBtnClickListener);
        }
        if (!TextUtils.isEmpty(negativeBtnText)) {
            builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        if (dialogCancelListener != null) {
            builder.setOnCancelListener(dialogCancelListener);
        }
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void updateConfirmationMessage(String message) {
        if (mAlertDialog != null) {
            mAlertDialog.setMessage(message);
        }
    }

    public void dismissConfirmationDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    public void showProgressDialog(String message) {
        // dismiss old dialog
        if (mProgressDialog != null) {
            dismissProgressDialog();
        }
        mProgressDialog = new ProgressDialog(mActivityContext);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void updateProgressMessage(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
