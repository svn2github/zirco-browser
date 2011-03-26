package org.tint.controllers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.tint.model.BookmarkItem;
import org.tint.model.HistoryItem;
import org.tint.utils.ApplicationUtils;
import org.tint.utils.Constants;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.provider.Browser;

/**
 * Class to manage history and bookmarks database. The Android history/bookmarks database is used.
 */
public final class BookmarksHistoryController {
	
	/**
	 * Holder for singleton implementation.
	 */
	private static final class BookmarksHistoryAdapterHolder {
		private static final BookmarksHistoryController INSTANCE = new BookmarksHistoryController();
		/**
		 * Private Constructor.
		 */
		private BookmarksHistoryAdapterHolder() { }
	}
	
	/**
	 * Get the unique instance of the Controller.
	 * @return The instance of the Controller
	 */
	public static BookmarksHistoryController getInstance() {
		return BookmarksHistoryAdapterHolder.INSTANCE;
	}
	
	/**
	 * Private constructor (singleton implementation).
	 */
	private BookmarksHistoryController() { }
	
	/**
	 * Get a Cursor on the whole content of the history/bookmarks database.
	 * @param currentActivity The parent activity.
	 * @return A Cursor.
	 * @see Cursor
	 */
	public Cursor getAllRecords(Activity currentActivity) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID,
				Browser.BookmarkColumns.TITLE,
				Browser.BookmarkColumns.URL,
				Browser.BookmarkColumns.VISITS,
				Browser.BookmarkColumns.DATE,
				Browser.BookmarkColumns.CREATED,
				Browser.BookmarkColumns.BOOKMARK };
		
		return currentActivity.managedQuery(Browser.BOOKMARKS_URI, colums, null, null, null);
	}
	
	/**
	 * Get a Cursor on bookmarks.
	 * @param currentActivity The parent activity.
	 * @return A Cursor on bookmarks.
	 * @see Cursor
	 */
	public Cursor getBookmarks(Activity currentActivity) {
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC";
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };
		
		return currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, orderClause);
	}
	
	/**
	 * Get a list of most visited bookmarks items, limited in size.
	 * @param currentActivity The parent activity.
	 * @param limit The size limit.
	 * @return A list of BookmarkItem.
	 */
	public List<BookmarkItem> getBookmarksWithLimit(Activity currentActivity, int limit) {
		List<BookmarkItem> result = new ArrayList<BookmarkItem>();
		
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC";
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.FAVICON };
				
		Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, orderClause);
		
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				
				int columnTitle = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
				
				int count = 0;
				while (!cursor.isAfterLast() &&
						(count < limit)) {
					
					BookmarkItem item = new BookmarkItem(
							cursor.getString(columnTitle),
							cursor.getString(columnUrl));
					
					result.add(item);
					
					count++;
					cursor.moveToNext();
				}
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	/**
	 * Get a bookmark, given its id.
	 * @param currentActivity The parent activity.
	 * @param id The bookmark id.
	 * @return The bookmark, as a BookmarkItem.
	 * @see BookmarkItem
	 */
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
			
	/**
	 * Get a Cursor the history, e.g. records wich have a visits count > 0. Sorted by last visited date.
	 * @param currentActivity The parent activity.
	 * @return A Cursor to history records.
	 * @see Cursor
	 */
	public Cursor getHistory(Activity currentActivity) {
		String whereClause = Browser.BookmarkColumns.VISITS + " > 0";
		String orderClause = Browser.BookmarkColumns.DATE + " DESC";
		
		return currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION, whereClause, null, orderClause);
	}
	
	/**
	 * Get a list of most recent history items, limited in size.
	 * @param currentActivity The parent activity.
	 * @param limit The size limit.
	 * @return A list of HistoryItem.
	 */
	public List<HistoryItem> getHistoryWithLimit(Activity currentActivity, int limit) {
		List<HistoryItem> result = new ArrayList<HistoryItem>();
		
		String whereClause = Browser.BookmarkColumns.VISITS + " > 0";
		String orderClause = Browser.BookmarkColumns.DATE + " DESC";
		
		Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION, whereClause, null, orderClause);
		
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				
				int columnId = cursor.getColumnIndex(Browser.BookmarkColumns._ID);
				int columnTitle = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
				
				int count = 0;
				while (!cursor.isAfterLast() &&
						(count < limit)) {
					
					HistoryItem item = new HistoryItem(
							cursor.getLong(columnId),
							cursor.getString(columnTitle),
							cursor.getString(columnUrl),
							null);
					
					result.add(item);
					
					count++;
					cursor.moveToNext();
				}
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	/**
	 * Update the history: visit count and last visited date.
	 * @param currentActivity The parent activity.
	 * @param title The title.
	 * @param url The url.
	 * @param originalUrl The original url 
	 */
	public void updateHistory(Activity currentActivity, String title, String url, String originalUrl) {
		String[] colums = new String[] { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\" OR " + Browser.BookmarkColumns.URL + " = \"" + originalUrl + "\"";
		
		Cursor cursor = currentActivity.managedQuery(Browser.BOOKMARKS_URI, colums, whereClause, null, null);
		
		if (cursor.moveToFirst()) {
			
			long id = cursor.getLong(cursor.getColumnIndex(Browser.BookmarkColumns._ID));
			int visits = cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.VISITS)) + 1;
			
			ContentValues values = new ContentValues();
			values.put(Browser.BookmarkColumns.TITLE, title);
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
		
		cursor.close();
	}
	
	/**
	 * Remove from history values prior to now minus the number of days defined in preferences.
	 * Only delete history items, not bookmarks.
	 * @param currentActivity The parent activity.
	 */
	public void truncateHistory(Activity currentActivity) {
		int historySize;
		try {
			historySize = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(currentActivity).getString(Constants.PREFERENCES_BROWSER_HISTORY_SIZE, "90"));
		} catch (NumberFormatException e) {
			historySize = 90;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());		
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_YEAR, - historySize);

		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 0 AND " + Browser.BookmarkColumns.DATE + " < " + c.getTimeInMillis();
		currentActivity.getContentResolver().delete(Browser.BOOKMARKS_URI, whereClause, null);
	}
	
	/**
	 * Update the favicon in history/bookmarks database.
	 * @param currentActivity The parent activity.
	 * @param url The url.
	 * @param originalUrl The original url.
	 * @param favicon The favicon.
	 */
	public void updateFavicon(Activity currentActivity, String url, String originalUrl, Bitmap favicon) {
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url + "\" OR " + Browser.BookmarkColumns.URL + " = \"" + originalUrl + "\"";
		
		BitmapDrawable icon = ApplicationUtils.getNormalizedFavicon(currentActivity, favicon);		
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();    	
		icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);
		
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
	
	/**
	 * Delete a bookmark, e.g. delete it if it has never been visited, or remove the bookmark flag, to keep history.
	 * @param currentActivity The parent activity.
	 * @param id The bookmark id.
	 */
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
					values.putNull(Browser.BookmarkColumns.CREATED);
					
					currentActivity.getContentResolver().update(Browser.BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
					
				} else {
					// never visited, it can be deleted.
					currentActivity.getContentResolver().delete(Browser.BOOKMARKS_URI, Browser.BookmarkColumns._ID + " = " + id, null);
					
				}
			}
		}
	}
	
	/**
	 * Delete an history record, e.g. reset the visited count and visited date if its a bookmark, or delete it if not.
	 * @param currentActivity The parent activity.
	 * @param id The history id.
	 */
	public void deleteHistoryRecord(Activity currentActivity, long id) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.BOOKMARK, Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;
		
		Cursor cursor = currentActivity.managedQuery(android.provider.Browser.BOOKMARKS_URI, colums, whereClause, null, null);
		if (cursor.moveToFirst()) {
			if (cursor.getInt(cursor.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) == 1) {
				// The record is a bookmark, so we cannot delete it. Instead, reset its visited count and last visited date.
				ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.VISITS, 0);
				values.putNull(Browser.BookmarkColumns.DATE);
				
				currentActivity.getContentResolver().update(Browser.BOOKMARKS_URI, values, Browser.BookmarkColumns._ID + " = " + id, null);
			} else {
				// The record is not a bookmark, we can delete it.
				currentActivity.getContentResolver().delete(Browser.BOOKMARKS_URI, Browser.BookmarkColumns._ID + " = " + id, null);
			}
		}
	}
	
	/**
	 * Get a Cursor on suggestions.
	 * @param currentActivity The parent activity.
	 * @param pattern The string to search for.
	 * @return A Cursor on suggestions from history/bookmarks.
	 * @see Cursor
	 */
	public Cursor getSuggestion(Activity currentActivity, String pattern) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.BOOKMARK };
		String sqlPattern = "%" + pattern + "%";
		String whereClause = Browser.BookmarkColumns.TITLE + " LIKE \"" + sqlPattern + "\" OR " + Browser.BookmarkColumns.URL + " LIKE \"" + sqlPattern + "\"";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC, " + Browser.BookmarkColumns.DATE + " DESC";
		
		return currentActivity.managedQuery(Browser.BOOKMARKS_URI, colums, whereClause, null, orderClause);
	}
	
	/**
	 * Insert a full record in history/bookmarks database.
	 * @param currentActivity The parent activity.
	 * @param title The record title.
	 * @param url The record url.
	 * @param visits The record visit count.
	 * @param date The record last visit date.
	 * @param created The record bookmark creation date.
	 * @param bookmark The bookmark flag.
	 */
	public void insertRawRecord(Activity currentActivity, String title, String url, int visits, long date, long created, int bookmark) {
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		values.put(Browser.BookmarkColumns.VISITS, visits);
		
		if (date > 0) {
			values.put(Browser.BookmarkColumns.DATE, date);
		} else {
			values.putNull(Browser.BookmarkColumns.DATE);
		}
		
		if (created > 0) {
			values.put(Browser.BookmarkColumns.CREATED, created);
		} else {
			values.putNull(Browser.BookmarkColumns.CREATED);
		}
		
		if (bookmark > 0) {
			values.put(Browser.BookmarkColumns.BOOKMARK, 1);
		} else {
			values.put(Browser.BookmarkColumns.BOOKMARK, 0);
		}
		
		currentActivity.getContentResolver().insert(Browser.BOOKMARKS_URI, values);
	}
	
	/**
	 * Clear the history/bookmarks table.
	 * @param currentActivity The parent activity.
	 * @param clearHistory If true, history items will be cleared.
	 * @param clearBookmarks If true, bookmarked items will be cleared.
	 */
	public void clearHistoryAndOrBookmarks(Activity currentActivity, boolean clearHistory, boolean clearBookmarks) {
		
		if (!clearHistory && !clearBookmarks) {
			return;
		}
		
		String whereClause = null;
		if (clearHistory && clearBookmarks) {
			whereClause = null;
		} else if (clearHistory) {
			whereClause = Browser.BookmarkColumns.BOOKMARK + " = 0";
		} else if (clearBookmarks) {
			whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		}
		
		currentActivity.getContentResolver().delete(Browser.BOOKMARKS_URI, whereClause, null);		
	}
	
}
