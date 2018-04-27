/*
 * Decompiled with CFR 0_124.
 *
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.ActionBar
 *  android.app.Activity
 *  android.app.AlertDialog
 *  android.app.AlertDialog$Builder
 *  android.app.Fragment
 *  android.app.FragmentManager
 *  android.app.FragmentTransaction
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.content.Intent
 *  android.content.res.Configuration
 *  android.content.res.Resources
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$Config
 *  android.graphics.BitmapFactory
 *  android.graphics.Canvas
 *  android.graphics.Paint
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Environment
 *  android.os.Handler
 *  android.os.IBinder
 *  android.os.StatFs
 *  android.text.Editable
 *  android.view.KeyEvent
 *  android.view.LayoutInflater
 *  android.view.Menu
 *  android.view.MenuInflater
 *  android.view.MenuItem
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.view.Window
 *  android.view.inputmethod.InputMethodManager
 *  android.widget.Button
 *  android.widget.EditText
 *  android.widget.ImageView
 *  android.widget.LinearLayout
 *  android.widget.TextView
 *  android.widget.TextView$OnEditorActionListener
 *  android.widget.Toast
 */
package com.vuforia.captureapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StatFs;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.vuforia.captureapp.CaptureListActivity;
import com.vuforia.captureapp.Util;
import com.vuforia.captureapp.captureactivity.CaptureActivity;
import com.vuforia.captureapp.model.CaptureInformation;
import com.vuforia.captureapp.model.CaptureModelFragment;
import java.io.File;
import java.io.Serializable;
import java.util.List;

