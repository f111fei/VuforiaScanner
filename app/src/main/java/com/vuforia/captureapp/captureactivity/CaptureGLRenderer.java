/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.opengl.GLSurfaceView
 *  android.opengl.GLSurfaceView$Renderer
 *  android.os.Handler
 *  android.os.Message
 *  javax.microedition.khronos.egl.EGLConfig
 *  javax.microedition.khronos.opengles.GL10
 */
package com.vuforia.captureapp.captureactivity;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import com.vuforia.Vuforia;
import com.vuforia.captureapp.captureactivity.CaptureActivity;
import com.vuforia.captureapp.captureactivity.DebugLog;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CaptureGLRenderer
implements GLSurfaceView.Renderer {
    public static Handler mainActivityHandler;
    public CaptureActivity mActivity;
    public boolean mIsActive = false;

    public void displayMessage(String string2) {
        Message message = new Message();
        message.obj = string2;
        mainActivityHandler.sendMessage(message);
    }

    public native void initRendering();

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void onDrawFrame(GL10 gL10) {
        if (!this.mIsActive) {
            return;
        }
        this.mActivity.updateRenderView();
        this.renderFrame();
    }

    public void onSurfaceChanged(GL10 gL10, int n, int n2) {
        DebugLog.LOGD("GLRenderer::onSurfaceChanged");
        this.updateRendering(n, n2);
        Vuforia.onSurfaceChanged(n, n2);
    }

    public void onSurfaceCreated(GL10 gL10, EGLConfig eGLConfig) {
        DebugLog.LOGD("GLRenderer::onSurfaceCreated");
        this.initRendering();
        Vuforia.onSurfaceCreated();
    }

    public native void renderFrame();

    public native void updateRendering(int var1, int var2);
}

