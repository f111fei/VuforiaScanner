/*
 * Decompiled with CFR 0_124.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.util.AttributeSet
 *  android.widget.ImageView
 */
package com.vuforia.captureapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageGridView
extends ImageView {
    public SquareImageGridView(Context context) {
        super(context);
    }

    public SquareImageGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquareImageGridView(Context context, AttributeSet attributeSet, int n) {
        super(context, attributeSet, n);
    }

    protected void onMeasure(int n, int n2) {
        super.onMeasure(n, n2);
        this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredWidth());
    }
}

