package org.tint.runnables;

import org.tint.adapters.BookmarksHistoryAdapter;

public class HistoryUpdaterRunnable implements Runnable {

	private String mTitle;
	private String mUrl;
	
	public HistoryUpdaterRunnable(String title, String url) {
		mTitle = title;
		mUrl = url;
	}
	
	@Override
	public void run() {
		BookmarksHistoryAdapter.getInstance().updateHistory(mTitle, mUrl);		
	}

}
