package org.tint.runnables;

import org.tint.controllers.BookmarksHistoryController;

import android.app.Activity;

/**
 * Runnable to update the history during browsing.
 */
public class HistoryUpdaterRunnable implements Runnable {

	private Activity mActivity;
	private String mTitle;
	private String mUrl;
	private String mOriginalUrl;
	
	/**
	 * Constructor.
	 * @param activity The parent activity.
	 * @param title The page title.
	 * @param url The page url.
	 * @param originalUrl The page original url.
	 */
	public HistoryUpdaterRunnable(Activity activity, String title, String url, String originalUrl) {
		mActivity = activity;
		mTitle = title;
		mUrl = url;
		mOriginalUrl = originalUrl;
	}
	
	@Override
	public void run() {
		BookmarksHistoryController.getInstance().updateHistory(mActivity, mTitle, mUrl, mOriginalUrl);
		BookmarksHistoryController.getInstance().truncateHistory(mActivity);
	}

}
