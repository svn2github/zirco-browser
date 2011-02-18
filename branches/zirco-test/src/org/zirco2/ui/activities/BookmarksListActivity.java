package org.zirco2.ui.activities;

import org.zirco2.R;
import org.zirco2.adapters.BookmarksAdapter;
import org.zirco2.utils.Constants;

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
	
	private BookmarksAdapter mBookmarksAdapter;
	private ListView mList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.bookmarks_list_activity);
        
        mBookmarksAdapter = new BookmarksAdapter(this);
        
        View emptyView = findViewById(R.id.BookmarksListActivity_EmptyTextView);
        mList = (ListView) findViewById(R.id.BookmarksListActivity_List);
        
        mList.setEmptyView(emptyView);
        
        mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				
				String url = mBookmarksAdapter.getBookmarkUrlById(id);
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
		Cursor cursor = mBookmarksAdapter.getBookmarks();
		
		String[] from = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
		int[] to = new int[] {R.id.BookmarkRow_Title, R.id.BookmarkRow_Url};
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.bookmark_row, cursor, from, to);
		mList.setAdapter(adapter);
	}
	
}
