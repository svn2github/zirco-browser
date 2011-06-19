package org.zirco2.ui.activities;

import org.zirco2.adapters.BookmarksHistoryAdapter;
import org.zirco2.adapters.HistoryExpandableListAdapter;
import org.zirco2.adapters.HistoryItem;
import org.zirco2.utils.Constants;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

public class HistoryListActivity extends ExpandableListActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        HistoryExpandableListAdapter adapter = new HistoryExpandableListAdapter(this, BookmarksHistoryAdapter.getInstance().getHistory(), Browser.HISTORY_PROJECTION_DATE_INDEX);
        setListAdapter(adapter);
        
        if (getExpandableListAdapter().getGroupCount() > 0) {
        	getExpandableListView().expandGroup(0);
        }
        
        registerForContextMenu(getExpandableListView());
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		HistoryItem item = (HistoryItem) getExpandableListAdapter().getChild(groupPosition, childPosition);
		doNavigateToUrl(item.getUrl(), false);
		
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			
		}
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Load the given url.
	 * @param url The url.
	 * @param newTab If True, will open a new tab. If False, the current tab is used.
	 */
	private void doNavigateToUrl(String url, boolean newTab) {
		Intent result = new Intent();
        result.putExtra(Constants.EXTRA_ID_NEW_TAB, newTab);
        result.putExtra(Constants.EXTRA_ID_URL,  url);
        
        if (getParent() != null) {
        	getParent().setResult(RESULT_OK, result);
        } else {
        	setResult(RESULT_OK, result);
        }
        
        finish();
	}

}
