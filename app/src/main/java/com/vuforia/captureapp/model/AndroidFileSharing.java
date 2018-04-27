package com.vuforia.captureapp.model;

import android.net.*;
import com.vuforia.captureapp.*;
import java.io.*;
import java.util.*;
import com.vuforia.captureapp.captureactivity.*;
import android.os.*;

public class AndroidFileSharing
{
    private ZipCompletionCallback _callback;
    private final List<String> _capturesToShare;

    public AndroidFileSharing() {
        this._capturesToShare = new ArrayList<String>();
    }

    private void doZip(final ArrayList<Uri> list) {
        final File captureShareDirectory = Util.getCaptureShareDirectory();
        Util.deleteFileRecursive(captureShareDirectory);
        captureShareDirectory.mkdirs();
        final Iterator<String> iterator = this._capturesToShare.iterator();
        while (iterator.hasNext()) {
            list.add(Uri.fromFile(Util.getOdFile(iterator.next())));
        }
    }

    public void addCaptureName(final String s) {
        this._capturesToShare.add(s);
    }

    public void zip(final ZipCompletionCallback callback) {
        this._callback = callback;
        try {
            new ModelZipTask().execute(new Void[0]);
        }
        catch (Exception ex) {
            DebugLog.LOGE("Loading tracking data set failed");
        }
    }

    private class ModelZipTask extends AsyncTask<Void, Integer, Boolean>
    {
        ArrayList<Uri> imageUris;

        private ModelZipTask() {
            this.imageUris = new ArrayList<Uri>();
        }

        protected Boolean doInBackground(final Void... array) {
            AndroidFileSharing.this.doZip(this.imageUris);
            return true;
        }

        protected void onPostExecute(final Boolean b) {
            final StringBuilder append = new StringBuilder().append("BuilderNewSequenceTask::onPostExecute: execution ");
            String s;
            if (b) {
                s = "successful";
            }
            else {
                s = "failed";
            }
            DebugLog.LOGD(append.append(s).toString());
            AndroidFileSharing.this._callback.onZipDone(this.imageUris);
        }
    }

    public interface ZipCompletionCallback
    {
        void onZipDone(final ArrayList<Uri> p0);
    }
}
