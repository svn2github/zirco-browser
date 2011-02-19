package org.zirco2.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.provider.Browser;

public class BookmarksHistoryAdapter {

	private Activity mActivity;
	
	public BookmarksHistoryAdapter(Activity activity) {
		mActivity = activity;
	}
	
	public Cursor getBookmarks() {
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC";
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };		
		return mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, orderClause);
	}
	
	public String getBookmarkUrlById(long id) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.URL };
		Cursor cursor = mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, Browser.BookmarkColumns._ID + "=" + id, null, null);
		
		if (cursor.moveToFirst()) {
			return cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL));
		}
		
		return null;
	}
	
	public Cursor getHistory() {
		String whereClause = Browser.BookmarkColumns.VISITS + " > 0";
		String orderClause = Browser.BookmarkColumns.DATE + " DESC";
		
		return mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION, whereClause, null, orderClause);
	}
	
}
