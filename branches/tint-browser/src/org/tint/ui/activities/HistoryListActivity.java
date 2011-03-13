package org.tint.ui.activities;

import org.tint.R;
import org.tint.adapters.HistoryExpandableListAdapter;
import org.tint.controllers.BookmarksHistoryController;
import org.tint.model.HistoryItem;
import org.tint.utils.Constants;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

/**
 * History list activity.
 */
public class HistoryListActivity extends ExpandableListActivity {
	
	private static final int CONTEXT_MENU_OPEN_IN_TAB = Menu.FIRST + 10;
	private static final int CONTEXT_MENU_DELETE_FROM_HISTORY = Menu.FIRST + 11;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                      
        
        registerForContextMenu(getExpandableListView());
        
        fillData();
	}
	
	/**
	 * Fill the history list.
	 */
	private void fillData() {
		HistoryExpandableListAdapter adapter = new HistoryExpandableListAdapter(this, BookmarksHistoryController.getInstance().getHistory(this), Browser.HISTORY_PROJECTION_DATE_INDEX);
        setListAdapter(adapter);
        
        if (getExpandableListAdapter().getGroupCount() > 0) {
        	getExpandableListView().expandGroup(0);
        }
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		HistoryItem item = (HistoryItem) getExpandableListAdapter().getChild(groupPosition, childPosition);
		doNavigateToUrl(item.getUrl(), false);
		
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		ExpandableListView.ExpandableListContextMenuInfo info =
			(ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		int child =	ExpandableListView.getPackedPositionChild(info.packedPosition);
		
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			HistoryItem item = (HistoryItem) getExpandableListAdapter().getChild(group, child);
			if (item != null) {
				menu.setHeaderTitle(item.getTitle());
				menu.add(0, CONTEXT_MENU_OPEN_IN_TAB, 0, R.string.HistoryListActivity_ContextMenuOpenInTab);
				menu.add(0, CONTEXT_MENU_DELETE_FROM_HISTORY, 0, R.string.HistoryListActivity_ContextMenuDeleteFromHistory);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			int child =	ExpandableListView.getPackedPositionChild(info.packedPosition);
			
			HistoryItem historyItem = (HistoryItem) getExpandableListAdapter().getChild(group, child);
			
			if (historyItem != null) {
				switch (item.getItemId()) {
				case CONTEXT_MENU_OPEN_IN_TAB:
					Intent i = new Intent();
		            i.putExtra(Constants.EXTRA_ID_NEW_TAB, true);
		            i.putExtra(Constants.EXTRA_ID_URL, historyItem.getUrl());
		            
		            if (getParent() != null) {
			        	getParent().setResult(RESULT_OK, i);
			        } else {
			        	setResult(RESULT_OK, i);
			        }
			        
			        finish();
					break;
				case CONTEXT_MENU_DELETE_FROM_HISTORY:
					BookmarksHistoryController.getInstance().deleteHistoryRecord(this, info.id);
					fillData();
					break;
				default: break;
				}
			}
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
