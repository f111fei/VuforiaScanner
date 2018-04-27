package com.vuforia.captureapp.captureactivity;

import com.vuforia.captureapp.model.*;
import java.util.*;
import com.vuforia.captureapp.*;
import android.content.pm.*;
import com.vuforia.*;

import android.graphics.Matrix;
import android.opengl.*;
import android.widget.*;
import android.view.inputmethod.*;
import android.content.*;
import android.annotation.*;
import android.util.*;
import android.content.res.*;
import android.app.*;
import java.io.*;
import android.graphics.*;
import java.nio.*;
import android.view.*;
import java.lang.ref.*;
import android.os.*;

public class CaptureActivity extends Activity implements PersitenceModelFragment.PersistenceCallback
{
    private static final int APPSTATUS_CAMERA_RUNNING = 7;
    private static final int APPSTATUS_CAMERA_STOPPED = 6;
    private static final int APPSTATUS_INITED = 5;
    private static final int APPSTATUS_INIT_APP = 0;
    private static final int APPSTATUS_INIT_APP_AR = 3;
    private static final int APPSTATUS_INIT_QCAR = 1;
    private static final int APPSTATUS_INIT_TRACKER = 2;
    private static final int APPSTATUS_LOAD_TRACKER = 4;
    private static final int APPSTATUS_UNINITED = -1;
    private static final int CAPTURE_IMAGE_TARGETS = 100;
    private static final int CAPTURE_NB_IMAGE_POINTS = 110;
    private static final int CAPTURE_NO_IMAGE_TARGETS = 101;
    private static final int CAPTURE_SAVED = 102;
    private static final int FOCUS_MODE_CONTINUOUS_AUTO = 1;
    private static final int FOCUS_MODE_INFINITY = 2;
    private static final int FOCUS_MODE_NORMAL = 0;
    static final int HIDE_LOADING_DIALOG = 0;
    private static final int INVALID_SCREEN_ROTATION = -1;
    private static final String NATIVE_LIB_SAMPLE = "CaptureApp";
    private static final String NATIVE_LIB_VUFORIA = "Vuforia";
    public static final int RESULT_NO_CAPTURE = 101;
    public static final int RESULT_WITH_CAPTURE = 102;
    static final int SHOW_LOADING_DIALOG = 1;
    private static final String TAG_FRAGMENT = "tag_fragment_save_model";
    private static final String sVuforiaKey = "AXVOQk7/////AAAAAcGhB4JJd0nlh/B8dBcXPkBsKxZLhiU8LF0WSWZakGP6TC5Cdxg7UidNeEd4INThOFfFOcEv45sDbOazRPt6FQEbclNYYf4sgTqw0Yd9oAfdz1s/qW3SH9fbzj5CkRGsUSQXS+/amTCN9iXw1Eppa1/YrgBFTHHYvTz9QbnRv8ivQMSX8tdIkb0vXX3KrEGA4vnwPSfKBI6Eygr7Xz0FZasntcreYqxi2xrtDuu7LObdAdfEHsO7L4jKrrfZLvuzl2+O4L6mUMqnfQyNJUmUTJueS017e+ZNCvPvMaWy7HqtPHSOYzRAVxcIBI7aHsdff3aWEblYTGrZgYkSbWNAuJXgEPUfHkATBX8JU+CCW5sU";
    private Bitmap _firstFrameBitmap;
    private PersitenceModelFragment _fragment;
    private Handler loadingDialogHandler;
    private LinearLayout mAlignmentOverlay;
    private int mAppStatus;
    private BuilderNewSequenceTask mBuilderNewSequenceTask;
    private ImageView mButtonClose;
    private ImageView mButtonSave;
    private ImageView mButtonScanDisabled;
    private ImageView mButtonScanPaused;
    private ImageView mButtonScanning;
    List<String> mCaptureNameList;
    private ImageView mCoverageImage;
    private LinearLayout mCoverageLayout;
    private String mCurrentCaptureName;
    private int mCurrentJniState;
    private int mCurrentMode;
    private String mCurrentRecordingName;
    MenuItem mDataSetMenuItem;
    private int[] mDomeFacets;
    private float mFileSize;
    private GestureDetector mGestureDetector;
    private CaptureGLView mGlView;
    private InitQCARTask mInitQCARTask;
    boolean mIsLegoDataSetActive;
    private boolean mIsRecording;
    private int mLastScreenRotation;
    private LoadTrackerTask mLoadTrackerTask;
    private View mLoadingDialogContainer;
    private TextView mMemoryConsumption;
    private int mNbPoints;
    private int mNbRecordingClips;
    private TextView mPointsCount;
    private int mQCARFlags;
    private CaptureGLRenderer mRenderer;
    private int mScreenHeight;
    private int mScreenWidth;
    private boolean mShowDome;
    private Object mShutdownLock;
    private Vector<Texture> mTextures;
    private RelativeLayout mUILayout;

