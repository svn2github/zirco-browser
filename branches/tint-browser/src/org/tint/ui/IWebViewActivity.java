package org.tint.ui;

/**
 * Interface to activities managing WebViews.
 */
public interface IWebViewActivity {
	
	/**
	 * Open a new tab and navigate to the given url.
	 * @param url The url to navigate to.
	 * @return The index of the new tab.
	 */
	int addTab(String url);
	
	/**
	 * Open a new tab and navigate to the given url, at the specified position.
	 * @param tabIndex The index to insert the new tab.
	 * @param url The url to navigate to.
	 * @return The index of the new tab.
	 */
	int addTab(int tabIndex, String url);
	
	/**
	 * Get the current tab index.
	 * @return The current tab index.
	 */
	int getCurrentWebViewIndex();

}
