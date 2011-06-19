package org.tint.ui.activities.preferences;

import org.tint.R;
import org.tint.utils.Constants;

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
 * Spinner activity allowing to choose a search engine.
 */
public class SearchUrlPreference extends DialogPreference {
	
	private Context mContext;
	
	private Spinner mSpinner;
	private EditText mCustomEditText;
	
	/**
	 * Constructor.
	 * @param context The current context.
	 */
	public SearchUrlPreference(Context context) {
		this(context, null);
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs Attributes.
	 */
	public SearchUrlPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs Attributes.
	 * @param defStyle Styles.
	 */
	public SearchUrlPreference(Context context, AttributeSet attrs, int defStyle) {
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
		
		mSpinner.setPromptId(R.string.SearchUrlPreferenceActivity_Prompt);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.SearchUrlValues, android.R.layout.simple_spinner_item);
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
	    	editor.putString(Constants.PREFERENCES_GENERAL_SEARCH_URL, mCustomEditText.getText().toString());
	    	editor.commit();
		}
	}
	
	/**
	 * Set the Spinner current value with the current value in preferences.
	 */
	private void setSpinnerValueFromPreferences() {
		String currentSearchUrl = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.PREFERENCES_GENERAL_SEARCH_URL, Constants.URL_SEARCH_GOOGLE);
		
		if (currentSearchUrl.equals(Constants.URL_SEARCH_GOOGLE)) {
			mSpinner.setSelection(0);
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(Constants.URL_SEARCH_GOOGLE);
		} else if (currentSearchUrl.equals(Constants.URL_SEARCH_WIKIPEDIA)) {
			mSpinner.setSelection(1);
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(Constants.URL_SEARCH_WIKIPEDIA);
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
		case 0: mCustomEditText.setEnabled(false); mCustomEditText.setText(Constants.URL_SEARCH_GOOGLE); break;
		case 1: mCustomEditText.setEnabled(false); mCustomEditText.setText(Constants.URL_SEARCH_WIKIPEDIA); break;
		case 2: {
			mCustomEditText.setEnabled(true);
			
			if ((mCustomEditText.getText().toString().equals(Constants.URL_SEARCH_GOOGLE)) ||
					(mCustomEditText.getText().toString().equals(Constants.URL_SEARCH_WIKIPEDIA))) {					
				mCustomEditText.setText(null);
			}
			break;
		}
		default: mCustomEditText.setEnabled(false); mCustomEditText.setText(Constants.URL_SEARCH_GOOGLE); break;
		}
	}

}
