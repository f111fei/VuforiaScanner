/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.content.Intent
 *  android.os.Bundle
 *  android.os.Handler
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.view.Window
 *  android.widget.RelativeLayout
 */
package com.vuforia.captureapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import com.vuforia.captureapp.CaptureListActivity;

public class ActivitySplashScreen
extends Activity {
    private static long SPLASH_MILLIS = 450L;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);
        this.addContentView(LayoutInflater.from(this).inflate(R.layout.activity_splash_screen, null, false), new ViewGroup.LayoutParams(-1, -1));
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent intent = new Intent(ActivitySplashScreen.this, CaptureListActivity.class);
                ActivitySplashScreen.this.startActivity(intent);
            }
        }, SPLASH_MILLIS);
    }

}

