package org.tint.utils;

import org.tint.R;

import android.content.Context;

/**
 * Various constants.
 */
public class Constants {
	
	public static final String EXTRA_ID_NEW_TAB = "EXTRA_ID_NEW_TAB";
	public static final String EXTRA_ID_URL = "EXTRA_ID_URL";
	public static final String EXTRA_CURRENT_VIEW_INDEX = "EXTRA_CURRENT_VIEW_INDEX";
	
	public static final String EXTRA_ID_BOOKMARK_ID = "EXTRA_ID_BOOKMARK_ID";
	public static final String EXTRA_ID_BOOKMARK_TITLE = "EXTRA_ID_BOOKMARK_TITLE";
	public static final String EXTRA_ID_BOOKMARK_URL = "EXTRA_ID_BOOKMARK_URL";		
	
	/**
	 * User agents.
	 */
	public static String USER_AGENT_DEFAULT = "";
	public static String USER_AGENT_DESKTOP = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.44 Safari/534.7";
	
	/**
	 * Search urls.
	 */	
	public static String URL_SEARCH_GOOGLE = "http://www.google.com/search?ie=UTF-8&sourceid=navclient&gfns=1&q=%s";
	public static String URL_SEARCH_WIKIPEDIA = "http://en.wikipedia.org/w/index.php?search=%s&go=Go";
	
	/**
	 * Preferences.
	 */
	public static final String PREFERENCES_GENERAL_HOME_PAGE = "GeneralHomePage";
	public static final String PREFERENCES_GENERAL_FULL_SCREEN = "GeneralFullScreen";
	public static final String PREFERENCES_GENERAL_HIDE_TITLE_BARS = "GeneralHideTitleBars";
	public static final String PREFERENCES_GENERAL_SEARCH_URL = "GeneralSearchUrl";
	public static final String PREFERENCES_GENERAL_BUBBLE_POSITION = "GeneralBubblePosition";
	public static final String PREFERENCES_GENERAL_BARS_DURATION = "GeneralBarsDuration";
	
	public static final String PREFERENCES_BROWSER_ENABLE_JAVASCRIPT = "BrowserEnableJavascript";
	public static final String PREFERENCES_BROWSER_ENABLE_IMAGES = "BrowserEnableImages";
	public static final String PREFERENCES_BROWSER_ENABLE_FORM_DATA = "BrowserEnableFormData";
	public static final String PREFERENCES_BROWSER_ENABLE_PASSWORDS = "BrowserEnablePasswords";
	public static final String PREFERENCES_BROWSER_ENABLE_COOKIES = "BrowserEnableCookies";
	public static final String PREFERENCES_BROWSER_HISTORY_SIZE = "BrowserHistorySize";
	public static final String PREFERENCES_BROWSER_DEFAULT_ZOOM_LEVEL = "BrowserDefaultZoomLevel";
	public static final String PREFERENCES_BROWSER_ENABLE_PLUGINS = "BrowserEnablePlugins";
	public static final String PREFERENCES_BROWSER_USER_AGENT = "BrowserUserAgent";	
	
	/**
	 * Initialize the search url "constants", which depends on the user local.
	 * @param context The current context.
	 */
	public static void initializeConstantsFromResources(Context context) {
		URL_SEARCH_GOOGLE = context.getResources().getString(R.string.Constants_SearchUrlGoogle);
		URL_SEARCH_WIKIPEDIA = context.getResources().getString(R.string.Constants_SearchUrlWikipedia);
	}
	
}
