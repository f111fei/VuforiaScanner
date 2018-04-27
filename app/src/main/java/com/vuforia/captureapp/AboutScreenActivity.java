package com.vuforia.captureapp;

import android.app.*;
import android.os.*;
import android.util.*;
import android.webkit.*;
import java.io.*;
import android.view.*;
import android.net.*;
import android.content.*;

public class AboutScreenActivity extends Activity
{
    private static final String LOGTAG = "AboutScreenActivity";

    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.setContentView(R.layout.activity_about_screen);
        final WebView webView = (WebView)this.findViewById(R.id.webView);
        String s;
        String string = s = "";
        try {
            final InputStream open = this.getAssets().open("about.html");
            s = string;
            s = string;
            s = string;
            final InputStreamReader inputStreamReader = new InputStreamReader(open);
            s = string;
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                s = string;
                final String line = bufferedReader.readLine();
                s = string;
                if (line == null) {
                    break;
                }
                s = string;
                s = string;
                final StringBuilder sb = new StringBuilder();
                s = string;
                string = sb.append(string).append(line).toString();
            }
        }
        catch (IOException ex) {
            Log.e("AboutScreenActivity", "About html loading failed");
        }
        webView.loadData(s, "text/html", "UTF-8");
        webView.setWebViewClient((WebViewClient)new AboutWebViewClient());
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        boolean onOptionsItemSelected = false;
        switch (menuItem.getItemId()) {
            default: {
                onOptionsItemSelected = super.onOptionsItemSelected(menuItem);
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

    private class AboutWebViewClient extends WebViewClient
    {
        public boolean shouldOverrideUrlLoading(final WebView webView, final String s) {
            AboutScreenActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(s)));
            return true;
        }
    }
}
