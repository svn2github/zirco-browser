package org.zirco2.ui.components;

import org.zirco2.R;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class CustomWebChromeClient extends WebChromeClient {
	
	private ProgressBar mProgressBar;
	
	public CustomWebChromeClient(View view) {
		mProgressBar = (ProgressBar) view.findViewById(R.id.WebViewProgress);
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		mProgressBar.setProgress(newProgress);
		super.onProgressChanged(view, newProgress);
	}

}
