/*
 * Decompiled with CFR 0_124.
 *
 * Could not load the following classes:
 *  android.content.Context
 *  android.opengl.GLSurfaceView
 *  android.opengl.GLSurfaceView$EGLConfigChooser
 *  android.opengl.GLSurfaceView$EGLContextFactory
 *  android.view.SurfaceHolder
 *  javax.microedition.khronos.egl.EGL10
 *  javax.microedition.khronos.egl.EGLConfig
 *  javax.microedition.khronos.egl.EGLContext
 *  javax.microedition.khronos.egl.EGLDisplay
 */
package com.vuforia.captureapp.captureactivity;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;
import com.vuforia.captureapp.captureactivity.DebugLog;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class CaptureGLView
extends GLSurfaceView {
    private static boolean mUseOpenGLES2 = true;

    public CaptureGLView(Context context) {
        super(context);
    }

    private static void checkEglError(String string2, EGL10 eGL10) {
        int n;
        while ((n = eGL10.eglGetError()) != 12288) {
            DebugLog.LOGE(String.format("%s: EGL error: 0x%x", string2, n));
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    public void init(int n, boolean bl, int n2, int n3) {
        boolean bl2 = (n & 1) != 0;
        mUseOpenGLES2 = bl2;
        StringBuilder stringBuilder = new StringBuilder().append("Using OpenGL ES ");
        Object object = mUseOpenGLES2 ? "2.0" : "1.x";
        DebugLog.LOGI(stringBuilder.append((String)object).toString());
        stringBuilder = new StringBuilder().append("Using ");
        object = bl ? "translucent" : "opaque";
        DebugLog.LOGI(stringBuilder.append((String)object).append(" GLView, depth buffer size: ").append(n2).append(", stencil size: ").append(n3).toString());
        if (bl) {
            this.getHolder().setFormat(-3);
        }
        this.setEGLContextFactory(new ContextFactory());
        object = bl ? new ConfigChooser(8, 8, 8, 8, n2, n3) : new ConfigChooser(5, 6, 5, 0, n2, n3);
        this.setEGLConfigChooser((GLSurfaceView.EGLConfigChooser)object);
    }

    private static class ConfigChooser
    implements GLSurfaceView.EGLConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

        public ConfigChooser(int n, int n2, int n3, int n4, int n5, int n6) {
            this.mRedSize = n;
            this.mGreenSize = n2;
            this.mBlueSize = n3;
            this.mAlphaSize = n4;
            this.mDepthSize = n5;
            this.mStencilSize = n6;
        }

        private int findConfigAttrib(EGL10 eGL10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int n, int n2) {
            if (eGL10.eglGetConfigAttrib(eGLDisplay, eGLConfig, n, this.mValue)) {
                n2 = this.mValue[0];
            }
            return n2;
        }

        private EGLConfig getMatchingConfig(EGL10 eGL10, EGLDisplay eGLDisplay, int[] arrn) {
            int[] arrn2 = new int[1];
            eGL10.eglChooseConfig(eGLDisplay, arrn, null, 0, arrn2);
            int n = arrn2[0];
            if (n <= 0) {
                throw new IllegalArgumentException("No matching EGL configs");
            }
            EGLConfig[] arreGLConfig = new EGLConfig[n];
            eGL10.eglChooseConfig(eGLDisplay, arrn, arreGLConfig, n, arrn2);
            return this.chooseConfig(eGL10, eGLDisplay, arreGLConfig);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public EGLConfig chooseConfig(EGL10 eGL10, EGLDisplay eGLDisplay) {
            if (!mUseOpenGLES2) return this.getMatchingConfig(eGL10, eGLDisplay, new int[]{12324, 5, 12323, 6, 12322, 5, 12352, 1, 12344});
            return this.getMatchingConfig(eGL10, eGLDisplay, new int[]{12324, 4, 12323, 4, 12322, 4, 12352, 4, 12344});
        }

        /*
         * Enabled aggressive block sorting
         */
        public EGLConfig chooseConfig(EGL10 eGL10, EGLDisplay eGLDisplay, EGLConfig[] arreGLConfig) {
            int n = arreGLConfig.length;
            int n2 = 0;
            while (n2 < n) {
                EGLConfig eGLConfig = arreGLConfig[n2];
                int n3 = this.findConfigAttrib(eGL10, eGLDisplay, eGLConfig, 12325, 0);
                int n4 = this.findConfigAttrib(eGL10, eGLDisplay, eGLConfig, 12326, 0);
                if (n3 >= this.mDepthSize && n4 >= this.mStencilSize) {
                    n4 = this.findConfigAttrib(eGL10, eGLDisplay, eGLConfig, 12324, 0);
                    int n5 = this.findConfigAttrib(eGL10, eGLDisplay, eGLConfig, 12323, 0);
                    int n6 = this.findConfigAttrib(eGL10, eGLDisplay, eGLConfig, 12322, 0);
                    n3 = this.findConfigAttrib(eGL10, eGLDisplay, eGLConfig, 12321, 0);
                    if (n4 == this.mRedSize && n5 == this.mGreenSize && n6 == this.mBlueSize && n3 == this.mAlphaSize) {
                        return eGLConfig;
                    }
                }
                ++n2;
            }
            return null;
        }
    }

    private static class ContextFactory
    implements GLSurfaceView.EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION = 12440;

        private ContextFactory() {
        }

        /*
         * Enabled aggressive block sorting
         */
        public EGLContext createContext(EGL10 eGL10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            EGLContext eglContext;
            if (mUseOpenGLES2) {
                DebugLog.LOGI("Creating OpenGL ES 2.0 context");
                CaptureGLView.checkEglError("Before eglCreateContext", eGL10);
                int n = EGL_CONTEXT_CLIENT_VERSION;
                eglContext = eGL10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{n, 2, 12344});
            } else {
                DebugLog.LOGI("Creating OpenGL ES 1.x context");
                CaptureGLView.checkEglError("Before eglCreateContext", eGL10);
                int n = EGL_CONTEXT_CLIENT_VERSION;
                eglContext = eGL10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{n, 1, 12344});
            }
            CaptureGLView.checkEglError("After eglCreateContext", eGL10);
            return eglContext;
        }

        public void destroyContext(EGL10 eGL10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            eGL10.eglDestroyContext(eGLDisplay, eGLContext);
        }
    }

}

