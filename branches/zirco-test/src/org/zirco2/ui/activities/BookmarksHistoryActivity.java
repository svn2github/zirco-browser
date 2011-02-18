package org.zirco2.ui.activities;

import org.zirco2.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class BookmarksHistoryActivity extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	}

}
