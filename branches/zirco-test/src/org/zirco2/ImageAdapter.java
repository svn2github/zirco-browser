package org.zirco2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
    int mGalleryItemBackground;
    private Context mContext;
    
    private int mThumbHeight;
    private int mThumbWidth;
    
    private List<Bitmap> mBitmaps;    

    public ImageAdapter(Context c) {
        mContext = c;
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.TestGallery);
        mGalleryItemBackground = a.getResourceId(R.styleable.TestGallery_android_galleryItemBackground, 0);
        a.recycle();        
        
        mThumbHeight = (int) (0.66f * mContext.getResources().getDisplayMetrics().heightPixels);
		mThumbWidth = (int) (0.66f * mContext.getResources().getDisplayMetrics().widthPixels);
        
        mBitmaps = new ArrayList<Bitmap>();
        
        List<WebViewContainer> webViewContainers = TabsController.getInstance().getWebViewContainers();
        for (WebViewContainer webViewContainer : webViewContainers) {
        	mBitmaps.add(getWebWiewScreenShot(webViewContainer.getWebView()));
        }
    }

    public int getCount() {
        return mBitmaps.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);

        i.setImageBitmap(mBitmaps.get(position));
        
        i.setLayoutParams(new Gallery.LayoutParams(mThumbWidth, mThumbHeight));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(mGalleryItemBackground);

        return i;
    }
    
    private Bitmap getWebWiewScreenShot(WebView webView) {
    	webView.postInvalidate();
    	Picture thumbnail = webView.capturePicture();
		if (thumbnail == null) {
			return null;
		}
		
		Bitmap bm = Bitmap.createBitmap(mThumbWidth,	mThumbHeight, Bitmap.Config.ARGB_4444);
		
		Canvas canvas = new Canvas(bm);
		
		if (thumbnail.getWidth() > 0) {			
			float widthFactor = (float) mThumbWidth / (float) thumbnail.getWidth();
			canvas.scale(widthFactor, widthFactor);
		}
		
		thumbnail.draw(canvas);
		return bm;
    }
    
}