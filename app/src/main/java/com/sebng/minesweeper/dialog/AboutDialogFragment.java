package com.sebng.minesweeper.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.sebng.minesweeper.R;

public class AboutDialogFragment extends DialogFragment {

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

        builder.setTitle(getString(R.string.about_dialog___title))
                .setView(dialogView)
                .setNegativeButton(R.string.dialog___button_close, null);

        return builder.create();
    }
}
