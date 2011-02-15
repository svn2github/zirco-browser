package org.zirco2;

import android.webkit.WebView;
import android.widget.RelativeLayout;

public class WebViewContainer {

	private RelativeLayout mView;
	private WebView mWebView;
	
	public WebViewContainer(RelativeLayout view, WebView webView) {
		mView = view;
		mWebView = webView;
	}
	
	public RelativeLayout getView() {
		return mView;
	}
	
	public WebView getWebView() {
		return mWebView;
	}
	
}
