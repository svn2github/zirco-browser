package org.zirco2.adapters;

import java.util.ArrayList;
import java.util.List;

import org.zirco2.R;
import org.zirco2.controllers.TabsController;
import org.zirco2.controllers.WebViewContainer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.os.Build;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class WebViewsImageAdapter extends BaseAdapter {
	
    int mGalleryItemBackground;
    private Context mContext;
    
    private float mHeightFactor = 0.6f;
    private float mWidthFactor = 0.6f;
    
    private int mThumbHeight;
    private int mThumbWidth;
    
    private List<Bitmap> mBitmaps;    

    public WebViewsImageAdapter(Context c) {
        mContext = c;
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.TestGallery);
        mGalleryItemBackground = a.getResourceId(R.styleable.TestGallery_android_galleryItemBackground, 0);
        a.recycle();
        
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        int rotation;
        if (Build.VERSION.SDK_INT <= 7) {
        	rotation = display.getOrientation();
        } else {
        	rotation = display.getRotation();
        }
        
        switch (rotation) {
        case Surface.ROTATION_0:
        case Surface.ROTATION_180:
        	mHeightFactor = 0.6f;
        	mWidthFactor = 0.6f;
        	break;
        case Surface.ROTATION_90:
        case Surface.ROTATION_270:
        	mHeightFactor = 0.5f;
        	mWidthFactor = 0.6f;
        	break;        	
        }
        
        mThumbHeight = (int) (mHeightFactor * mContext.getResources().getDisplayMetrics().heightPixels);
		mThumbWidth = (int) (mWidthFactor * mContext.getResources().getDisplayMetrics().widthPixels);
        
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
    	Picture thumbnail = webView.capturePicture();				    	
    	
		Bitmap bm = Bitmap.createBitmap(mThumbWidth, mThumbHeight, Bitmap.Config.ARGB_4444);
		
		Canvas canvas = new Canvas(bm);
		
		if ((thumbnail != null) &&
				(thumbnail.getWidth() > 0)) {			
			float widthFactor = (float) mThumbWidth / (float) thumbnail.getWidth();
			canvas.scale(widthFactor, widthFactor);
			thumbnail.draw(canvas);
		} else {
			// No image for this WebView, draw a white rectangle.
			Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
			p.setColor(0xFFFFFFFF);
			canvas.drawRect(new RectF(0, 0, mThumbWidth, mThumbHeight), p);
		}
				
		return bm;
    }
    
}