/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.vuforia.captureapp.captureactivity;

import android.util.Log;

public class DebugLog {
    private static final String LOGTAG = "QCAR";

    public static final void LOGD(String string2) {
        Log.d((String)"QCAR", (String)string2);
    }

    public static final void LOGE(String string2) {
        Log.e((String)"QCAR", (String)string2);
    }

    public static final void LOGI(String string2) {
        Log.i((String)"QCAR", (String)string2);
    }

    public static final void LOGW(String string2) {
        Log.w((String)"QCAR", (String)string2);
    }
}

