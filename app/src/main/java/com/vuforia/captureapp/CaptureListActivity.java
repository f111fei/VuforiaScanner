package com.vuforia.captureapp;

import android.annotation.*;
import android.os.*;
import java.io.*;
import android.content.*;
import android.app.*;
import com.vuforia.captureapp.captureactivity.*;
import java.util.*;
import com.vuforia.captureapp.model.*;
import android.graphics.*;
import android.widget.*;
import android.view.*;

public class CaptureListActivity extends Activity implements CaptureModelFragment.ShareCallback
{
    public static int MIN_MB_TO_ENABLE_SCAN = 0;
    private static final String TAG_FRAGMENT = "tag_fragment_capture_model";
    private Button _buttonOk;
    private LinearLayout _buttonsView;
    private CaptureModelFragment _fragment;
    private GridAdapter _gridAdapter;
    private GridView _gridView;

    static {
        CaptureListActivity.MIN_MB_TO_ENABLE_SCAN = 200;
    }

    private void enterMode(final CaptureListMode mode) {
        switch (mode) {
            case MODE_DELETE: {
                this.setTitle(R.string.title_delete);
                this._buttonOk.setText(R.string.title_delete);
                this._buttonsView.setVisibility(View.VISIBLE);
                break;
            }
            case MODE_SHARE: {
                this.setTitle(R.string.title_share);
                this._buttonOk.setText(R.string.title_share);
                this._buttonsView.setVisibility(View.VISIBLE);
                break;
            }
            case MODE_NORMAL: {
                this.setTitle(R.string.title_activity_capture_list);
                this._buttonOk.setText(R.string.title_ok);
                this._buttonsView.setVisibility(View.GONE);
                break;
            }
        }
        this._fragment.setMode(mode);
        this.invalidateOptionsMenu();
        if (this._gridAdapter.getCount() > 0) {
            this.findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
        }
        else {
            this.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({ "NewApi" })
    public long freeDiskSpace() {
        final StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long n;
        if (Build.VERSION.SDK_INT >= 18) {
            n = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        }
        else {
            n = statFs.getAvailableBlocks() * statFs.getBlockSize();
        }
        return n;
    }

    protected void onActivityResult(final int n, final int n2, final Intent intent) {
        super.onActivityResult(n, n2, intent);
        if (n == 0) {
            if (n2 == 102) {}
        }
        else if (n == 1) {
            this.enterMode(CaptureListMode.MODE_NORMAL);
            this._gridAdapter.refresh();
            this._gridView.invalidateViews();
        }
        if (this._gridAdapter.getCount() > 0) {
            this.findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
        }
        else {
            this.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
        }
    }

    public void onBackPressed() {
        if (this._fragment.getMode() != CaptureListMode.MODE_NORMAL) {
            this._fragment.resetSelection();
            this.enterMode(CaptureListMode.MODE_NORMAL);
            this._gridView.invalidateViews();
        }
        else {
            super.onBackPressed();
        }
    }

    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.setContentView(R.layout.activity_capture_list);
        final FragmentManager fragmentManager = this.getFragmentManager();
        this._fragment = (CaptureModelFragment)fragmentManager.findFragmentByTag("tag_fragment_capture_model");
        if (this._fragment == null) {
            this._fragment = new CaptureModelFragment();
            fragmentManager.beginTransaction().add(this._fragment, "tag_fragment_capture_model").commit();
        }
        this._gridView = this.findViewById(R.id.gridView_capture);
        this._buttonsView = this.findViewById(R.id.bottom);
        this._buttonOk = this._buttonsView.findViewById(R.id.BtnOk);
        final Button button = this._buttonsView.findViewById(R.id.BtnCancel);
        this._gridAdapter = new GridAdapter(this, this._fragment);
        this._gridView.setAdapter(this._gridAdapter);
        this._buttonsView.setVisibility(View.GONE);
        this._gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                switch (CaptureListActivity.this._fragment.getMode()) {
                    case MODE_SHARE: {
                        CaptureListActivity.this._fragment.resetSelection();
                        CaptureListActivity.this._gridAdapter.onItemClick(view, n);
                        CaptureListActivity.this._gridView.invalidateViews();
                        break;
                    }
                    case MODE_DELETE: {
                        CaptureListActivity.this._gridAdapter.onItemClick(view, n);
                        CaptureListActivity.this._gridView.invalidateViews();
                        break;
                    }
                    case MODE_NORMAL: {
                        final Intent intent = new Intent(CaptureListActivity.this, (Class)CaptureDetailActivity.class);
                        intent.putExtra("item", CaptureListActivity.this._fragment.getCaptureInformationItem(n));
                        CaptureListActivity.this.startActivityForResult(intent, 1);
                        break;
                    }
                }
            }
        });
        this._buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                final int nbSelections = CaptureListActivity.this._fragment.nbSelections();
                final AlertDialog.Builder alertDialog$Builder = new AlertDialog.Builder(CaptureListActivity.this);
                if (nbSelections > 0) {
                    final DialogInterface.OnClickListener dialogInterface$OnClickListener = new DialogInterface.OnClickListener() {
                        private void deleteCaptures(final List<String> list) {
                            for (final String s : list) {
                                Util.deleteFileRecursive(Util.getCaptureMetadataDirectory(s));
                                Util.getOdFile(s).delete();
                            }
                        }

                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            switch (n) {
                                case -1: {
                                    this.deleteCaptures(CaptureListActivity.this._fragment.getSelections());
                                    CaptureListActivity.this.enterMode(CaptureListMode.MODE_NORMAL);
                                    CaptureListActivity.this._gridView.invalidateViews();
                                    CaptureListActivity.this._gridAdapter.refresh();
                                    if (CaptureListActivity.this._gridAdapter.getCount() > 0) {
                                        CaptureListActivity.this.findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
                                        break;
                                    }
                                    CaptureListActivity.this.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        }
                    };
                    if (CaptureListActivity.this._fragment.getMode() == CaptureListMode.MODE_DELETE) {
                        final StringBuilder append = new StringBuilder().append("\nAre you sure you want to delete ").append(nbSelections).append(" scan");
                        String s;
                        if (nbSelections > 1) {
                            s = "s";
                        }
                        else {
                            s = "";
                        }
                        ((TextView)alertDialog$Builder.setMessage(append.append(s).append("?\n").toString()).setPositiveButton("Yes", dialogInterface$OnClickListener).setNegativeButton("No", dialogInterface$OnClickListener).setTitle("Delete").show().findViewById(16908299)).setGravity(3);
                    }
                    else {
                        CaptureListActivity.this._fragment.share();
                        CaptureListActivity.this.enterMode(CaptureListMode.MODE_NORMAL);
                    }
                }
                else {
                    String title;
                    if (CaptureListActivity.this._fragment.getMode() == CaptureListMode.MODE_SHARE) {
                        title = "Share";
                    }
                    else {
                        title = "Delete";
                    }
                    alertDialog$Builder.setMessage("\nSelect at least 1 scan to continue\n").setPositiveButton("OK", null).setTitle(title).show();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                CaptureListActivity.this._fragment.resetSelection();
                CaptureListActivity.this.enterMode(CaptureListMode.MODE_NORMAL);
                CaptureListActivity.this._gridView.invalidateViews();
            }
        });
        this._gridView.invalidateViews();
        this.enterMode(this._fragment.getMode());
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        if (this._gridAdapter.getCount() > 0) {
            this.getMenuInflater().inflate(R.menu.capture_list, menu);
        }
        else {
            this.getMenuInflater().inflate(R.menu.capture_list_empty, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        boolean onOptionsItemSelected = false;
        switch (menuItem.getItemId()) {
            default: {
                onOptionsItemSelected = super.onOptionsItemSelected(menuItem);
                break;
            }
            case R.id.action_new: {
                if (this.freeDiskSpace() / 1048576L >= CaptureListActivity.MIN_MB_TO_ENABLE_SCAN) {
                    final Intent intent = new Intent(this, (Class)CaptureActivity.class);
                    intent.putExtra("action", 100);
                    intent.putExtra("captureName", "");
                    intent.putExtra("captureNbPoints", 0);
                    intent.putStringArrayListExtra("captureList", (ArrayList)this._fragment.getModelNames());
                    this.startActivityForResult(intent, 0);
                }
                else {
                    ((TextView)new AlertDialog.Builder(this).setMessage("\nNot enough storage space to scan\nClear space and try again\n").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialogInterface, final int n) {
                            dialogInterface.dismiss();
                        }
                    }).setTitle("Insufficient Memory").show().findViewById(16908299)).setGravity(3);
                }
                onOptionsItemSelected = true;
                break;
            }
            case R.id.action_share: {
                this.enterMode(CaptureListMode.MODE_SHARE);
                onOptionsItemSelected = true;
                break;
            }
            case R.id.action_delete: {
                this.enterMode(CaptureListMode.MODE_DELETE);
                onOptionsItemSelected = true;
                break;
            }
            case R.id.action_about: {
                this.startActivity(new Intent(this, (Class)AboutScreenActivity.class));
                onOptionsItemSelected = true;
                break;
            }
        }
        return onOptionsItemSelected;
    }

    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        if (this._gridAdapter.getCount() > 0) {
            this.getMenuInflater().inflate(R.menu.capture_list, menu);
        }
        else {
            this.getMenuInflater().inflate(R.menu.capture_list_empty, menu);
        }
        boolean onPrepareOptionsMenu = false;
        switch (this._fragment.getMode()) {
            default: {
                onPrepareOptionsMenu = super.onPrepareOptionsMenu(menu);
                break;
            }
            case MODE_SHARE:
            case MODE_DELETE: {
                onPrepareOptionsMenu = false;
                break;
            }
        }
        return onPrepareOptionsMenu;
    }

    protected void onResume() {
        final String markerCatureName = Util.getMarkerCatureName();
        super.onResume();
        if (markerCatureName != null) {
            this._gridAdapter.refresh();
            final Intent intent = new Intent(this, (Class)CaptureDetailActivity.class);
            intent.putExtra("item", this._fragment.getCaptureInformationItem(markerCatureName));
            this.startActivityForResult(intent, 1);
        }
        else {
            this._gridAdapter.refresh();
            this._gridView.invalidateViews();
            if (this._gridAdapter.getCount() > 0) {
                this.findViewById(R.id.empty_text).setVisibility(View.INVISIBLE);
            }
            else {
                this.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
            }
        }
    }

    public void onShareDone() {
    }

    class GridAdapter extends BaseAdapter
    {
        private final Context _context;
        private CaptureModelFragment _fragment;

        public GridAdapter(final Context context, final CaptureModelFragment fragment) {
            this._context = context;
            this._fragment = fragment;
        }

        public int getCount() {
            return this._fragment.getCount();
        }

        public Object getItem(final int n) {
            return this._fragment.getCaptureInformationItem(n);
        }

        public long getItemId(final int n) {
            return 0L;
        }

        public View getView(final int n, View inflate, final ViewGroup viewGroup) {
            if (inflate == null) {
                inflate = ((Activity)this._context).getLayoutInflater().inflate(R.layout.capture_grid_item, viewGroup, false);
            }
            final CaptureInformation captureInformation = (CaptureInformation)this.getItem(n);
            final View viewById = inflate.findViewById(R.id.mainView);
            final View viewById2 = inflate.findViewById(R.id.greyOutView);
            final ImageView imageView = viewById2.findViewById(R.id.greyOutImageView);
            viewById.setVisibility(View.VISIBLE);
            final ImageView imageView2 = viewById.findViewById(R.id.item_capture_image);
            final TextView textView = viewById.findViewById(R.id.item_capture_name);
            final TextView textView2 = viewById.findViewById(R.id.item_capture_lastModified);
            imageView2.setImageBitmap(BitmapFactory.decodeFile(captureInformation.getImagePath()));
            textView.setText(captureInformation.getItemName());
            textView2.setText(captureInformation.getFormatedLastModified());
            inflate.invalidate();
            if (this._fragment.getMode() == CaptureListMode.MODE_NORMAL) {
                viewById2.setVisibility(View.INVISIBLE);
            }
            else {
                viewById2.setVisibility(View.VISIBLE);
                final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageView2.getWidth(), imageView2.getHeight());
                layoutParams.gravity = 17;
                viewById2.setLayoutParams(layoutParams);
                if (captureInformation.isSelected()) {
                    viewById2.setBackgroundColor(CaptureListActivity.this.getResources().getColor(R.color.background_item_selected));
                    imageView.setImageResource(R.drawable.selection_icon_selected);
                }
                else {
                    viewById2.setBackgroundColor(CaptureListActivity.this.getResources().getColor(R.color.background_item_not_selected));
                    imageView.setImageResource(R.drawable.selection_icon_not_selected);
                }
                viewById2.invalidate();
            }
            return inflate;
        }

        public void onItemClick(final View view, final int n) {
            this._fragment.getCaptureInformationItem(n).toggleSelected();
        }

        public void refresh() {
            this._fragment.refresh();
            this.notifyDataSetChanged();
        }
    }
}
