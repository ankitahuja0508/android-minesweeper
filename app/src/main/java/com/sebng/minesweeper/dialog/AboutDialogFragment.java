package com.sebng.minesweeper.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.sebng.minesweeper.BuildConfig;
import com.sebng.minesweeper.MSApplication;
import com.sebng.minesweeper.R;

public class AboutDialogFragment extends DialogFragment {
    private String mVersionName = null;

    public AboutDialogFragment() {
        // Required empty public constructor
    }

    public static AboutDialogFragment newInstance() {
        return new AboutDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_about, null);

        ((TextView) dialogView.findViewById(R.id.about__label_version)).setText(getVersionName());

        builder.setTitle(getString(R.string.about_dialog___title))
                .setView(dialogView)
                .setNegativeButton(R.string.dialog___button_close, null);

        return builder.create();
    }

    public String getVersionName() {
        if (mVersionName == null) {
            Activity activity = getActivity();
            if (activity != null) {
                Application application = activity.getApplication();
                if (application != null) {
                    PackageManager packageManager = application.getPackageManager();
                    try {
                        PackageInfo pInfo = packageManager.getPackageInfo(application.getPackageName(), 0);
                        mVersionName = String.format(getString(R.string.about_dialog___version_format), pInfo.versionName, pInfo.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        // should never happen
                        if (BuildConfig.DEBUG)
                            android.util.Log.d(MSApplication.LOG_TAG, "Unable to obtain package name");
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG)
                            android.util.Log.d(MSApplication.LOG_TAG, "Unable to obtain version name");
                    }
                }
            }
        }
        return mVersionName;
    }

    public void setVersionName(String versionName) {
        mVersionName = versionName;
    }
}
