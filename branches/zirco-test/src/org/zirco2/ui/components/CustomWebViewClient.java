package org.zirco2.ui.components;

import org.zirco2.R;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class CustomWebViewClient extends WebViewClient {
	
	private ProgressBar mProgressBar;
	
	public CustomWebViewClient(View view) {
		mProgressBar = (ProgressBar) view.findViewById(R.id.WebViewProgress);
		mProgressBar.setVisibility(View.GONE);
		mProgressBar.setMax(100);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		mProgressBar.setProgress(100);
		mProgressBar.setVisibility(View.GONE);
		super.onPageFinished(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		mProgressBar.setProgress(0);
		mProgressBar.setVisibility(View.VISIBLE);
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		Log.d("NEW SCALE", newScale + "");

		super.onScaleChanged(view, oldScale, newScale);
	}

}