    static {
        loadLibrary("Vuforia");
        loadLibrary("CaptureApp");
    }

    public CaptureActivity() {
        this.mScreenWidth = 0;
        this.mScreenHeight = 0;
        this.mLastScreenRotation = -1;
        this.mAppStatus = -1;
        this.mShutdownLock = new Object();
        this.mQCARFlags = 0;
        this.mDataSetMenuItem = null;
        this.mIsLegoDataSetActive = false;
        this.mIsRecording = false;
        this.mShowDome = true;
        this.mNbRecordingClips = 0;
        this.mNbPoints = 0;
        this.mFileSize = 0.0f;
        this.mDomeFacets = new int[49];
        this.mCurrentRecordingName = null;
        this.mCurrentJniState = -1;
        this.mCurrentMode = 100;
        this.mCurrentCaptureName = "";
        this.loadingDialogHandler = new LoadingDialogHandler(this);
    }

    private native boolean activateFlash(final boolean p0);

    private native boolean autofocus();

    private native boolean builderCloseSequenceUI();

    private native boolean builderNewSequenceUI();

    private native boolean builderOpenSequenceUI(final String p0);

    private native boolean builderSaveSequenceUI(final String p0);

    private native boolean builderStartClipUI();

    private native boolean builderStopClipUI();

    private native void cycleAugmentationMode(final boolean p0);

    private native void deinitApplicationNative();

    private void endActivity(String string2) {
        Intent intent = new Intent();
        if (string2 != null && string2.length() > 0 || this.mCurrentMode == 300) {
            File file = new File("/sdcard/VuforiaObjectScanner/ObjectReco/" + this.mCurrentRecordingName + ".od");
            try {
                Thread.sleep(300L);
            }
            catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Log.d((String)"tag_fragment_save_model", (String)("file" + this.mCurrentRecordingName + " ,Size = " + file.length()));
            this.mFileSize = (float)file.length() / 1048576.0f;
            intent.putExtra("captureName", string2);
            intent.putExtra("nbPoints", this.mNbPoints);
            intent.putExtra("mFileSize", this.mFileSize);
            intent.putExtra("mDomeFacets", this.mDomeFacets);
            if (string2 != null) {
                Util.prepareMetadataDirectory(string2, this.mNbPoints, this.mFileSize, this.mDomeFacets);
            }
            this.setResult(102, intent);
        } else {
            this. setResult(101, intent);
        }
        this.finish();
        System.exit(0);
    }

    private String getInitializationErrorString(final int n) {
        String s;
        if (n == -2) {
            s = this.getString(R.string.INIT_ERROR_DEVICE_NOT_SUPPORTED);
        }
        else if (n == -3) {
            s = this.getString(R.string.INIT_ERROR_NO_CAMERA_ACCESS);
        }
        else if (n == -4) {
            s = this.getString(R.string.INIT_LICENSE_ERROR_MISSING_KEY);
        }
        else if (n == -5) {
            s = this.getString(R.string.INIT_LICENSE_ERROR_INVALID_KEY);
        }
        else if (n == -7) {
            s = this.getString(R.string.INIT_LICENSE_ERROR_NO_NETWORK_TRANSIENT);
        }
        else if (n == -6) {
            s = this.getString(R.string.INIT_LICENSE_ERROR_NO_NETWORK_PERMANENT);
        }
        else if (n == -8) {
            s = this.getString(R.string.INIT_LICENSE_ERROR_CANCELED_KEY);
        }
        else {
            s = this.getString(R.string.INIT_LICENSE_ERROR_UNKNOWN_ERROR);
        }
        return s;
    }

    private int getInitializationFlags() {
        return 1;
    }

    private float getMemoryConsumption() {
        final ActivityManager.MemoryInfo activityManager$MemoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(activityManager$MemoryInfo);
        return activityManager$MemoryInfo.availMem / (activityManager$MemoryInfo.totalMem * 1.0f) * 100.0f;
    }

    private void initApplication() {
//        int n;
//        int n2 = n = 0;
//        if (4 == 0) {
//            try {
//                n2 = ActivityInfo.class.getField("SCREEN_ORIENTATION_FULL_SENSOR").getInt(null);
//            }
//            catch (Exception exception) {
//                exception.printStackTrace();
//                n2 = n;
//            }
//        }
//        this.setRequestedOrientation(n2);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.updateActivityOrientation();
        this.storeScreenDimensions();
        this.getWindow().setFlags(128, 128);
    }

    private void initApplicationAR() {
        this.initApplicationNative(this.mScreenWidth, this.mScreenHeight);
        (this.mGlView = new CaptureGLView(this)).init(this.mQCARFlags, Vuforia.requiresAlpha(), 16, 0);
        this.mRenderer = new CaptureGLRenderer();
        this.mRenderer.mActivity = this;
        this.mGlView.setRenderer(this.mRenderer);
        this.mGlView.setPreserveEGLContextOnPause(true);
    }

