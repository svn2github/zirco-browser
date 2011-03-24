package org.tint.ui;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * Interface to activities managing WebViews.
 */
public interface IWebViewActivity {
	
	/**
	 * Open a new tab and navigate to the given url.
	 * @param url The url to navigate to.
	 * @param useAnimation if true, the switch between tabs will be made using animations.
	 * @return The index of the new tab.
	 */
	int addTab(String url, boolean useAnimation);
	
	/**
	 * Open a new tab and navigate to the given url, at the specified position.
	 * @param tabIndex The index to insert the new tab.
	 * @param url The url to navigate to.
	 * @param useAnimation if true, the switch between tabs will be made using animations.
	 * @return The index of the new tab.
	 */
	int addTab(int tabIndex, String url, boolean useAnimation);
	
	/**
	 * Get the current tab index.
	 * @return The current tab index.
	 */
	int getCurrentWebViewIndex();
	
	/**
	 * Notify of a web page load start.
	 * @param webView The WebView source of the notification.
	 * @param url The url being loaded.
	 */
	void onPageStarted(WebView webView, String url);
	
	/**
	 * Notify of a web page load end.
	 * @param webView The WebView source of the notification.
	 */
	void onPageFinished(WebView webView);
	
	/**
	 * Notify of a web page load progress.
	 * @param webView The WebView source of the notification.
	 * @param newProgress The progress value.
	 */
	void onPageProgress(WebView webView, int newProgress);
	
	/**
	 * Notify of a web page receiving its favicon.
	 * @param webView The WebView source of the notification.
	 * @param favicon The favicon.
	 */
	void onReceivedFavicon(WebView webView, Bitmap favicon);

}
