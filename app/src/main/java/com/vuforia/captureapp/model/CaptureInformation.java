/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 */
package com.vuforia.captureapp.model;

import android.annotation.SuppressLint;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class CaptureInformation
implements Serializable {
    private static final long serialVersionUID = 4729003554512915982L;
    private final SimpleDateFormat _dateFormat;
    private int[] _facets = new int[49];
    private float _fileSize;
    private String _imagePath;
    private boolean _isSelected;
    private String _itemName;
    private Date _lastModified;
    private int _nbPoints;

    @SuppressLint(value={"SimpleDateFormat"})
    public CaptureInformation(String string2, String string3, Date date, Properties properties) {
        this._imagePath = string2;
        this._itemName = string3;
        this._lastModified = date;
        this._dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm");
        this._isSelected = false;
        this._nbPoints = Integer.parseInt(properties.getProperty("nbPoints", "124"));
        this._fileSize = Float.parseFloat(properties.getProperty("mFileSize", "0"));
        for (int i = 0; i < this._facets.length; ++i) {
            this._facets[i] = Integer.parseInt(properties.getProperty("facet" + i, "0"));
        }
    }

    public int[] getFacets() {
        return this._facets;
    }

    public float getFileSize() {
        return this._fileSize;
    }

    public String getFormatedLastModified() {
        return this._dateFormat.format(this._lastModified);
    }

    public String getImagePath() {
        return this._imagePath;
    }

    public String getItemName() {
        return this._itemName;
    }

    public long getLastModified() {
        return this._lastModified.getTime();
    }

    public int getNbPoints() {
        return this._nbPoints;
    }

    public boolean isSelected() {
        return this._isSelected;
    }

    public void selected(boolean bl) {
        this._isSelected = false;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void toggleSelected() {
        boolean bl = !this._isSelected;
        this._isSelected = bl;
    }

    public void update(Date date, int n, float f, int[] arrn) {
        this._lastModified = date;
        if (n > 0) {
            this._nbPoints = n;
        }
        if (f > 0.0f) {
            this._fileSize = f;
        }
        int n2 = 0;
        for (int i = 0; i < arrn.length; ++i) {
            n = n2;
            if (arrn[i] > 0) {
                n = n2 + 1;
            }
            n2 = n;
        }
        if (n2 > 0) {
            for (n = 0; n < arrn.length; ++n) {
                this._facets[n] = arrn[n];
            }
        }
    }

    public void updateName(String string2, String string3) {
        this._itemName = string2;
        this._imagePath = string3;
    }
}

