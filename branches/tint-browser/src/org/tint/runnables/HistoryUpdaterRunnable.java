package org.tint.runnables;

import org.tint.adapters.BookmarksHistoryAdapter;

import android.app.Activity;

public class HistoryUpdaterRunnable implements Runnable {

	private Activity mActivity;
	private String mTitle;
	private String mUrl;
	
	public HistoryUpdaterRunnable(Activity activity, String title, String url) {
		mActivity = activity;
		mTitle = title;
		mUrl = url;
	}
	
	@Override
	public void run() {
		BookmarksHistoryAdapter.getInstance().updateHistory(mActivity, mTitle, mUrl);		
	}

}
