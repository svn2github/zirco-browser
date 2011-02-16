package org.zirco2;

import org.zirco2.ui.components.CustomWebView;

import android.widget.RelativeLayout;

public class WebViewContainer {

	private RelativeLayout mView;
	private CustomWebView mWebView;
	
	public WebViewContainer(RelativeLayout view, CustomWebView webView) {
		mView = view;
		mWebView = webView;
	}
	
	public RelativeLayout getView() {
		return mView;
	}
	
	public CustomWebView getWebView() {
		return mWebView;
	}
	
}
