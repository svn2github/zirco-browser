package org.tint.model;

import org.tint.ui.components.CustomWebView;

import android.widget.RelativeLayout;

/**
 * Represent and association of a WebView and its parent layout in the ViewFlipper.
 */
public class WebViewContainer {

	private RelativeLayout mView;
	private CustomWebView mWebView;
	
	/**
	 * Constructor.
	 * @param view The parent view.
	 * @param webView The WebView.
	 */
	public WebViewContainer(RelativeLayout view, CustomWebView webView) {
		mView = view;
		mWebView = webView;
	}
	
	/**
	 * Get the parent view.
	 * @return The parent view.
	 */
	public RelativeLayout getView() {
		return mView;
	}
	
	/**
	 * Get the WebView.
	 * @return The WebView.
	 */
	public CustomWebView getWebView() {
		return mWebView;
	}
	
}
