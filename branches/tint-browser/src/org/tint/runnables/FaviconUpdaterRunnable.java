package org.tint.runnables;

import org.tint.controllers.BookmarksHistoryController;

import android.app.Activity;
import android.graphics.Bitmap;

public class FaviconUpdaterRunnable implements Runnable {
	
	private Activity mActivity;
	private String mUrl;
	private String mOriginalUrl;
	private Bitmap mFavIcon;

	public FaviconUpdaterRunnable(Activity activity, String url, String originalUrl, Bitmap favicon) {
		mActivity = activity;
		mUrl = url;
		mOriginalUrl = originalUrl;
		mFavIcon = favicon;
	}
	
	@Override
	public void run() {
		BookmarksHistoryController.getInstance().updateFavicon(mActivity, mUrl, mOriginalUrl, mFavIcon);
	}

}
