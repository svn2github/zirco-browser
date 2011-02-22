package org.tint.runnables;

import org.tint.controllers.BookmarksHistoryController;

import android.app.Activity;

public class HistoryUpdaterRunnable implements Runnable {

	private Activity mActivity;
	private String mTitle;
	private String mUrl;
	private String mOriginalUrl;
	
	public HistoryUpdaterRunnable(Activity activity, String title, String url, String originalUrl) {
		mActivity = activity;
		mTitle = title;
		mUrl = url;
		mOriginalUrl = originalUrl;
	}
	
	@Override
	public void run() {
		BookmarksHistoryController.getInstance().updateHistory(mActivity, mTitle, mUrl, mOriginalUrl);		
	}

}
