package de.mikropi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar spinner;
    private Button btnReload;

    private final String TAG = "Mikropi-Log";
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        spinner = (ProgressBar)findViewById(R.id.spinner);
        btnReload = (Button)findViewById(R.id.reload);

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReload.setVisibility(View.GONE);
                webView.reload();
            }
        });
        spinner.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        webView.setWebChromeClient(new Chrome(this));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                spinner.setVisibility(View.VISIBLE);

                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                spinner.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            };
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                //Your code to do
                webView.setVisibility(View.GONE);
                btnReload.setVisibility(View.VISIBLE);

            }
        });
        webView.loadUrl("https://mikropi.de/login.php");
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            Toast.makeText(this, "Long Press", Toast.LENGTH_SHORT).show();
            //Long Press code goes here

            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:

                    String url = webView.getUrl();
                    if (url.contains("index.php") && !url.contains("cuts")) {
                        if (doubleBackToExitPressedOnce) {
                            super.onBackPressed();
                            return false;
                        }
                        this.doubleBackToExitPressedOnce = true;
                        Toast.makeText(this, getString(R.string.closing), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce=false;
                            }
                        }, 2000);
                    } else {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                    }

                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                        if (doubleBackToExitPressedOnce) {
                            clearCache(this, 0);
                            Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        this.doubleBackToExitPressedOnce = true;
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce=false;
                            }
                        }, 2000);
                    }

                    return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        webView.loadUrl(webView.getUrl());
    }


    //helper method for clearCache() , recursive
//returns number of deleted files
    private int clearCacheFolder(final File dir, final int numDays) {

        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {

                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }
        return deletedFiles;
    }

    /*
     * Delete the files older than numDays days from the application cache
     * 0 means all files.
     */
    public void clearCache(final Context context, final int numDays) {
        Log.i(TAG, String.format("Starting cache prune, deleting files older than %d days", numDays));
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
        Log.i(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
    }

}
