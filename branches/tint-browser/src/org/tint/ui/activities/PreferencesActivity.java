package org.tint.ui.activities;

import org.tint.R;
import org.tint.utils.Constants;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceClickListener;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.preferences_activity);
		
		PreferenceCategory browserPreferenceCategory = (PreferenceCategory) findPreference("BrowserPreferenceCategory");
		Preference enablePluginsEclair = (Preference) findPreference(Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS_ECLAIR);
		Preference enablePlugins = (Preference) findPreference(Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS);
		
		if (Build.VERSION.SDK_INT <= 7) {
			browserPreferenceCategory.removePreference(enablePlugins);
		} else {
			browserPreferenceCategory.removePreference(enablePluginsEclair);
		}
		
		Preference aboutPref = (Preference) findPreference("About");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(PreferencesActivity.this, AboutActivity.class);
				startActivity(i);
				return true;
			}
		});
	}

}
