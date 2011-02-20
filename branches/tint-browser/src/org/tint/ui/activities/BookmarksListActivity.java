package org.tint.ui.activities;

import org.tint.R;
import org.tint.adapters.BookmarksHistoryAdapter;
import org.tint.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class BookmarksListActivity extends Activity {
	
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
				
				String url = BookmarksHistoryAdapter.getInstance().getBookmarkUrlById(BookmarksListActivity.this, id);
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
        
        fillData();
	}

	private void fillData() {
		Cursor cursor = BookmarksHistoryAdapter.getInstance().getBookmarks(this);
		
		String[] from = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
		int[] to = new int[] {R.id.BookmarkRow_Title, R.id.BookmarkRow_Url};
		
		mCursorAdapter = new SimpleCursorAdapter(this, R.layout.bookmark_row, cursor, from, to);
		mList.setAdapter(mCursorAdapter);
	}
	
}
