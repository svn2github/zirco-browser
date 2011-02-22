package org.tint.adapters;

import org.tint.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.Browser;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class UrlSuggestionCursorAdapter extends SimpleCursorAdapter {

	public UrlSuggestionCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View superView = super.getView(position, convertView, parent);
		
		ImageView iconView = (ImageView) superView.findViewById(R.id.AutocompleteImageView);
		
		int resultType = getCursor().getInt(getCursor().getColumnIndex(Browser.BookmarkColumns.BOOKMARK));
		switch (resultType) {
		case 0: iconView.setImageResource(R.drawable.ic_tab_history_unselected); break;
		case 1: iconView.setImageResource(R.drawable.ic_tab_bookmarks_unselected); break;
		}
		
		return superView;
	}

}
