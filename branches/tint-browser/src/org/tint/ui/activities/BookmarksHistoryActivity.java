package org.tint.ui.activities;

import org.tint.R;
import org.tint.utils.Constants;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

/**
 * Bookmarks and history activity.
 */
public class BookmarksHistoryActivity extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_GENERAL_FULL_SCREEN, false)) {
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        }
		
		setContentView(R.layout.bookmarks_history_activity);
		
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		// Bookmarks
		intent = new Intent().setClass(this, BookmarksListActivity.class);
		
		spec = tabHost.newTabSpec("bookmarks").setIndicator(res.getString(R.string.BookmarksHistoryActivity_TabBookmarks),
                res.getDrawable(R.drawable.ic_tab_bookmarks))
                .setContent(intent);
		tabHost.addTab(spec);
		
		// History
		intent = new Intent().setClass(this, HistoryListActivity.class);

		spec = tabHost.newTabSpec("history").setIndicator(res.getString(R.string.BookmarksHistoryActivity_TabHistory),
                res.getDrawable(R.drawable.ic_tab_history))
                .setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}

}
