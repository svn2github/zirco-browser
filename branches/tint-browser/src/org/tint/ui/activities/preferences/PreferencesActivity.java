package org.tint.ui.activities.preferences;

import java.util.List;

import org.tint.R;
import org.tint.controllers.BookmarksHistoryController;
import org.tint.controllers.TabsController;
import org.tint.runnables.XmlHistoryBookmarksExporter;
import org.tint.runnables.XmlHistoryBookmarksImporter;
import org.tint.ui.activities.AboutActivity;
import org.tint.utils.ApplicationUtils;
import org.tint.utils.Constants;
import org.tint.utils.IOUtils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.webkit.CookieManager;

/**
 * The preferences activity.
 */
public class PreferencesActivity extends PreferenceActivity {
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.preferences_activity);
		
		Preference aboutPref = (Preference) findPreference("About");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(PreferencesActivity.this, AboutActivity.class);
				startActivity(i);
				return true;
			}
		});
		
		Preference clearHistoryPref = (Preference) findPreference("PrivacyClearHistory");
		clearHistoryPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				clearHistory();
				return true;
			}			
		});
		
		Preference clearformDataPref = (Preference) findPreference("PrivacyClearFormData");
		clearformDataPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				clearFormData();
				return true;
			}			
		});
		
		Preference clearCachePref = (Preference) findPreference("PrivacyClearCache");
		clearCachePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				clearCache();
				return true;
			}			
		});
		
		Preference clearCookiesPref = (Preference) findPreference("PrivacyClearCookies");
		clearCookiesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				clearCookies();
				return true;
			}			
		});
		
		Preference exportHistoryBookmarksPref = (Preference) findPreference("ExportHistoryBookmarks");
		exportHistoryBookmarksPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				exportHistoryBookmarks();
				return true;
			}			
		});
		
		Preference importHistoryBookmarksPref = (Preference) findPreference("ImportHistoryBookmarks");
		importHistoryBookmarksPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				importHistoryBookmarks();
				return true;
			}			
		});		
		
		Preference fullScreenPref = (Preference) findPreference(Constants.PREFERENCES_GENERAL_FULL_SCREEN);
		fullScreenPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				askForRestart();
				return true;
			}
		});
		
		Preference titleBarsPref = (Preference) findPreference(Constants.PREFERENCES_GENERAL_HIDE_TITLE_BARS);
		titleBarsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				askForRestart();
				return true;
			}
		});
		
		PreferenceScreen test = (PreferenceScreen) findPreference("Test");
		Intent testIntent = new Intent(this, WebSettingsActivity.class);
		test.setIntent(testIntent);
	}
	
	/**
	 * Ask user to restart the app. Do it if click on "Yes".
	 */
	private void askForRestart() {
		ApplicationUtils.showYesNoDialog(this,
				android.R.drawable.ic_dialog_alert,
				R.string.PreferencesActivity_RestartDialogTitle,
				R.string.PreferencesActivity_RestartDialogMessage,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				PendingIntent intent = PendingIntent.getActivity(PreferencesActivity.this.getBaseContext(), 0, new Intent(getIntent()), getIntent().getFlags());
				AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, intent);
				System.exit(2);
			}
			
		});
	}	
	
	/**
	 * Import the given file to bookmarks and history.
	 * @param fileName The file to import.
	 */
	private void doImportHistoryBookmarks(String fileName) {
		
		if (ApplicationUtils.checkCardState(this, true)) {
			mProgressDialog = ProgressDialog.show(this,
	    			this.getResources().getString(R.string.Commons_PleaseWait),
	    			this.getResources().getString(R.string.Commons_ImportingHistoryBookmarks));
			
			XmlHistoryBookmarksImporter importer = new XmlHistoryBookmarksImporter(this, fileName, mProgressDialog);
			new Thread(importer).start();
		}
		
	}
	
	/**
	 * Ask the user the file to import to bookmarks and history, and launch the import. 
	 */
	private void importHistoryBookmarks() {
		List<String> exportedFiles = IOUtils.getExportedBookmarksFileList();    	
    	
    	final String[] choices = exportedFiles.toArray(new String[exportedFiles.size()]);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setInverseBackgroundForced(true);
    	builder.setIcon(android.R.drawable.ic_dialog_info);
    	builder.setTitle(getResources().getString(R.string.Commons_ImportHistoryBookmarksSource));
    	builder.setSingleChoiceItems(choices,
    			0,
    			new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
								
				doImportHistoryBookmarks(choices[which]);				
				
				dialog.dismiss();				
			}    		
    	});    	
    	
    	builder.setCancelable(true);
    	builder.setNegativeButton(R.string.Commons_Cancel, null);
    	
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	/**
	 * Export the bookmarks and history.
	 */
	private void doExportHistoryBookmarks() {
		if (ApplicationUtils.checkCardState(this, true)) {
			mProgressDialog = ProgressDialog.show(this,
	    			this.getResources().getString(R.string.Commons_PleaseWait),
	    			this.getResources().getString(R.string.Commons_ExportingHistoryBookmarks));
			
			XmlHistoryBookmarksExporter exporter = new XmlHistoryBookmarksExporter(this,
					IOUtils.getNowForFileName() + ".xml",
					BookmarksHistoryController.getInstance().getAllRecords(this),
					mProgressDialog);
			
			new Thread(exporter).start();
		}
	}
	
	/**
	 * Ask the user to confirm the export. Launch it if confirmed.
	 */
	private void exportHistoryBookmarks() {
		ApplicationUtils.showYesNoDialog(this,
				android.R.drawable.ic_dialog_info,
				R.string.Commons_HistoryBookmarksExportSDCardConfirmation,
				R.string.Commons_OperationCanBeLongMessage,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doExportHistoryBookmarks();
					}			
		});
	}
	
	/**
	 * Clear the history.
	 */
	private void clearHistory() {
		ApplicationUtils.showYesNoDialog(this,
				android.R.drawable.ic_dialog_alert,
				R.string.Commons_ClearHistory,
				R.string.Commons_NoUndoMessage,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//doClearHistory();
					}			
		});
	}
	
	/**
	 * Clear form data.
	 */
	private void doClearFormData() {
		mProgressDialog = ProgressDialog.show(this,
    			this.getResources().getString(R.string.Commons_PleaseWait),
    			this.getResources().getString(R.string.Commons_ClearingFormData));
    	
    	new FormDataClearer();
	}
	
	/**
	 * Ask confirmation to clear form data.
	 */
	private void clearFormData() {
		ApplicationUtils.showYesNoDialog(this,
				android.R.drawable.ic_dialog_alert,
				R.string.Commons_ClearFormData,
				R.string.Commons_NoUndoMessage,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doClearFormData();
					}			
		});
	}
	
	/**
	 * Clear the cache.
	 */
	private void doClearCache() {
		mProgressDialog = ProgressDialog.show(this,
    			this.getResources().getString(R.string.Commons_PleaseWait),
    			this.getResources().getString(R.string.Commons_ClearingCache));
    	
    	new CacheClearer();
	}
	
	/**
	 * Ask confirmation to clear cache.
	 */
	private void clearCache() {
		ApplicationUtils.showYesNoDialog(this,
				android.R.drawable.ic_dialog_alert,
				R.string.Commons_ClearCache,
				R.string.Commons_NoUndoMessage,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doClearCache();
					}			
		});
	}
	
	/**
	 * Clear the cookies.
	 */
	private void doClearCookies() {
		mProgressDialog = ProgressDialog.show(this,
    			this.getResources().getString(R.string.Commons_PleaseWait),
    			this.getResources().getString(R.string.Commons_ClearingCookies));
    	
    	new CookiesClearer();
	}
	
	/**
	 * Ask confirmation to clear cookies.
	 */
	private void clearCookies() {
		ApplicationUtils.showYesNoDialog(this,
				android.R.drawable.ic_dialog_alert,
				R.string.Commons_ClearCookies,
				R.string.Commons_NoUndoMessage,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						doClearCookies();
					}			
		});
	}
	
	/**
	 * Base class for all clear operations launched as Runnable.
	 */
	private abstract class AbstractClearer implements Runnable {

		/**
		 * Constructor. Launch itself as a Thread.
		 */
		public AbstractClearer() {
			new Thread(this).start();
		}
		
		protected Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				mProgressDialog.dismiss();
			}
		};
	}
	
	/**
	 * Runnable to clear form data.
	 */
	private class FormDataClearer extends AbstractClearer {
		
		@Override
		public void run() {
			TabsController.getInstance().clearFormData();
			
			mHandler.sendEmptyMessage(0);
		}		
	}
	
	/**
	 * Runnable to clear cache.
	 */
	private class CacheClearer extends AbstractClearer {

		@Override
		public void run() {
			TabsController.getInstance().clearCache();
			
			mHandler.sendEmptyMessage(0);
		}		
	}
	
	/**
	 * Runnable to clear cookies.
	 */
	private class CookiesClearer extends AbstractClearer {

		@Override
		public void run() {
			CookieManager.getInstance().removeAllCookie();
			
			mHandler.sendEmptyMessage(0);
		}		
	}
}