public class CaptureDetailActivity
extends Activity
implements CaptureModelFragment.ShareCallback {
    private static final String TAG_FRAGMENT = "tag_fragment_capture_model_detail";
    private CaptureInformation _captureInformation;
    private CaptureModelFragment _fragment;

    private void endActivityForRefresh() {
        this.setResult(0, new Intent());
        this.finish();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void renameTo(final String s) {
        final File file = new File("/sdcard/VuforiaObjectScanner/" + "ObjectReco/", this._captureInformation.getItemName() + ".od");
        final File file2 = new File("/sdcard/VuforiaObjectScanner/" + "ObjectReco/", s + ".od");
        final File file3 = new File("/sdcard/VuforiaObjectScanner/" + "metadata/", this._captureInformation.getItemName());
        final File file4 = new File("/sdcard/VuforiaObjectScanner/" + "metadata/", s);
        if (!file2.exists() && !file4.exists()) {
            file.renameTo(file2);
            file3.renameTo(file4);
            this._captureInformation.updateName(s, file4.getAbsolutePath() + "/capture.jpg");
            this.setTitle(this._captureInformation.getItemName());
            ((TextView)this.findViewById(R.id.capture_name)).setText(this._captureInformation.getItemName());
            final Bundle extras = this.getIntent().getExtras();
            if (extras != null) {
                extras.putSerializable("item", this._captureInformation);
            }
        }
        else {
            Toast.makeText(this, "File or folder already exists", Toast.LENGTH_LONG).show();
        }
    }

    private void setDomeVisualization(int[] arrn) {
        ImageView imageView = this.findViewById(R.id.item_capture_coverage);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.coverage_empty);
        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        bitmap = null;
        Canvas canvas = new Canvas(bitmap2);
        for (int i = 0; i < arrn.length; ++i) {
            if (arrn[i] <= 0) continue;
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ring_d_16 - i);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        imageView.setImageBitmap(bitmap2);
        imageView.invalidate();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @SuppressLint(value={"NewApi"})
    public long freeDiskSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (Build.VERSION.SDK_INT < 18) return (long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize();
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final View inflate = ((LayoutInflater)this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_capture_detail, null);
        final ViewGroup viewGroup = this.getWindow().getDecorView().findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(inflate);
        ((ImageView)this.findViewById(R.id.item_capture_image)).setImageBitmap(BitmapFactory.decodeFile(this._captureInformation.getImagePath()));
        ((TextView)this.findViewById(R.id.capture_name)).setText(this._captureInformation.getItemName());
        ((TextView)this.findViewById(R.id.capture_points)).setText("" + this._captureInformation.getNbPoints());
        ((TextView)this.findViewById(R.id.capture_file_size)).setText("" + this._captureInformation.getFileSize() + " MB");
        ((TextView)this.findViewById(R.id.last_modified)).setText(this._captureInformation.getFormatedLastModified());
    }

    protected void onCreate(Bundle extras) {
        super.onCreate(extras);
        if (extras != null) {
            this._captureInformation = (CaptureInformation)extras.getSerializable("captureInformation");
        }
        this.getWindow().setFlags(1024, 1024);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(32);
        this.setContentView(R.layout.activity_capture_detail);
        final FragmentManager fragmentManager = this.getFragmentManager();
        this._fragment = (CaptureModelFragment)fragmentManager.findFragmentByTag("tag_fragment_capture_model_detail");
        if (this._fragment == null) {
            this._fragment = new CaptureModelFragment();
            fragmentManager.beginTransaction().add(this._fragment, "tag_fragment_capture_model_detail").commit();
        }
        extras = this.getIntent().getExtras();
        if (extras != null && extras != null && this._captureInformation == null) {
            this._captureInformation = (CaptureInformation)extras.getSerializable("item");
        }
        this.setTitle(this._captureInformation.getItemName());
        ((ImageView)this.findViewById(R.id.item_capture_image)).setImageBitmap(BitmapFactory.decodeFile(this._captureInformation.getImagePath()));
        ((TextView)this.findViewById(R.id.capture_name)).setText(this._captureInformation.getItemName());
        ((TextView)this.findViewById(R.id.capture_points)).setText("" + this._captureInformation.getNbPoints());
        ((TextView)this.findViewById(R.id.capture_file_size)).setText("" + String.format("%.1f", this._captureInformation.getFileSize()) + " MB");
        ((TextView)this.findViewById(R.id.last_modified)).setText(this._captureInformation.getFormatedLastModified());
        this.setDomeVisualization(this._captureInformation.getFacets());
        final LinearLayout linearLayout = this.findViewById(R.id.bottom);
        linearLayout.findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                final Intent intent = new Intent(CaptureDetailActivity.this, (Class)CaptureActivity.class);
                intent.putExtra("action", 200);
                intent.putExtra("captureName", CaptureDetailActivity.this._captureInformation.getItemName());
                intent.putExtra("captureNbPoints", CaptureDetailActivity.this._captureInformation.getNbPoints());
                CaptureDetailActivity.this.startActivityForResult(intent, 0);
            }
        });
        linearLayout.findViewById(R.id.cont_scan_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                if (CaptureDetailActivity.this.freeDiskSpace() / 1048576L >= CaptureListActivity.MIN_MB_TO_ENABLE_SCAN) {
                    final Intent intent = new Intent(CaptureDetailActivity.this, (Class)CaptureActivity.class);
                    intent.putExtra("action", 300);
                    intent.putExtra("captureName", CaptureDetailActivity.this._captureInformation.getItemName());
                    intent.putExtra("captureNbPoints", CaptureDetailActivity.this._captureInformation.getNbPoints());
                    CaptureDetailActivity.this.startActivityForResult(intent, 0);
                }
                else {
                    ((TextView)new AlertDialog.Builder(CaptureDetailActivity.this).setMessage("\nNot enough storage space to scan\nClear space and try again\n").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            dialogInterface.dismiss();
                        }
                    }).setTitle("Insufficient Memory").show().findViewById(16908299)).setGravity(3);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu2) {
        this.getMenuInflater().inflate(R.menu.capture_detail, menu2);
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final String itemName = this._captureInformation.getItemName();
        boolean onOptionsItemSelected = false;
        switch (menuItem.getItemId()) {
            default: {
                onOptionsItemSelected = super.onOptionsItemSelected(menuItem);
                break;
            }
            case R.id.action_share: {
                this._fragment.share(itemName);
                onOptionsItemSelected = true;
                break;
            }
            case R.id.action_delete: {
                final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(this);
                alertDialog$Builder.setTitle("Delete");
                alertDialog$Builder.setMessage("\nAre you sure you want to delete?\n");
                alertDialog$Builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog$Builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        Util.deleteFileRecursive(Util.getCaptureMetadataDirectory(itemName));
                        Util.getOdFile(itemName).delete();
                        CaptureDetailActivity.this.endActivityForRefresh();
                    }
                });
                ((TextView)alertDialog$Builder.show().findViewById(16908299)).setGravity(3);
                onOptionsItemSelected = true;
                break;
            }
            case R.id.action_edit_name: {
                final AlertDialog.Builder alertDialog$Builder2 = new AlertDialog.Builder(this);
                final View inflate = LayoutInflater.from(this).inflate(R.layout.text_edit_layout, null, false);
                final EditText editText = inflate.findViewById(R.id.text_edit_view);
                final TextView textView = inflate.findViewById(R.id.text_edit_error_message);
                editText.setText(this._captureInformation.getItemName());
                editText.setInputType(524289);
                editText.setImeActionLabel("OK", 66);
                editText.setImeOptions(6);
                alertDialog$Builder2.setView(inflate);
                alertDialog$Builder2.setTitle("Name");
                alertDialog$Builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog$Builder2.setPositiveButton("Ok", null);
                final AlertDialog show = alertDialog$Builder2.show();
                show.getButton(-1).setOnClickListener(new View.OnClickListener() {
                    public void onClick(final View view) {
                        final String string = editText.getText().toString();
                        if (string.length() > 64) {
                            textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_max_length));
                        }
                        else if (string.length() == 0) {
                            textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_character));
                        }
                        else {
                            for (int i = 0; i < string.length(); ++i) {
                                final char char1 = string.charAt(i);
                                if ((char1 < 'a' || char1 > 'z') && (char1 < 'A' || char1 > 'Z') && (char1 < '0' || char1 > '9') && char1 != '_') {
                                    textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_character));
                                    return;
                                }
                            }
                            if (string == "" || (CaptureDetailActivity.this._fragment.getModelNames().contains(string) && string.compareTo(itemName) != 0)) {
                                textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_duplicate_name));
                            }
                            else {
                                if (string.compareTo(itemName) != 0) {
                                    CaptureDetailActivity.this.renameTo(string);
                                }
                                show.dismiss();
                            }
                        }
                    }
                });
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(final TextView textView, int i, final KeyEvent keyEvent) {
                        boolean b = false;
                        if (i == 6 || i == 0) {
                            boolean b2 = true;
                            ((InputMethodManager)CaptureDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            final String string = editText.getText().toString();
                            if (string.length() > 64) {
                                textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_max_length));
                                b2 = false;
                            }
                            char char1;
                            boolean b3;
                            for (i = 0; i < string.length(); ++i, b2 = b3) {
                                char1 = string.charAt(i);
                                if (char1 >= 'a') {
                                    b3 = b2;
                                    if (char1 <= 'z') {
                                        continue;
                                    }
                                }
                                if (char1 >= 'A') {
                                    b3 = b2;
                                    if (char1 <= 'Z') {
                                        continue;
                                    }
                                }
                                if (char1 >= '0') {
                                    b3 = b2;
                                    if (char1 <= '9') {
                                        continue;
                                    }
                                }
                                b3 = b2;
                                if (char1 != '_') {
                                    textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_character));
                                    b3 = false;
                                }
                            }
                            if (string == "" || string.length() == 0) {
                                textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_character));
                                b2 = false;
                            }
                            boolean b4 = b2;
                            if (CaptureDetailActivity.this._fragment.getModelNames().contains(string)) {
                                b4 = b2;
                                if (string.compareTo(itemName) != 0) {
                                    textView.setText(CaptureDetailActivity.this.getResources().getString(R.string.edit_text_error_duplicate_name));
                                    b4 = false;
                                }
                            }
                            if (string.compareTo(itemName) == 0) {
                                b4 = false;
                                show.dismiss();
                            }
                            b = b4;
                            if (b4) {
                                CaptureDetailActivity.this.renameTo(string);
                                show.dismiss();
                                b = b4;
                            }
                        }
                        return b;
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMethodManager)CaptureDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 1);
                    }
                }, 500L);
                onOptionsItemSelected = true;
                break;
            }
            case 16908332: {
                this.finish();
                onOptionsItemSelected = true;
                break;
            }
        }
        return onOptionsItemSelected;
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        String string2 = Util.getMarkerCatureName();
        super.onResume();
        if (string2 != null) {
            this._captureInformation = Util.getCaptureInformation(string2);
            if (this._captureInformation != null) {
                ((TextView)this.findViewById(R.id.capture_points)).setText("" + this._captureInformation.getNbPoints());
                ((TextView)this.findViewById(R.id.capture_file_size)).setText("" + String.format("%.1f", Float.valueOf(this._captureInformation.getFileSize())) + " MB");
                ((TextView)this.findViewById(R.id.last_modified)).setText(this._captureInformation.getFormatedLastModified());
                this.setDomeVisualization(this._captureInformation.getFacets());
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("captureInformation", this._captureInformation);
    }

    @Override
    public void onShareDone() {
    }

}

