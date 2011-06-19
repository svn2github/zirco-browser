package org.tint.ui.activities.preferences;

import org.tint.R;
import org.tint.utils.Constants;
import org.tint.utils.UrlUtils;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Spinner activity allowing to choose an home page.
 */
public class StartPagePreference extends DialogPreference {
	
	private Context mContext;
	
	private Spinner mSpinner;
	private EditText mCustomEditText;
	
	/**
	 * Constructor.
	 * @param context The current context.
	 */
	public StartPagePreference(Context context) {
		this(context, null);
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs Attributes.
	 */
	public StartPagePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs Attributes.
	 * @param defStyle Styles.
	 */
	public StartPagePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }
	
	/**
	 * Initialize the preference.
	 * @param context The current context.
	 */
	private void initialize(Context context) {
		mContext = context;
		setPersistent(false);
		setDialogLayoutResource(R.layout.spinner_dialog_preference);
	}
	
	@Override
	protected void onBindDialogView(View view) {		
		super.onBindDialogView(view);
		
		mSpinner = (Spinner) view.findViewById(R.id.BaseSpinnerCustomPreferenceSpinner);
		mCustomEditText = (EditText) view.findViewById(R.id.BaseSpinnerCustomPreferenceEditText);		
		
		mSpinner.setPromptId(R.string.HomepagePreference_Prompt);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.HomepageValues, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		mSpinner.setAdapter(adapter);				
		
		setSpinnerValueFromPreferences();
		
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				onSpinnerItemSelected(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		if (positiveResult) {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
	    	editor.putString(Constants.PREFERENCES_GENERAL_HOME_PAGE, mCustomEditText.getText().toString());
	    	editor.commit();
		}
	}
	
	/**
	 * Set the Spinner current value with the current value in preferences.
	 */
	private void setSpinnerValueFromPreferences() {
		String currentSearchUrl = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.PREFERENCES_GENERAL_HOME_PAGE, UrlUtils.URL_ABOUT_START);
		
		if (currentSearchUrl.equals(UrlUtils.URL_ABOUT_START)) {
			mSpinner.setSelection(0);
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(UrlUtils.URL_ABOUT_START);
		} else if (currentSearchUrl.equals(UrlUtils.URL_ABOUT_BLANK)) {
			mSpinner.setSelection(1);
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(UrlUtils.URL_ABOUT_BLANK);
		} else {
			mSpinner.setSelection(2);
			mCustomEditText.setEnabled(true);
			mCustomEditText.setText(currentSearchUrl);					
		}
	}
	
	/**
	 * Update UI when spinner selection changes.
	 * @param position The new spinner selection.
	 */
	private void onSpinnerItemSelected(int position) {
		switch (position) {
		case 0: mCustomEditText.setEnabled(false); mCustomEditText.setText(UrlUtils.URL_ABOUT_START); break;
		case 1: mCustomEditText.setEnabled(false); mCustomEditText.setText(UrlUtils.URL_ABOUT_BLANK); break;
		case 2: {
			mCustomEditText.setEnabled(true);
			
			if ((mCustomEditText.getText().toString().equals(UrlUtils.URL_ABOUT_START)) ||
					(mCustomEditText.getText().toString().equals(UrlUtils.URL_ABOUT_BLANK))) {					
				mCustomEditText.setText(null);
			}
			break;
		}
		default: mCustomEditText.setEnabled(false); mCustomEditText.setText(UrlUtils.URL_ABOUT_START); break;
		}
	}

}
