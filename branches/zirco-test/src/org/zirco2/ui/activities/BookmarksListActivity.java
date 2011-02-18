package org.zirco2.ui.activities;

import org.zirco2.R;
import org.zirco2.adapters.BookmarksAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
