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
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };		
		return mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, Browser.BookmarkColumns.BOOKMARK + "=1", null, null);
	}
	
	public String getBookmarkUrlById(long id) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.URL };
		Cursor cursor = mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, Browser.BookmarkColumns._ID + "=" + id, null, null);
		
		if (cursor.moveToFirst()) {
			return cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL));
		}
		
		return null;
	}
	
}
