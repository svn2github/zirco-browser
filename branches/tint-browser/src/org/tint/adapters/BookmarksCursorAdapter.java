package org.tint.adapters;

import org.tint.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.Browser;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class BookmarksCursorAdapter extends SimpleCursorAdapter {

	public BookmarksCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View superView = super.getView(position, convertView, parent);
		
		ImageView thumbnailView = (ImageView) superView.findViewById(R.id.BookmarkRow_Thumbnail);
		
		byte[] favicon = getCursor().getBlob(getCursor().getColumnIndex(Browser.BookmarkColumns.FAVICON));
		if (favicon != null) {
			thumbnailView.setImageBitmap(BitmapFactory.decodeByteArray(favicon, 0, favicon.length));
		} else {
			thumbnailView.setImageResource(R.drawable.fav_icn_unknown);
		}
		
		return superView;
	}

}
