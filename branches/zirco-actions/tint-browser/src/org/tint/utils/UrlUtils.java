/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.tint.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.tint.R;
import org.tint.controllers.BookmarksHistoryController;
import org.tint.model.BookmarkItem;
import org.tint.model.HistoryItem;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Url management utils.
 */
public class UrlUtils {
	
	public static final String URL_ABOUT_BLANK = "about:blank";
	public static final String URL_ABOUT_START = "about:start";
	public static final String URL_ACTION_SEARCH = "action:search?q=";
	
	private static String mRawStartPage = null;
	private static String mRawStartPageStyles = null;
	private static String mRawStartPageJs = null;
	private static String mRawStartPageBookmarks = null;
	private static String mRawStartPageHistory = null;
	
	private static String mRawStartPageSearch = null;

	/**
	 * Check if a string is an url.
	 * For now, just consider that if a string contains a dot, it is an url.
	 * @param url The url to check.
	 * @return True if the string is an url.
	 */
	public static boolean isUrl(String url) {
		return url.equals(URL_ABOUT_BLANK) ||
			url.equals(URL_ABOUT_START) ||
			url.contains(".");
	}
		
	/**
	 * Get the current search url.
	 * @param context The current context.
	 * @param searchTerms The terms to search for.
	 * @return The search url.
	 */
	/*
	public static String getSearchUrl(Context context, String searchTerms) {
		String currentSearchUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCES_GENERAL_SEARCH_URL, Constants.URL_SEARCH_GOOGLE);
		return String.format(currentSearchUrl, searchTerms);
	}
	*/
	
	/**
	 * Check en url. Add http:// before if missing.
	 * @param url The url to check.
	 * @return The modified url if necessary.
	 */
	public static String checkUrl(String url) {
		if ((url != null) &&
    			(url.length() > 0)) {
    	
    		if ((!url.startsWith("http://")) &&
    				(!url.startsWith("https://")) &&
    				(!url.startsWith(URL_ABOUT_BLANK)) &&
    				(!url.startsWith(URL_ABOUT_START))) {
    			
    			url = "http://" + url;
    			
    		}
		}
		
		return url;
	}
	
	/**
	 * Get the current search url.
	 * @param context The current context.
	 * @param searchTerms The terms to search for.
	 * @return The search url.
	 */
	public static String getSearchUrl(Context context, String searchTerms) {
		String currentSearchUrl = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCES_GENERAL_SEARCH_URL, Constants.URL_SEARCH_GOOGLE);
		return String.format(currentSearchUrl, searchTerms);
	}
	
	/**
	 * Load a raw string resource.
	 * @param context The current context.
	 * @param resourceId The resource id.
	 * @return The loaded string.
	 */
	private static String getStringFromRawResource(Context context, int resourceId) {
		String result = null;
		
		InputStream is = context.getResources().openRawResource(resourceId);
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {					
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				Log.w("UrlUtils", String.format("Unable to load resource %s: %s", resourceId, e.getMessage()));
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					Log.w("UrlUtils", String.format("Unable to load resource %s: %s", resourceId, e.getMessage()));
				}
			}
			result = sb.toString();
		} else {        
			result = "";
		}
		
		return result;
	}
	
	/**
	 * Build the html result of the most recent bookmarks.
	 * @param activity The current activity.
	 * @return The html result of the most recent bookmarks.
	 */
	private static String getBookmarksHtml(Activity currentActivity) {
		String result = "";
		StringBuilder bookmarksSb = new StringBuilder();
		
		//if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREFERENCES_START_PAGE_SHOW_BOOKMARKS, true)) {
		if (true) {
			
			int limit;
			try {
				//limit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCES_START_PAGE_BOOKMARKS_LIMIT, "5"));
				limit = 5;
			} catch (Exception e) {
				limit = 5;
			}
			
			List<BookmarkItem> items = BookmarksHistoryController.getInstance().getBookmarksWithLimit(currentActivity, limit);
			
			for (BookmarkItem item : items) {
				bookmarksSb.append(String.format("<li><a href=\"%s\">%s</a></li>",
						item.getUrl(),
						item.getTitle()));
			}		
		}
		
		result = String.format(mRawStartPageBookmarks,
				currentActivity.getResources().getString(R.string.StartPage_Bookmarks),
				bookmarksSb.toString());
		
		return result;
	}
	
	/**
	 * Build the html result of the most recent history.
	 * @param activity The current activity.
	 * @return The html result of the most recent history.
	 */
	private static String getHistoryHtml(Activity currentActivity) {
		String result = "";
		StringBuilder historySb = new StringBuilder();
		
		//if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREFERENCES_START_PAGE_SHOW_HISTORY, true)) {
		if (true) {

			int limit;
			try {
				//limit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCES_START_PAGE_HISTORY_LIMIT, "5"));
				limit = 5;
			} catch (Exception e) {
				limit = 5;
			}
			
			List<HistoryItem> items = BookmarksHistoryController.getInstance().getHistoryWithLimit(currentActivity, limit);
			
			for (HistoryItem item : items) {
				historySb.append(String.format("<li><a href=\"%s\">%s</a></li>",
						item.getUrl(),
						item.getTitle()));
			}						
		}
		
		result = String.format(mRawStartPageHistory,
				currentActivity.getResources().getString(R.string.StartPage_History),
				historySb.toString());
		
		return result;
	}
	
	/**
	 * Load the start page html.
	 * @param activity The current activity.
	 * @return The start page html.
	 */
	public static String getStartPage(Activity currentActivity) {
		
		if (mRawStartPage == null) {
			
			mRawStartPage = getStringFromRawResource(currentActivity, R.raw.start);
			mRawStartPageStyles = getStringFromRawResource(currentActivity, R.raw.start_style);
			mRawStartPageJs = getStringFromRawResource(currentActivity, R.raw.start_js);
			mRawStartPageBookmarks = getStringFromRawResource(currentActivity, R.raw.start_bookmarks);
			mRawStartPageHistory = getStringFromRawResource(currentActivity, R.raw.start_history);
			
			mRawStartPageSearch = getStringFromRawResource(currentActivity, R.raw.start_search);
		}
		
		String result = mRawStartPage;
	
		String bookmarksHtml = getBookmarksHtml(currentActivity);
		String historyHtml = getHistoryHtml(currentActivity);				
		
		
		String searchHtml = "";
		//if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREFERENCES_START_PAGE_SHOW_SEARCH, true)) {
		if (true) {
			searchHtml = String.format(mRawStartPageSearch, currentActivity.getResources().getString(R.string.StartPage_Search), currentActivity.getResources().getString(R.string.StartPage_SearchButton));
		}
		
		String bodyHtml = searchHtml + bookmarksHtml + historyHtml;
		
		result = String.format(mRawStartPage,
				mRawStartPageStyles,
				mRawStartPageJs,
				currentActivity.getResources().getString(R.string.StartPage_Welcome),
				bodyHtml);		
		
		return result;
	}
}