    private native void initApplicationNative(final int p0, final int p1);

    public static boolean loadLibrary(String string2) {
        try {
            System.loadLibrary(string2);
            StringBuilder stringBuilder = new StringBuilder();
            DebugLog.LOGI(stringBuilder.append("Native library lib").append(string2).append(".so loaded").toString());
            return true;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            DebugLog.LOGE("The library lib" + string2 + ".so could not be loaded");
            return false;
        }
        catch (SecurityException securityException) {
            DebugLog.LOGE("The library lib" + string2 + ".so was not allowed to be loaded");
            return false;
        }
    }

    private void loadTextures() {
        this.mTextures.add(Texture.loadTextureFromApk("TestTexture.png", this.getAssets()));
    }

    private void prepareRecordingUIState() {
        this.mAlignmentOverlay = this.mUILayout.findViewById(R.id.alignment_overlay);
        this.mButtonClose = this.mUILayout.findViewById(R.id.button_close);
        this.mButtonScanning = this.mUILayout.findViewById(R.id.button_scanning);
        this.mButtonScanPaused = this.mUILayout.findViewById(R.id.button_scan_ready_to_record);
        this.mButtonScanDisabled = this.mUILayout.findViewById(R.id.button_scan_disabled);
        this.mButtonSave = this.mUILayout.findViewById(R.id.button_save);
        this.mCoverageLayout = this.mUILayout.findViewById(R.id.coverage_layout);
        this.mCoverageImage = this.mUILayout.findViewById(R.id.coverage_image);
        this.mPointsCount = this.mUILayout.findViewById(R.id.points_count);
        this.mMemoryConsumption = this.mUILayout.findViewById(R.id.memory_consumption);
        this.mIsRecording = false;
        this.mNbRecordingClips = 0;
        this.mCoverageLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                CaptureActivity.this.mShowDome = !CaptureActivity.this.mShowDome;
                CaptureActivity.this.cycleAugmentationMode(CaptureActivity.this.mShowDome);
                if (CaptureActivity.this.mShowDome) {
                    CaptureActivity.this.mCoverageImage.setImageResource(R.drawable.checkbox_checked);
                }
                else {
                    CaptureActivity.this.mCoverageImage.setImageResource(R.drawable.checkbox_unchecked);
                }
            }
        });
        this.mButtonClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                if (CaptureActivity.this.mNbRecordingClips == 0) {
                    CaptureActivity.this.builderCloseSequenceUI();
                    CaptureActivity.this.endActivity(null);
                }
                else {
                    CaptureActivity.this.builderStopClipUI();
                    CaptureActivity.this.mIsRecording = false;
                    final DialogInterface.OnClickListener dialogInterface$OnClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            switch (n) {
                                case -1: {
                                    CaptureActivity.this.builderCloseSequenceUI();
                                    CaptureActivity.this.endActivity(null);
                                    break;
                                }
                            }
                        }
                    };
                    ((TextView)new AlertDialog.Builder(CaptureActivity.this).setMessage("\nData from this scan will be lost. Continue?\n").setPositiveButton("Yes", dialogInterface$OnClickListener).setNegativeButton("No", dialogInterface$OnClickListener).setTitle("Discard scan").show().findViewById(16908299)).setGravity(3);
                }
            }
        });
        this.mButtonScanPaused.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                CaptureActivity.this.builderStartClipUI();
                CaptureActivity.this.mButtonScanning.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanPaused.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mIsRecording = true;
                CaptureActivity.this.mNbRecordingClips++;
                CaptureActivity.this.setRecordingUIStateRecording();
                CaptureActivity.this.onRecordingStarted(true);
            }
        });
        this.mButtonScanning.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                CaptureActivity.this.builderStopClipUI();
                CaptureActivity.this.mIsRecording = false;
                CaptureActivity.this.setUIStateRecordingPaused();
            }
        });
        this.mButtonSave.setOnClickListener(new View.OnClickListener() {
            @TargetApi(17)
            public void onClick(final View view) {
                if (CaptureActivity.this.mCurrentMode == 100) {
                    CaptureActivity.this.mCurrentRecordingName = null;
                    final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(CaptureActivity.this);
                    final View inflate = LayoutInflater.from(CaptureActivity.this).inflate(R.layout.text_edit_layout, null, false);
                    final EditText editText = inflate.findViewById(R.id.text_edit_view);
                    final TextView textView = inflate.findViewById(R.id.text_edit_error_message);
                    editText.setInputType(524289);
                    editText.setImeActionLabel("OK", 66);
                    editText.setImeOptions(6);
                    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(final TextView textView, int i, final KeyEvent keyEvent) {
                            boolean b = false;
                            if (i == 6 || i == 66) {
                                boolean b2 = true;
                                ((InputMethodManager)CaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                final String string = editText.getText().toString();
                                if (string.length() > 64) {
                                    textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_max_length));
                                    b2 = false;
                                }
                                if (string == "" || string.length() == 0) {
                                    textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_character));
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
                                        textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_character));
                                        b3 = false;
                                    }
                                }
                                if (CaptureActivity.this.mCaptureNameList.contains(string)) {
                                    textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_duplicate_name));
                                    b2 = false;
                                }
                                b = b2;
                                if (b2) {
                                    CaptureActivity.this.mCurrentRecordingName = string;
                                    CaptureActivity.this._fragment.save(string);
                                    b = b2;
                                }
                            }
                            return b;
                        }
                    });
                    alertDialog$Builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(final DialogInterface dialogInterface) {
                            CaptureActivity.this.mUILayout.setVisibility(View.VISIBLE);
                            CaptureActivity.this.mUILayout.invalidate();
                            CaptureActivity.this.mUILayout.requestLayout();
                        }
                    });
                    alertDialog$Builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(final DialogInterface dialogInterface) {
                            CaptureActivity.this.mUILayout.setVisibility(View.VISIBLE);
                            CaptureActivity.this.mUILayout.invalidate();
                            CaptureActivity.this.mUILayout.requestLayout();
                        }
                    });
                    alertDialog$Builder.setView(inflate);
                    alertDialog$Builder.setTitle("Name");
                    alertDialog$Builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog$Builder.setPositiveButton("Ok", null);
                    final AlertDialog show = alertDialog$Builder.show();
                    show.getButton(-1).setOnClickListener(new View.OnClickListener() {
                        public void onClick(final View view) {
                            final String string = editText.getText().toString();
                            if (string.length() > 64) {
                                textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_max_length));
                            }
                            else if (string == "" || string.length() == 0) {
                                textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_character));
                            }
                            else {
                                for (int i = 0; i < string.length(); ++i) {
                                    final char char1 = string.charAt(i);
                                    if ((char1 < 'a' || char1 > 'z') && (char1 < 'A' || char1 > 'Z') && (char1 < '0' || char1 > '9') && char1 != '_') {
                                        textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_character));
                                        return;
                                    }
                                }
                                if (CaptureActivity.this.mCaptureNameList.contains(string)) {
                                    textView.setText(CaptureActivity.this.getResources().getString(R.string.edit_text_error_duplicate_name));
                                }
                                else {
                                    CaptureActivity.this.mCurrentRecordingName = string;
                                    CaptureActivity.this._fragment.save(string);
                                    show.dismiss();
                                }
                            }
                        }
                    });
                    ((InputMethodManager)CaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((InputMethodManager)CaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 1);
                        }
                    }, 500L);
                    CaptureActivity.this.mUILayout.setVisibility(View.INVISIBLE);
                }
                else if (CaptureActivity.this.mCurrentMode == 300) {
                    CaptureActivity.this.mCurrentRecordingName = CaptureActivity.this.mCurrentCaptureName;
                    CaptureActivity.this._fragment.save(CaptureActivity.this.mCurrentCaptureName);
                }
            }
        });
    }

    private native void setActivityPortraitMode(final boolean p0);

    private void setCaptureSavedState() {
        if (this.mNbRecordingClips == 0) {
            this.builderCloseSequenceUI();
        }
        else {
            this.builderStopClipUI();
            this.builderCloseSequenceUI();
        }
    }

    private native boolean setFocusMode(final int p0);

    private native void setProjectionMatrix();

    private void setRecordingUIReadyToRecord() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CaptureActivity.this.mButtonClose.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanning.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanPaused.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanDisabled.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonSave.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mAlignmentOverlay.setVisibility(View.INVISIBLE);
                if (CaptureActivity.this.mCurrentMode == 300) {
                    CaptureActivity.this.mPointsCount.setVisibility(View.VISIBLE);
                    CaptureActivity.this.mCoverageLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setRecordingUIStateInitial() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CaptureActivity.this.mButtonClose.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanning.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanPaused.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanDisabled.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonSave.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mPointsCount.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mCoverageLayout.setVisibility(View.INVISIBLE);
                if (CaptureActivity.this.mCurrentMode == 100) {
                    CaptureActivity.this.mAlignmentOverlay.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setRecordingUIStateRecording() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CaptureActivity.this.mPointsCount.setVisibility(View.VISIBLE);
                CaptureActivity.this.mCoverageLayout.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonClose.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanning.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanPaused.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanDisabled.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonSave.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mAlignmentOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setUIStateRecordingPaused() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CaptureActivity.this.mPointsCount.setVisibility(View.VISIBLE);
                CaptureActivity.this.mCoverageLayout.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonClose.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanning.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanPaused.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanDisabled.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mAlignmentOverlay.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonSave.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUIStateTest() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CaptureActivity.this.mButtonClose.setVisibility(View.VISIBLE);
                CaptureActivity.this.mButtonScanning.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanPaused.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonScanDisabled.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mButtonSave.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mPointsCount.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mCoverageLayout.setVisibility(View.INVISIBLE);
                CaptureActivity.this.mAlignmentOverlay.setVisibility(View.INVISIBLE);
            }
        });
    }

    private native void startCamera();

    private native void stopCamera();

    private void storeScreenDimensions() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mScreenHeight = displayMetrics.heightPixels;
    }

    private void updateActivityOrientation() {
        final Configuration configuration = this.getResources().getConfiguration();
        boolean activityPortraitMode = false;
        switch (configuration.orientation) {
            case 1: {
                activityPortraitMode = true;
                break;
            }
            case 2: {
                activityPortraitMode = false;
                break;
            }
        }
        final StringBuilder append = new StringBuilder().append("Activity is in ");
        String s;
        if (activityPortraitMode) {
            s = "PORTRAIT";
        }
        else {
            s = "LANDSCAPE";
        }
        DebugLog.LOGI(append.append(s).toString());
        this.setActivityPortraitMode(activityPortraitMode);
    }

    private void updateApplicationStatus(int n) {
        synchronized (this) {
            int n2 = this.mAppStatus;
            if (n2 != n) {
                this.mAppStatus = n;
                switch (this.mAppStatus) {
                    default: {
                        RuntimeException runtimeException = new RuntimeException("Invalid application state");
                        throw runtimeException;
                    }
                    case 0: {
                        this.mUILayout = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.camera_overlay, null, false);
                        this.mUILayout.setVisibility(View.VISIBLE);
                        this.mUILayout.setBackgroundColor(0xE9888DE9);
                        this.prepareRecordingUIState();
                        this.mLoadingDialogContainer = this.mUILayout.findViewById(R.id.loading_indicator);
                        this.loadingDialogHandler.sendEmptyMessage(1);
                        RelativeLayout relativeLayout = this.mUILayout;
                        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
                        this.addContentView(relativeLayout, layoutParams);
                        this.initApplication();
                        this.updateApplicationStatus(1);
                        break;
                    }
                    case 1: {
                        try {
                            InitQCARTask initQCARTask;
                            this.mInitQCARTask = initQCARTask = new InitQCARTask();
                            this.mInitQCARTask.execute();
                        }
                        catch (Exception exception) {
                            DebugLog.LOGE("Initializing QCAR SDK failed");
                        }
                        break;
                    }
                    case 2: {
                        if (this.initTracker() <= 0) break;
                        this.updateApplicationStatus(3);
                        break;
                    }
                    case 3: {
                        this.initApplicationAR();
                        this.updateApplicationStatus(4);
                        break;
                    }
                    case 4: {
                        try {
                            LoadTrackerTask loadTrackerTask;
                            this.mLoadTrackerTask = loadTrackerTask = new LoadTrackerTask();
                            this.mLoadTrackerTask.execute();
                        }
                        catch (Exception exception) {
                            DebugLog.LOGE("Loading tracking data set failed");
                        }
                        break;
                    }
                    case 5: {
                        System.gc();
                        this.setVisualizationMode(this.mCurrentMode);
                        this.onQCARInitializedNative();
                        this.mRenderer.mIsActive = true;
                        CaptureGLView captureGLView = this.mGlView;
                        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
                        this.addContentView(captureGLView, layoutParams);
                        this.mUILayout.bringToFront();
                        this.updateApplicationStatus(7);
                        break;
                    }
                    case 6: {
                        this.stopCamera();
                        break;
                    }
                    case 7: {
                        this.startCamera();
                        this.loadingDialogHandler.sendEmptyMessage(0);
                        this.mUILayout.setBackgroundColor(0);
                        if (!this.setFocusMode(1)) {
                            this.setFocusMode(0);
                        }
                        if (this.mCurrentMode == 100 && this.mNbRecordingClips == 0) {
                            BuilderNewSequenceTask builderNewSequenceTask = this.mBuilderNewSequenceTask;
                            if (builderNewSequenceTask == null) {
                                try {
                                    this.mBuilderNewSequenceTask = builderNewSequenceTask = new BuilderNewSequenceTask();
                                    this.mBuilderNewSequenceTask.execute();
                                }
                                catch (Exception exception) {
                                    DebugLog.LOGE("New Sequence Task failed");
                                }
                            }
                            this.setRecordingUIStateInitial();
                            break;
                        }
                        if (this.mCurrentMode == 300 || this.mCurrentMode == 200) {
                            this._fragment.load(this.mCurrentCaptureName);
                            break;
                        }
                        if (this.mNbRecordingClips != 0) break;
                        this.setRecordingUIStateInitial();
                    }
                }
            }
            return;
        }
    }

    public void changeOfCaptureState(final int mCurrentJniState, final int mNbPoints) {
        if (this.mCurrentJniState != mCurrentJniState || mNbPoints > 0) {
            switch (this.mCurrentJniState = mCurrentJniState) {
                case 100: {
                    this.setRecordingUIReadyToRecord();
                    break;
                }
                case 101: {
                    this.setRecordingUIStateInitial();
                    break;
                }
                case 102: {
                    this.setCaptureSavedState();
                    break;
                }
                case 110: {
                    if (this.mPointsCount != null) {
                        if (mNbPoints > 0) {
                            this.mNbPoints = mNbPoints;
                        }
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CaptureActivity.this.mPointsCount.setText(String.format("Points %d", mNbPoints));
                            }
                        });
                        break;
                    }
                    break;
                }
            }
        }
    }

    public native void deinitTracker();

    public native void destroyTrackerData();

    public boolean doLoad(final String s) {
        return this.builderOpenSequenceUI(s);
    }

    public boolean doSave(final String s) {
        return this.builderSaveSequenceUI(s);
    }

    public native int getOpenGlEsVersionNative();

    public Texture getTexture(final int n) {
        return this.mTextures.elementAt(n);
    }

    public int getTextureCount() {
        return this.mTextures.size();
    }

    public native int initTracker();

    public native int loadTrackerData();

    public native void nativeTouchEvent(final int p0, final int p1, final float p2, final float p3);

    public void onBackPressed() {
        if (this.mNbRecordingClips == 0) {
            super.onBackPressed();
            System.exit(0);
        }
        else {
            if (this.mIsRecording) {
                this.builderStopClipUI();
                this.mIsRecording = false;
                this.setUIStateRecordingPaused();
            }
            final DialogInterface.OnClickListener dialogInterface$OnClickListener = new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialogInterface, final int n) {
                    switch (n) {
                        case -1: {
                            CaptureActivity.this.endActivity(null);
                            break;
                        }
                    }
                }
            };
            ((TextView)new AlertDialog.Builder(this).setMessage("\nData from this scan will be lost. Continue?\n").setPositiveButton("Yes", dialogInterface$OnClickListener).setNegativeButton("No", dialogInterface$OnClickListener).setTitle("Discard scan").show().findViewById(16908299)).setGravity(3);
        }
    }

    public void onConfigurationChanged(final Configuration configuration) {
        DebugLog.LOGD("CaptureApp::onConfigurationChanged");
        super.onConfigurationChanged(configuration);
    }

    protected void onCreate(Bundle extras) {
        DebugLog.LOGD("CaptureApp::onCreate");
        super.onCreate(extras);
        this.mCurrentMode = 100;
        this.mCurrentCaptureName = null;
        extras = this.getIntent().getExtras();
        if (extras != null) {
            this.mCurrentMode = extras.getInt("action", 100);
            this.mCurrentCaptureName = extras.getString("captureName");
            this.mCaptureNameList = extras.getStringArrayList("captureList");
            this.mNbPoints = extras.getInt("captureNbPoints");
        }
        if (this.mCurrentMode == 300) {
            this.mCurrentRecordingName = this.mCurrentCaptureName;
        }
        final FragmentManager fragmentManager = this.getFragmentManager();
        this._fragment = (PersitenceModelFragment)fragmentManager.findFragmentByTag("tag_fragment_save_model");
        if (this._fragment == null) {
            this._fragment = new PersitenceModelFragment();
            fragmentManager.beginTransaction().add(this._fragment, "tag_fragment_save_model").commit();
        }
        this.mCurrentJniState = -1;
        this.mTextures = new Vector<Texture>();
        this.loadTextures();
        this.requestWindowFeature(1);
        this.getWindow().setFlags(1024, 1024);
        this.mQCARFlags = this.getInitializationFlags();
        this.mGestureDetector = new GestureDetector(this, (GestureDetector.OnGestureListener)new GestureListener());
        this.updateApplicationStatus(0);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mInitQCARTask != null && this.mInitQCARTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mInitQCARTask.cancel(true);
            this.mInitQCARTask = null;
        }
        if (this.mLoadTrackerTask != null && this.mLoadTrackerTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mLoadTrackerTask.cancel(true);
            this.mLoadTrackerTask = null;
        }
        if (this.mBuilderNewSequenceTask != null && this.mBuilderNewSequenceTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mBuilderNewSequenceTask.cancel(true);
            this.mBuilderNewSequenceTask = null;
        }
        synchronized (this.mShutdownLock) {
            this.deinitApplicationNative();
            this.mTextures.clear();
            this.mTextures = null;
            this.destroyTrackerData();
            this.deinitTracker();
            Vuforia.deinit();
            System.gc();
        }
    }

    public void onLoadDone() {
        if (this.mCurrentMode == 300) {
            if (this.mPointsCount != null && this.mNbPoints > 0) {
                this.runOnUiThread(new Runnable() {
                    final /* synthetic */ int val$pointsCount = CaptureActivity.this.mNbPoints;

                    @Override
                    public void run() {
                        CaptureActivity.this.mPointsCount.setText(String.format("Points %d", this.val$pointsCount));
                    }
                });
            }
            this.setRecordingUIReadyToRecord();
        }
        else if (this.mCurrentMode == 200) {
            this.setUIStateTest();
        }
    }

    protected void onPause() {
        DebugLog.LOGD("CaptureApp::onPause");
        super.onPause();
        if (this.mGlView != null) {
            this.mGlView.setVisibility(View.INVISIBLE);
            this.mGlView.onPause();
        }
        if (this.mAppStatus == 7) {
            this.updateApplicationStatus(6);
        }
        Vuforia.onPause();
    }

    public native int onQCARCameraRunning();

    public native void onQCARInitializedNative();

    public native void onRecordingStarted(final boolean p0);

    protected void onResume() {
        DebugLog.LOGD("CaptureApp::onResume");
        super.onResume();
        Vuforia.onResume();
        if (this.mAppStatus == 6) {
            this.updateApplicationStatus(7);
        }
        if (this.mGlView != null) {
            this.mGlView.setVisibility(View.VISIBLE);
            this.mGlView.onResume();
        }
        if (this.mIsRecording) {
            this.mIsRecording = false;
            this.setUIStateRecordingPaused();
        }
    }

    public void onSaveDone() {
        this.endActivity(this.mCurrentRecordingName);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int n = motionEvent.getActionMasked();
        int n2 = -1;
        int n3 = (65280 & n) >> 8;
        int n4 = n2;
        switch (n) {
            default: {
                n4 = n2;
                break;
            }
            case 0:
            case 5: {
                n4 = 0;
                break;
            }
            case 2: {
                n4 = 1;
                break;
            }
            case 1:
            case 6: {
                n4 = 2;
            }
            case 4: {
                break;
            }
            case 3: {
                n4 = 3;
            }
        }
        if (n3 != 0) return this.mGestureDetector.onTouchEvent(motionEvent);
        this.nativeTouchEvent(n4, motionEvent.getPointerId(n3), motionEvent.getX(n3), motionEvent.getY(n3));
        return this.mGestureDetector.onTouchEvent(motionEvent);
    }

    public void saveDomePanelsState(final int[] array) {
        for (int i = 0; i < this.mDomeFacets.length; ++i) {
            Log.d("tag_fragment_save_model", "" + array[i]);
            this.mDomeFacets[i] = array[i];
        }
    }

    public native void setVisualizationMode(final int p0);

    public void storeCameraImage(final byte[] array, final int n, final int n2) {
        try {
            final YuvImage yuvImage = new YuvImage(array, 17, n, n2, null);
            final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/VuforiaObjectScanner/capture.jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(array.length);
            yuvImage.compressToJpeg(new Rect(0, 0, n - 1, n2 - 1), 90, byteArrayOutputStream);
            final Bitmap decodeByteArray = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
            final Matrix matrix = new Matrix();
            if (this.getResources().getConfiguration().orientation == 1) {
                matrix.postRotate(90.0f);
            }
            Bitmap.createBitmap(decodeByteArray, 0, 0, decodeByteArray.getWidth(), decodeByteArray.getHeight(), matrix, true).compress(Bitmap.CompressFormat.JPEG, 70, (OutputStream)fileOutputStream);
            fileOutputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void storeCameraImageWithAugmentation(final byte[] array, final int n, final int n2) {
        try {
            final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(n2 * n * 4);
            allocateDirect.put(array);
            allocateDirect.rewind();
            final Bitmap bitmap = Bitmap.createBitmap(n, n2, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(allocateDirect);
            final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/VuforiaObjectScanner/capture.jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            final Matrix matrix = new Matrix();
            if (this.getResources().getConfiguration().orientation == 1) {
                matrix.postRotate(90.0f);
            }
            matrix.postScale(1.0f, -1.0f);
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true).compress(Bitmap.CompressFormat.JPEG, 70, (OutputStream)fileOutputStream);
            fileOutputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateRenderView() {
        final int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation != this.mLastScreenRotation && Vuforia.isInitialized() && this.mAppStatus == 7) {
            DebugLog.LOGD("CaptureApp::updateRenderView");
            this.storeScreenDimensions();
            this.mRenderer.updateRendering(this.mScreenWidth, this.mScreenHeight);
            this.setProjectionMatrix();
            this.mLastScreenRotation = rotation;
        }
    }

    private class BuilderNewSequenceTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(final Void... array) {
            synchronized (CaptureActivity.this.mShutdownLock) {
                // monitorexit(CaptureActivity.access$100(this.this$0))
                return CaptureActivity.this.onQCARCameraRunning() > 0;
            }
        }

        protected void onPostExecute(final Boolean b) {
            final StringBuilder append = new StringBuilder().append("BuilderNewSequenceTask::onPostExecute: execution ");
            String s;
            if (b) {
                s = "successful";
            }
            else {
                s = "failed";
            }
            DebugLog.LOGD(append.append(s).toString());
            if (b) {}
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        private final Handler autofocusHandler;

        private GestureListener() {
            this.autofocusHandler = new Handler();
        }

        public boolean onDown(final MotionEvent motionEvent) {
            return true;
        }

        public boolean onSingleTapUp(final MotionEvent motionEvent) {
            CaptureActivity.this.autofocus();
            this.autofocusHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 1000L);
            return true;
        }
    }

    private class InitQCARTask extends AsyncTask<Void, Integer, Boolean>
    {
        private int mProgressValue;

        private InitQCARTask() {
            this.mProgressValue = -1;
        }

        protected Boolean doInBackground(final Void... array) {
            boolean b = true;
            synchronized (CaptureActivity.this.mShutdownLock) {
                Vuforia.setInitParameters(CaptureActivity.this, CaptureActivity.this.mQCARFlags, "AXVOQk7/////AAAAAcGhB4JJd0nlh/B8dBcXPkBsKxZLhiU8LF0WSWZakGP6TC5Cdxg7UidNeEd4INThOFfFOcEv45sDbOazRPt6FQEbclNYYf4sgTqw0Yd9oAfdz1s/qW3SH9fbzj5CkRGsUSQXS+/amTCN9iXw1Eppa1/YrgBFTHHYvTz9QbnRv8ivQMSX8tdIkb0vXX3KrEGA4vnwPSfKBI6Eygr7Xz0FZasntcreYqxi2xrtDuu7LObdAdfEHsO7L4jKrrfZLvuzl2+O4L6mUMqnfQyNJUmUTJueS017e+ZNCvPvMaWy7HqtPHSOYzRAVxcIBI7aHsdff3aWEblYTGrZgYkSbWNAuJXgEPUfHkATBX8JU+CCW5sU");
                do {
                    this.mProgressValue = Vuforia.init();
                    this.publishProgress((Integer[]) new Integer[] { this.mProgressValue });
                } while (!this.isCancelled() && this.mProgressValue >= 0 && this.mProgressValue < 100);
                if (this.mProgressValue <= 0) {
                    b = false;
                }
                // monitorexit(CaptureActivity.access$100(this.this$0))
                return b;
            }
        }

        protected void onPostExecute(final Boolean b) {
            if (b) {
                DebugLog.LOGD("InitQCARTask::onPostExecute: QCAR initialization successful");
                CaptureActivity.this.updateApplicationStatus(2);
            }
            else {
                final AlertDialog create = new AlertDialog.Builder((Context)CaptureActivity.this).create();
                create.setButton(-1, "Close", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        System.exit(1);
                    }
                });
                final String access$400 = CaptureActivity.this.getInitializationErrorString(this.mProgressValue);
                DebugLog.LOGE("InitQCARTask::onPostExecute: " + access$400 + " Exiting.");
                create.setMessage(access$400);
                create.setCancelable(false);
                create.setCanceledOnTouchOutside(false);
                create.show();
            }
        }

        protected void onProgressUpdate(final Integer... array) {
        }
    }

    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(final Void... array) {
            synchronized (CaptureActivity.this.mShutdownLock) {
                // monitorexit(CaptureActivity.access$100(this.this$0))
                return CaptureActivity.this.loadTrackerData() > 0;
            }
        }

        protected void onPostExecute(final Boolean b) {
            final StringBuilder append = new StringBuilder().append("LoadTrackerTask::onPostExecute: execution ");
            String s;
            if (b) {
                s = "successful";
            }
            else {
                s = "failed";
            }
            DebugLog.LOGD(append.append(s).toString());
            if (b) {
                CaptureActivity.this.mIsLegoDataSetActive = true;
                CaptureActivity.this.updateApplicationStatus(5);
            }
            else {
                final AlertDialog create = new AlertDialog.Builder((Context)CaptureActivity.this).create();
                create.setButton(-1, "Close", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int n) {
                        System.exit(1);
                    }
                });
                create.setMessage("Failed to load tracker data.");
                create.show();
            }
        }
    }

    static class LoadingDialogHandler extends Handler
    {
        private final WeakReference<CaptureActivity> mCaptureApp;

        LoadingDialogHandler(final CaptureActivity captureActivity) {
            this.mCaptureApp = new WeakReference<CaptureActivity>(captureActivity);
        }

        public void handleMessage(final Message message) {
            final CaptureActivity captureActivity = this.mCaptureApp.get();
            if (captureActivity != null) {
                if (message.what == 1) {
                    captureActivity.mLoadingDialogContainer.setVisibility(View.VISIBLE);
                }
                else if (message.what == 0) {
                    captureActivity.mLoadingDialogContainer.setVisibility(View.GONE);
                }
            }
        }
    }
}
