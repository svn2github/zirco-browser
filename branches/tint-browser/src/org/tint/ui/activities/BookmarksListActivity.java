package org.tint.ui.activities;

import org.tint.R;
import org.tint.adapters.BookmarkItem;
import org.tint.adapters.BookmarksCursorAdapter;
import org.tint.adapters.BookmarksHistoryAdapter;
import org.tint.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class BookmarksListActivity extends Activity {
	
	public static int ACTIVITY_EDIT_BOOKMARK = 0;
	
	private static final int MENU_ADD_BOOKMARK = Menu.FIRST;
	
	private static final int CONTEXT_MENU_OPEN_IN_TAB = Menu.FIRST + 10;
    private static final int CONTEXT_MENU_EDIT_BOOKMARK = Menu.FIRST + 11;
    private static final int CONTEXT_MENU_DELETE_BOOKMARK = Menu.FIRST + 12;
	
	private ListView mList;
	
	private SimpleCursorAdapter mCursorAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.bookmarks_list_activity);
        
        View emptyView = findViewById(android.R.id.empty);
        mList = (ListView) findViewById(android.R.id.list);
        
        mList.setEmptyView(emptyView);
        
        mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				
				String url;
				BookmarkItem selectedItem = BookmarksHistoryAdapter.getInstance().getBookmarkById(BookmarksListActivity.this, id);
				if (selectedItem != null) {
					url = selectedItem.getUrl();
				} else {
					url = null;
				}
				
				if (url != null) {				
					Intent result = new Intent();
					result.putExtra(Constants.EXTRA_ID_NEW_TAB, false);
					result.putExtra(Constants.EXTRA_ID_URL, url);
					
					if (getParent() != null) {
			        	getParent().setResult(RESULT_OK, result);
			        } else {
			        	setResult(RESULT_OK, result);
			        }
			        
			        finish();
				}				
			}
		});
        
        registerForContextMenu(mList);
        
        fillData();
	}

	private void fillData() {
		Cursor cursor = BookmarksHistoryAdapter.getInstance().getBookmarks(this);
		
		String[] from = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
		int[] to = new int[] {R.id.BookmarkRow_Title, R.id.BookmarkRow_Url};

		mCursorAdapter = new BookmarksCursorAdapter(this, R.layout.bookmark_row, cursor, from, to);
		mList.setAdapter(mCursorAdapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		long id = ((AdapterContextMenuInfo) menuInfo).id;
		if (id != -1) {
			BookmarkItem selectedItem = BookmarksHistoryAdapter.getInstance().getBookmarkById(BookmarksListActivity.this, id);
			if (selectedItem != null) {
				menu.setHeaderTitle(selectedItem.getTitle());
			}
		}
		
		menu.add(0, CONTEXT_MENU_OPEN_IN_TAB, 0, R.string.BookmarksListActivity_ContextMenuOpenInTab);
        menu.add(0, CONTEXT_MENU_EDIT_BOOKMARK, 0, R.string.BookmarksListActivity_ContextMenuEdit);
        menu.add(0, CONTEXT_MENU_DELETE_BOOKMARK, 0, R.string.BookmarksListActivity_ContextMenuDelete);		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		Intent i;
		BookmarkItem selectedItem;
		switch (item.getItemId()) {
		case CONTEXT_MENU_OPEN_IN_TAB:
			selectedItem = BookmarksHistoryAdapter.getInstance().getBookmarkById(this, info.id);
			if (selectedItem != null) {
				i = new Intent();
	            i.putExtra(Constants.EXTRA_ID_NEW_TAB, true);
	            i.putExtra(Constants.EXTRA_ID_URL, selectedItem.getUrl());
	            
	            if (getParent() != null) {
		        	getParent().setResult(RESULT_OK, i);
		        } else {
		        	setResult(RESULT_OK, i);
		        }
		        
		        finish();
			}
			
			return true;
		case CONTEXT_MENU_EDIT_BOOKMARK:
			selectedItem = BookmarksHistoryAdapter.getInstance().getBookmarkById(this, info.id);
			if (selectedItem != null) {
				i = new Intent(this, EditBookmarkActivity.class);
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, info.id);
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, selectedItem.getTitle());
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, selectedItem.getUrl());
				
				startActivityForResult(i, ACTIVITY_EDIT_BOOKMARK);
			}
			
			return true;
		case CONTEXT_MENU_DELETE_BOOKMARK:
			BookmarksHistoryAdapter.getInstance().deleteBookmark(this, info.id);
			fillData();
			
			return true;
		default: return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			fillData();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item;
		
		item = menu.add(0, MENU_ADD_BOOKMARK, 0, R.string.BookmarksListActivity_MenuAddBookmark);
		item.setIcon(R.drawable.ic_menu_add_bookmark);
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case MENU_ADD_BOOKMARK:
			i = new Intent(this, EditBookmarkActivity.class);
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
			
			startActivityForResult(i, ACTIVITY_EDIT_BOOKMARK);
			return true;
		default: return super.onMenuItemSelected(featureId, item);
		}		
	}
	
}
