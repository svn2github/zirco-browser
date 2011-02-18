package org.zirco2.ui.activities;

import org.zirco2.adapters.BookmarksHistoryAdapter;
import org.zirco2.adapters.HistoryExpandableListAdapter;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.provider.Browser;

public class HistoryListActivity extends ExpandableListActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        HistoryExpandableListAdapter adapter = new HistoryExpandableListAdapter(this, new BookmarksHistoryAdapter(this).getHistory(), Browser.HISTORY_PROJECTION_DATE_INDEX);
        setListAdapter(adapter);
	}

}
