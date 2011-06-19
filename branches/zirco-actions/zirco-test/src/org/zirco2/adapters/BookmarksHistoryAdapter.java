package org.zirco2.adapters;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.Browser;

public class BookmarksHistoryAdapter {

	private Activity mActivity;
	
	/**
	 * Holder for singleton implementation.
	 */
	private static final class BookmarksHistoryAdapterHolder {
		private static final BookmarksHistoryAdapter INSTANCE = new BookmarksHistoryAdapter();
		/**
		 * Private Constructor.
		 */
		private BookmarksHistoryAdapterHolder() { }
	}
	
	/**
	 * Get the unique instance of the Controller.
	 * @return The instance of the Controller
	 */
	public static BookmarksHistoryAdapter getInstance() {
		return BookmarksHistoryAdapterHolder.INSTANCE;
	}
	
	private BookmarksHistoryAdapter() { }
	
	public void initialize(Activity activity) {
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
	
	public void updateHistory(String title, String url) {
		String[] colums = new String[] { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\"";
		
		Cursor cursor = mActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, null);
		
		if (cursor.moveToFirst()) {
			
			long id = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns._ID));
			int visits = cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.VISITS)) + 1;
			
			ContentValues values = new ContentValues();
			values.put(Browser.BookmarkColumns.TITLE, title);
			values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
			values.put(Browser.BookmarkColumns.VISITS, visits);
			
			mActivity.getContentResolver().update(android.provider.Browser.BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
			
		} else {
			ContentValues values = new ContentValues();
			values.put(Browser.BookmarkColumns.TITLE, title);
			values.put(Browser.BookmarkColumns.URL, url);
			values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
			values.put(Browser.BookmarkColumns.VISITS, 1);
			
			mActivity.getContentResolver().insert(android.provider.Browser.BOOKMARKS_URI, values);
		}
		
	}
	
}
