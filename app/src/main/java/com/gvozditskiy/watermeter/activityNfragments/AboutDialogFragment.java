package com.gvozditskiy.watermeter.activityNfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.gvozditskiy.watermeter.R;

/**
 * Created by Alexey on 26.12.2016.
 */

public class AboutDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_about, null, false);
        return new AlertDialog.Builder(getContext())
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
