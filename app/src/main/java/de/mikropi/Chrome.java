package de.mikropi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

public class Chrome extends WebChromeClient {

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    private MainActivity context;
    Chrome(MainActivity context) {
        this.context = context;
    }

    public Bitmap getDefaultVideoPoster()
    {
        if (mCustomView == null) {
            return null;
        }
        return BitmapFactory.decodeResource(context.getApplicationContext().getResources(), 2130837573);
    }

    public void onHideCustomView()
    {
        ((FrameLayout)context.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        context.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        context.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
    {
        if (this.mCustomView != null)
        {
            onHideCustomView();
            return;
        }
        this.mCustomView = paramView;
        this.mOriginalSystemUiVisibility = context.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = context.getRequestedOrientation();
        this.mCustomViewCallback = paramCustomViewCallback;
        ((FrameLayout)context.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
        context.getWindow().getDecorView().setSystemUiVisibility(3846);
    }
}
