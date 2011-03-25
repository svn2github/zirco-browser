package org.tint.ui.activities.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tint.R;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebStorage;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WebSettingsActivity extends ListActivity {
	
	private static String sMBStored = null;
	private SiteAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (sMBStored == null) {
            //sMBStored = getString(R.string.webstorage_origin_summary_mb_stored);
			sMBStored = "sMbStored";
        }
		
		mAdapter = new SiteAdapter(this, R.layout.website_settings_row);
        setListAdapter(mAdapter);
	}
	
	static class Site {
        private String mOrigin;
        private String mTitle;
        private Bitmap mIcon;
        private int mFeatures;
        
        private final static int FEATURE_WEB_STORAGE = 0;
        private final static int FEATURE_GEOLOCATION = 1;
        
        private final static int FEATURE_COUNT = 2;
        
        public Site(String origin) {
            mOrigin = origin;
            mTitle = null;
            mIcon = null;
            mFeatures = 0;
        }
        
        public void addFeature(int feature) {
            mFeatures |= (1 << feature);
        }

        public void removeFeature(int feature) {
            mFeatures &= ~(1 << feature);
        }

        public boolean hasFeature(int feature) {
            return (mFeatures & (1 << feature)) != 0;
        }
        
        public int getFeatureCount() {
            int count = 0;
            for (int i = 0; i < FEATURE_COUNT; ++i) {
                count += hasFeature(i) ? 1 : 0;
            }
            return count;
        }
        
        public int getFeatureByIndex(int n) {
            int j = -1;
            for (int i = 0; i < FEATURE_COUNT; ++i) {
                j += hasFeature(i) ? 1 : 0;
                if (j == n) {
                    return i;
                }
            }
            return -1;
        }

        public String getOrigin() {
            return mOrigin;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public void setIcon(Bitmap icon) {
            mIcon = icon;
        }

        public Bitmap getIcon() {
            return mIcon;
        }

        public String getPrettyOrigin() {
            return mTitle == null ? null : hideHttp(mOrigin);
        }

        public String getPrettyTitle() {
            return mTitle == null ? hideHttp(mOrigin) : mTitle;
        }

        private String hideHttp(String str) {
            Uri uri = Uri.parse(str);
            return "http".equals(uri.getScheme()) ? str.substring(7) : str;
        }
	}
	
	class SiteAdapter extends ArrayAdapter<Site> {

		private int mResource;
		private LayoutInflater mInflater;
		private Bitmap mDefaultIcon;
        private Bitmap mUsageEmptyIcon;
        private Bitmap mUsageLowIcon;
        private Bitmap mUsageHighIcon;
        private Bitmap mLocationAllowedIcon;
        private Bitmap mLocationDisallowedIcon;
        private Site mCurrentSite;
		
		public SiteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			
			mResource = textViewResourceId;
			
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			mDefaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.app_web_browser_sm);
            mUsageEmptyIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_list_data_off);
            mUsageLowIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_list_data_small);
            mUsageHighIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_list_data_large);
            mLocationAllowedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_list_gps_on);
            mLocationDisallowedIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_list_gps_denied);
		}
		
		private void addFeatureToSite(Map<String, Site> sites, String origin, int feature) {
            Site site = null;
            if (sites.containsKey(origin)) {
                site = (Site) sites.get(origin);
            } else {
                site = new Site(origin);
                sites.put(origin, site);
            }
            site.addFeature(feature);
        }
		
		@SuppressWarnings("unchecked")
		public void askForOrigins() {
            // Get the list of origins we want to display.
            // All 'HTML 5 modules' (Database, Geolocation etc) form these
            // origin strings using WebCore::SecurityOrigin::toString(), so it's
            // safe to group origins here. Note that WebCore::SecurityOrigin
            // uses 0 (which is not printed) for the port if the port is the
            // default for the protocol. Eg http://www.google.com and
            // http://www.google.com:80 both record a port of 0 and hence
            // toString() == 'http://www.google.com' for both.

            WebStorage.getInstance().getOrigins(new ValueCallback<Map>() {
                public void onReceiveValue(Map origins) {
                    Map<String, Site> sites = new HashMap<String, Site>();
                    if (origins != null) {
                        Iterator<String> iter = origins.keySet().iterator();
                        while (iter.hasNext()) {
                            addFeatureToSite(sites, iter.next(), Site.FEATURE_WEB_STORAGE);
                        }
                    }
                    askForGeolocation(sites);
                }
            });
        }

		public void askForGeolocation(final Map<String, Site> sites) {
            GeolocationPermissions.getInstance().getOrigins(new ValueCallback<Set<String> >() {
                public void onReceiveValue(Set<String> origins) {
                    if (origins != null) {
                        Iterator<String> iter = origins.iterator();
                        while (iter.hasNext()) {
                            addFeatureToSite(sites, iter.next(), Site.FEATURE_GEOLOCATION);
                        }
                    }
                    populateIcons(sites);
                    populateOrigins(sites);
                }
            });
        }
		
		@SuppressWarnings("unchecked")
		public void populateIcons(Map<String, Site> sites) {
            // Create a map from host to origin. This is used to add metadata
            // (title, icon) for this origin from the bookmarks DB.
            HashMap<String, Set<Site>> hosts = new HashMap<String, Set<Site>>();
            Set<Map.Entry<String, Site>> elements = sites.entrySet();
            Iterator<Map.Entry<String, Site>> originIter = elements.iterator();
            while (originIter.hasNext()) {
                Map.Entry<String, Site> entry = originIter.next();
                Site site = entry.getValue();
                String host = Uri.parse(entry.getKey()).getHost();
                Set<Site> hostSites = null;
                if (hosts.containsKey(host)) {
                    hostSites = (Set<Site>)hosts.get(host);
                } else {
                    hostSites = new HashSet<Site>();
                    hosts.put(host, hostSites);
                }
                hostSites.add(site);
            }

            // Check the bookmark DB. If we have data for a host used by any of
            // our origins, use it to set their title and favicon
            Cursor c = getContext().getContentResolver().query(Browser.BOOKMARKS_URI,
                    new String[] { Browser.BookmarkColumns.URL, Browser.BookmarkColumns.TITLE,
                    Browser.BookmarkColumns.FAVICON }, "bookmark = 1", null, null);

            if (c != null) {
                if (c.moveToFirst()) {
                    int urlIndex = c.getColumnIndex(Browser.BookmarkColumns.URL);
                    int titleIndex = c.getColumnIndex(Browser.BookmarkColumns.TITLE);
                    int faviconIndex = c.getColumnIndex(Browser.BookmarkColumns.FAVICON);
                    do {
                        String url = c.getString(urlIndex);
                        String host = Uri.parse(url).getHost();
                        if (hosts.containsKey(host)) {
                            String title = c.getString(titleIndex);
                            Bitmap bmp = null;
                            byte[] data = c.getBlob(faviconIndex);
                            if (data != null) {
                                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            }
                            Set matchingSites = (Set) hosts.get(host);
                            Iterator<Site> sitesIter = matchingSites.iterator();
                            while (sitesIter.hasNext()) {
                                Site site = sitesIter.next();
                                // We should only set the title if the bookmark is for the root
                                // (i.e. www.google.com), as website settings act on the origin
                                // as a whole rather than a single page under that origin. If the
                                // user has bookmarked a page under the root but *not* the root,
                                // then we risk displaying the title of that page which may or
                                // may not have any relevance to the origin.
                                if (url.equals(site.getOrigin()) ||
                                        (new String(site.getOrigin()+"/")).equals(url)) {
                                    site.setTitle(title);
                                }
                                if (bmp != null) {
                                    site.setIcon(bmp);
                                }
                            }
                        }
                    } while (c.moveToNext());
                }
                c.close();
            }
        }


        public void populateOrigins(Map<String, Site> sites) {
            clear();

            // We can now simply populate our array with Site instances
            Set<Map.Entry<String, Site>> elements = sites.entrySet();
            Iterator<Map.Entry<String, Site>> entryIterator = elements.iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, Site> entry = entryIterator.next();
                Site site = entry.getValue();
                add(site);
            }

            notifyDataSetChanged();

            if (getCount() == 0) {
                finish(); // we close the screen
            }
        }
        
        public void setIconForUsage(ImageView usageIcon, long usageInBytes) {
            float usageInMegabytes = (float) usageInBytes / (1024.0F * 1024.0F);
            // We set the correct icon:
            // 0 < empty < 0.1MB
            // 0.1MB < low < 5MB
            // 5MB < high
            if (usageInMegabytes <= 0.1) {
                usageIcon.setImageBitmap(mUsageEmptyIcon);
            } else if (usageInMegabytes > 0.1 && usageInMegabytes <= 5) {
                usageIcon.setImageBitmap(mUsageLowIcon);
            } else if (usageInMegabytes > 5) {
                usageIcon.setImageBitmap(mUsageHighIcon);
            }
        }
        
        public String sizeValueToString(long bytes) {
            // We display the size in MB, to 1dp, rounding up to the next 0.1MB.
            // bytes should always be greater than zero.
            if (bytes <= 0) {
                Log.e("WebSettingsActivity", "sizeValueToString called with non-positive value: " + bytes);
                return "0";
            }
            float megabytes = (float) bytes / (1024.0F * 1024.0F);
            int truncated = (int) Math.ceil(megabytes * 10.0F);
            float result = (float) (truncated / 10.0F);
            return String.valueOf(result);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final TextView title;
            final TextView subtitle;
            final ImageView icon;
            final ImageView usageIcon;
            final ImageView locationIcon;
            final ImageView featureIcon;

            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
            } else {
                view = convertView;
            }

            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            icon = (ImageView) view.findViewById(R.id.icon);
            featureIcon = (ImageView) view.findViewById(R.id.feature_icon);
            usageIcon = (ImageView) view.findViewById(R.id.usage_icon);
            locationIcon = (ImageView) view.findViewById(R.id.location_icon);
            usageIcon.setVisibility(View.GONE);
            locationIcon.setVisibility(View.GONE);

            if (mCurrentSite == null) {
                //setTitle(getString(R.string.pref_extras_website_settings));
            	setTitle("Title1");

                Site site = getItem(position);
                title.setText(site.getPrettyTitle());
                String subtitleText = site.getPrettyOrigin();
                if (subtitleText != null) {
                    title.setMaxLines(1);
                    title.setSingleLine(true);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(subtitleText);
                } else {
                    subtitle.setVisibility(View.GONE);
                    title.setMaxLines(2);
                    title.setSingleLine(false);
                }

                icon.setVisibility(View.VISIBLE);
                usageIcon.setVisibility(View.INVISIBLE);
                locationIcon.setVisibility(View.INVISIBLE);
                featureIcon.setVisibility(View.GONE);
                Bitmap bmp = site.getIcon();
                if (bmp == null) {
                    bmp = mDefaultIcon;
                }
                icon.setImageBitmap(bmp);
                // We set the site as the view's tag,
                // so that we can get it in onItemClick()
                view.setTag(site);

                String origin = site.getOrigin();
                if (site.hasFeature(Site.FEATURE_WEB_STORAGE)) {
                    WebStorage.getInstance().getUsageForOrigin(origin, new ValueCallback<Long>() {
                        public void onReceiveValue(Long value) {
                            if (value != null) {
                                setIconForUsage(usageIcon, value.longValue());
                                usageIcon.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                if (site.hasFeature(Site.FEATURE_GEOLOCATION)) {
                    locationIcon.setVisibility(View.VISIBLE);
                    GeolocationPermissions.getInstance().getAllowed(origin, new ValueCallback<Boolean>() {
                        public void onReceiveValue(Boolean allowed) {
                            if (allowed != null) {
                                if (allowed.booleanValue()) {
                                    locationIcon.setImageBitmap(mLocationAllowedIcon);
                                } else {
                                    locationIcon.setImageBitmap(mLocationDisallowedIcon);
                                }
                            }
                        }
                    });
                }
            } else {
                icon.setVisibility(View.GONE);
                locationIcon.setVisibility(View.GONE);
                usageIcon.setVisibility(View.GONE);
                featureIcon.setVisibility(View.VISIBLE);
                setTitle(mCurrentSite.getPrettyTitle());
                String origin = mCurrentSite.getOrigin();
                switch (mCurrentSite.getFeatureByIndex(position)) {
                    case Site.FEATURE_WEB_STORAGE:
                        WebStorage.getInstance().getUsageForOrigin(origin, new ValueCallback<Long>() {
                            public void onReceiveValue(Long value) {
                                if (value != null) {
                                    String usage = sizeValueToString(value.longValue()) + " " + sMBStored;
                                    //title.setText(R.string.webstorage_clear_data_title);
                                    title.setText("Title2");
                                    subtitle.setText(usage);
                                    subtitle.setVisibility(View.VISIBLE);
                                    setIconForUsage(featureIcon, value.longValue());
                                }
                            }
                        });
                        break;
                    case Site.FEATURE_GEOLOCATION:
                        //title.setText(R.string.geolocation_settings_page_title);
                    	title.setText("Title3: Geolocalisation settings page");
                        GeolocationPermissions.getInstance().getAllowed(origin, new ValueCallback<Boolean>() {
                            public void onReceiveValue(Boolean allowed) {
                                if (allowed != null) {
                                    if (allowed.booleanValue()) {
                                        //subtitle.setText(R.string.geolocation_settings_page_summary_allowed);
                                    	subtitle.setText("Geolocalisation allowed");
                                        featureIcon.setImageBitmap(mLocationAllowedIcon);
                                    } else {
                                        //subtitle.setText(R.string.geolocation_settings_page_summary_not_allowed);
                                    	subtitle.setText("Geolocalisation not allowed");
                                        featureIcon.setImageBitmap(mLocationDisallowedIcon);
                                    }
                                    subtitle.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        break;
                }
            }

            return view;
        }
		
	}

}
