package org.zirco2.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Browser;

public class BookmarksAdapter {

	private Activity mActivity;
	
	public BookmarksAdapter(Activity activity) {
		mActivity = activity;
	}
	
	public Cursor getBookmarks() {
		String[] colums = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };		
		return mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, Browser.BookmarkColumns.BOOKMARK + "=1", null, null);
	}
	
}
