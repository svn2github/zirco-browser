package org.tint.adapters;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.Browser;

public class BookmarksHistoryAdapter {
	
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
	
	public Cursor getBookmarks(Activity currentActivity) {
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC";
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };
		
		return currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, orderClause);
	}
	
	public BookmarkItem getBookmarkById(Activity currentActivity, long id) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
		Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, Browser.BookmarkColumns._ID + "=" + id, null, null);
		
		if (cursor.moveToFirst()) {
			String title = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.TITLE));
			String url = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL));
			return new BookmarkItem(title, url);
		}
		
		return null;
	}
	
	public Cursor getHistory(Activity currentActivity) {
		String whereClause = Browser.BookmarkColumns.VISITS + " > 0";
		String orderClause = Browser.BookmarkColumns.DATE + " DESC";
		
		return currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION, whereClause, null, orderClause);
	}
	
	public void updateHistory(Activity currentActivity, String title, String url) {
		String[] colums = new String[] { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\"";
		
		Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, null);
		
		if (cursor.moveToFirst()) {
			
			long id = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns._ID));
			int visits = cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.VISITS)) + 1;
			
			ContentValues values = new ContentValues();
			//values.put(Browser.BookmarkColumns.TITLE, title);
			values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
			values.put(Browser.BookmarkColumns.VISITS, visits);
			
			currentActivity.getContentResolver().update(android.provider.Browser.BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
			
		} else {
			ContentValues values = new ContentValues();
			values.put(Browser.BookmarkColumns.TITLE, title);
			values.put(Browser.BookmarkColumns.URL, url);
			values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
			values.put(Browser.BookmarkColumns.VISITS, 1);
			
			currentActivity.getContentResolver().insert(android.provider.Browser.BOOKMARKS_URI, values);
		}		
	}
	
	public void updateFavicon(Activity currentActivity, String url, String originalUrl, Bitmap favicon) {
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\" OR " + Browser.BookmarkColumns.URL + " = \"" + originalUrl + "\"";
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();    	
		favicon.compress(Bitmap.CompressFormat.PNG, 100, os);
		
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.FAVICON, os.toByteArray());
		
		currentActivity.getContentResolver().update(android.provider.Browser.BOOKMARKS_URI, values, whereClause, null);
	}
	
	/**
	 * Modify a bookmark/history record. If an id is provided, it look for it and update its values. If not, values will be inserted.
	 * If no id is provided, it look for a record with the given url. It found, its values are updated. If not, values will be inserted.
	 * @param currentActivity The current activity.
	 * @param id The record id to look for.
	 * @param title The record title.
	 * @param url The record url.
	 * @param isBookmark If True, the record will be a bookmark.
	 */
	public void setAsBookmark(Activity currentActivity, long id, String title, String url, boolean isBookmark) {
		
		boolean bookmarkExist = false;
		
		if (id != -1) {
			String[] colums = new String[] { Browser.BookmarkColumns._ID };
			String whereClause = Browser.BookmarkColumns._ID + " = " + id;
			
			Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, null);
			bookmarkExist = cursor.moveToFirst();
		} else {
			String[] colums = new String[] { Browser.BookmarkColumns._ID };
			String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\"";
			
			Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, null);
			bookmarkExist = cursor.moveToFirst();
			if (bookmarkExist) {
				id = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns._ID));
			}
		}
		
		ContentValues values = new ContentValues();
		if (title != null) {
			values.put(Browser.BookmarkColumns.TITLE, title);
		}
		
		if (url != null) {
			values.put(Browser.BookmarkColumns.URL, url);
		}
		
		if (isBookmark) {
			values.put(Browser.BookmarkColumns.BOOKMARK, 1);
			values.put(Browser.BookmarkColumns.CREATED, new Date().getTime());
		} else {
			values.put(Browser.BookmarkColumns.BOOKMARK, 0);
		}
		
		if (bookmarkExist) {						
			currentActivity.getContentResolver().update(android.provider.Browser.BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
		} else {			
			currentActivity.getContentResolver().insert(android.provider.Browser.BOOKMARKS_URI, values);
		}
	}
	
	public void deleteBookmark(Activity currentActivity, long id) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.BOOKMARK, Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;
		
		Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, null);
		if (cursor.moveToFirst()) {
			if (cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) == 1) {
				if (cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.VISITS)) > 0) {

					// If this record has been visited, keep it in history, but remove its bookmark flag.
					ContentValues values = new ContentValues();
					values.put(Browser.BookmarkColumns.BOOKMARK, 0);
					
					currentActivity.getContentResolver().update(Browser.BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
					
				} else {
					// never visited, it can be deleted.
					currentActivity.getContentResolver().delete(Browser.BOOKMARKS_URI, Browser.BookmarkColumns._ID + " = " + id, null);
					
				}
			}
		}
	}
	
}
