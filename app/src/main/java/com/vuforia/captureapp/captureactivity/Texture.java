/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.content.res.AssetManager
 *  android.graphics.BitmapFactory
 */
package com.vuforia.captureapp.captureactivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.vuforia.captureapp.captureactivity.DebugLog;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Texture {
    public int mChannels;
    public byte[] mData;
    public int mHeight;
    public int mWidth;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Texture loadTextureFromApk(String o, final AssetManager assetManager) {
        try {
            final Bitmap decodeStream = BitmapFactory.decodeStream((InputStream)new BufferedInputStream(assetManager.open((String)o, 3)));
            final int[] array = new int[decodeStream.getWidth() * decodeStream.getHeight()];
            decodeStream.getPixels(array, 0, decodeStream.getWidth(), 0, 0, decodeStream.getWidth(), decodeStream.getHeight());
            final byte[] mData = new byte[decodeStream.getWidth() * decodeStream.getHeight() * 4];
            for (int i = 0; i < decodeStream.getWidth() * decodeStream.getHeight(); ++i) {
                final int n = array[i];
                mData[i * 4] = (byte)(n >>> 16);
                mData[i * 4 + 1] = (byte)(n >>> 8);
                mData[i * 4 + 2] = (byte)n;
                mData[i * 4 + 3] = (byte)(n >>> 24);
            }
            final Texture texture = new Texture();
            texture.mWidth = decodeStream.getWidth();
            texture.mHeight = decodeStream.getHeight();
            texture.mChannels = 4;
            texture.mData = mData;
            return texture;
        }
        catch (IOException ex) {
            DebugLog.LOGE("Failed to log texture '" + (String)o + "' from APK");
            DebugLog.LOGI(ex.getMessage());
            return null;
        }
    }

    public byte[] getData() {
        return this.mData;
    }
}

