/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.Fragment
 *  android.app.FragmentManager
 *  android.content.res.Resources
 *  android.os.AsyncTask
 *  android.os.Bundle
 */
package com.vuforia.captureapp.model;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import com.vuforia.captureapp.R;
import com.vuforia.captureapp.captureactivity.DebugLog;
import com.vuforia.captureapp.model.WaitDialogFragment;

public class PersitenceModelFragment
extends Fragment {
    private static final String TAG_FRAGMENT = "tag_fragment_wait_save_dialog";
    private PersistenceCallback _persistenceCallback;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void load(String string2) {
        try {
            WaitDialogFragment waitDialogFragment = new WaitDialogFragment();
            waitDialogFragment.info(this.getResources().getString(R.string.wait_load_title), this.getResources().getString(R.string.wait_load_message));
            waitDialogFragment.show(this.getFragmentManager(), "tag_fragment_wait_save_dialog");
        }
        catch (Exception exception) {
            DebugLog.LOGW("Waiting for load dialog could not be shown");
        }
        try {
            ModelLoadTask modelLoadTask = new ModelLoadTask();
            modelLoadTask.execute((String[]) new String[]{string2});
            return;
        }
        catch (Exception exception) {
            DebugLog.LOGE("Loading model failed");
            return;
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this._persistenceCallback = (PersistenceCallback)activity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setRetainInstance(true);
    }

    public void onDetach() {
        super.onDetach();
        this._persistenceCallback = null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void onLoadDone() {
        try {
            WaitDialogFragment waitDialogFragment = (WaitDialogFragment)this.getFragmentManager().findFragmentByTag("tag_fragment_wait_save_dialog");
            if (waitDialogFragment != null) {
                waitDialogFragment.dismissAllowingStateLoss();
            }
            this._persistenceCallback.onLoadDone();
            return;
        }
        catch (Exception exception) {
            DebugLog.LOGW("Load dialog could not be dismissed");
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void onSaveDone() {
        try {
            WaitDialogFragment waitDialogFragment = (WaitDialogFragment)this.getFragmentManager().findFragmentByTag("tag_fragment_wait_save_dialog");
            if (waitDialogFragment != null) {
                waitDialogFragment.dismissAllowingStateLoss();
            }
            this._persistenceCallback.onSaveDone();
            return;
        }
        catch (Exception exception) {
            DebugLog.LOGW("Save dialog could not be dismissed");
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void save(String string2) {
        Object object;
        try {
            WaitDialogFragment waitDialogFragment = new WaitDialogFragment();
            waitDialogFragment.info(this.getResources().getString(R.string.wait_save_title), this.getResources().getString(R.string.wait_save_message));
            waitDialogFragment.show(this.getFragmentManager(), "tag_fragment_wait_save_dialog");
        }
        catch (Exception exception) {
            DebugLog.LOGW("Waiting for save dialog could not be shown");
        }
        try {
            ModelSaveTask modelSaveTask = new ModelSaveTask();
            modelSaveTask.execute((String[]) new String[]{string2});
            return;
        }
        catch (Exception exception) {
            DebugLog.LOGE("Saving model failed");
            return;
        }
    }

    private class ModelLoadTask extends AsyncTask<String, Integer, Boolean>
    {
        protected Boolean doInBackground(final String... array) {
            PersitenceModelFragment.this._persistenceCallback.doLoad(array[0]);
            return true;
        }

        protected void onPostExecute(final Boolean b) {
            final StringBuilder append = new StringBuilder().append("ModelLoadTask::onPostExecute: execution ");
            String s;
            if (b) {
                s = "successful";
            }
            else {
                s = "failed";
            }
            DebugLog.LOGD(append.append(s).toString());
            PersitenceModelFragment.this.onLoadDone();
        }
    }

    private class ModelSaveTask extends AsyncTask<String, Integer, Boolean>
    {
        protected Boolean doInBackground(final String... array) {
            PersitenceModelFragment.this._persistenceCallback.doSave(array[0]);
            return true;
        }

        protected void onPostExecute(final Boolean b) {
            final StringBuilder append = new StringBuilder().append("ModelSaveTask::onPostExecute: execution ");
            String s;
            if (b) {
                s = "successful";
            }
            else {
                s = "failed";
            }
            DebugLog.LOGD(append.append(s).toString());
            PersitenceModelFragment.this.onSaveDone();
        }
    }

    public interface PersistenceCallback {
        boolean doLoad(String var1);

        boolean doSave(String var1);

        void onLoadDone();

        void onSaveDone();
    }

}

