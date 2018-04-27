package com.vuforia.captureapp.model;

import android.os.*;
import android.app.*;
import android.content.*;

public class WaitDialogFragment extends DialogFragment
{
    private String _message;
    private String _title;

    public void info(final String title, final String message) {
        this._title = title;
        this._message = message;
    }

    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }

    public Dialog onCreateDialog(final Bundle bundle) {
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setTitle(this._title);
        progressDialog.setMessage("\n" + this._message + "\n\n\n\n\n");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        return progressDialog;
    }
}
