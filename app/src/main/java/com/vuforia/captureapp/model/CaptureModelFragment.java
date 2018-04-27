/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.Fragment
 *  android.app.FragmentManager
 *  android.content.Intent
 *  android.content.res.Resources
 *  android.net.Uri
 *  android.os.Bundle
 *  android.os.Environment
 */
package com.vuforia.captureapp.model;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.vuforia.captureapp.R;
import com.vuforia.captureapp.model.AndroidFileSharing;
import com.vuforia.captureapp.model.CaptureInformation;
import com.vuforia.captureapp.model.CaptureListMode;
import com.vuforia.captureapp.model.WaitDialogFragment;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class CaptureModelFragment
extends Fragment
implements AndroidFileSharing.ZipCompletionCallback {
    private static final String TAG_FRAGMENT = "tag_fragment_wait_dialog";
    private List<CaptureInformation> _captures;
    private CaptureListMode _mode;
    private ShareCallback _shareCallback;

    public CaptureModelFragment() {
        this._captures = this.getCaptureList();
        this._mode = CaptureListMode.MODE_NORMAL;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<CaptureInformation> getCaptureList() {
        final ArrayList<CaptureInformation> list = new ArrayList<CaptureInformation>();
        final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/VuforiaObjectScanner/metadata/");
        if (file.exists() && file.isDirectory()) {
            final File[] listFiles = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    final boolean b = false;
                    boolean b2;
                    if (!file.isDirectory()) {
                        b2 = b;
                    }
                    else {
                        file = new File(file, "capture.jpg");
                        b2 = b;
                        if (file.exists()) {
                            b2 = b;
                            if (file.isFile()) {
                                b2 = true;
                            }
                        }
                    }
                    return b2;
                }
            });
            if (listFiles == null) {
                return list;
            }
            final int length = listFiles.length;
            int i = 0;
            Label_0136_Outer:
            while (i < length) {
                final File file2 = listFiles[i];
                final Properties properties = new Properties();
                final File file3 = new File(file2, "info.properties");
                while (true) {
                    if (file3.exists()) {
                        try {
                            properties.load(new FileInputStream(file3));
                            list.add(new CaptureInformation(new File(file2, "capture.jpg").getAbsolutePath(), file2.getName(), new Date(file3.lastModified()), properties));
                            ++i;
                            continue Label_0136_Outer;
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }
                    continue;
                }
            }
            Collections.sort(list, new Comparator<CaptureInformation>() {
                @Override
                public int compare(final CaptureInformation captureInformation, final CaptureInformation captureInformation2) {
                    final long n = captureInformation2.getLastModified() - captureInformation.getLastModified();
                    int n2;
                    if (n > 0L) {
                        n2 = 1;
                    }
                    else if (n < 0L) {
                        n2 = -1;
                    }
                    else {
                        n2 = 0;
                    }
                    return n2;
                }
            });
        }
        return list;
    }

    public CaptureInformation getCaptureInformationItem(int n) {
        return this._captures.get(n);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CaptureInformation getCaptureInformationItem(String object) {
        CaptureInformation captureInformation;
        Iterator<CaptureInformation> iterator = this._captures.iterator();
        do {
            if (!iterator.hasNext()) return this._captures.get(0);
        } while (!(captureInformation = iterator.next()).getItemName().equals(object));
        return captureInformation;
    }

    public int getCount() {
        return this._captures.size();
    }

    public CaptureListMode getMode() {
        return this._mode;
    }

    public List<String> getModelNames() {
        ArrayList<String> arrayList = new ArrayList<String>();
        Iterator<CaptureInformation> iterator = this._captures.iterator();
        while (iterator.hasNext()) {
            arrayList.add(iterator.next().getItemName());
        }
        return arrayList;
    }

    public List<String> getSelections() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (CaptureInformation captureInformation : this._captures) {
            if (!captureInformation.isSelected()) continue;
            arrayList.add(captureInformation.getItemName());
        }
        return arrayList;
    }

    public int nbSelections() {
        int n = 0;
        Iterator<CaptureInformation> iterator = this._captures.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isSelected()) continue;
            ++n;
        }
        return n;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this._shareCallback = (ShareCallback)activity;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setRetainInstance(true);
    }

    public void onDetach() {
        super.onDetach();
        this._shareCallback = null;
    }

    @Override
    public void onZipDone(ArrayList<Uri> arrayList) {
        this.resetSelection();
        ((WaitDialogFragment)this.getFragmentManager().findFragmentByTag("tag_fragment_wait_dialog")).dismiss();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        intent.setType("*/*");
        this.getActivity().startActivity(Intent.createChooser(intent, "Share via"));
        this._shareCallback.onShareDone();
    }

    public void refresh() {
        this._captures.clear();
        this._captures.addAll(this.getCaptureList());
    }

    public void resetSelection() {
        Iterator<CaptureInformation> iterator = this._captures.iterator();
        while (iterator.hasNext()) {
            iterator.next().selected(false);
        }
    }

    public void setMode(CaptureListMode captureListMode) {
        this._mode = captureListMode;
    }

    public void share() {
        this.share(null);
    }

    public void share(final String s) {
        final WaitDialogFragment waitDialogFragment = new WaitDialogFragment();
        waitDialogFragment.info(this.getResources().getString(R.string.wait_share_title), this.getResources().getString(R.string.wait_share_message));
        waitDialogFragment.show(this.getFragmentManager(), "tag_fragment_wait_dialog");
        final AndroidFileSharing androidFileSharing = new AndroidFileSharing();
        if (s == null) {
            final Iterator<String> iterator = this.getSelections().iterator();
            while (iterator.hasNext()) {
                androidFileSharing.addCaptureName(iterator.next());
            }
        }
        else {
            androidFileSharing.addCaptureName(s);
        }
        androidFileSharing.zip(this);
    }

    public interface ShareCallback {
        void onShareDone();
    }

}

