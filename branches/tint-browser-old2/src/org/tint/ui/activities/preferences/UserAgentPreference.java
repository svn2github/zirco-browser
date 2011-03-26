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
 * Spinner activity allowing to choose an user-agent.
 */
public class UserAgentPreference extends DialogPreference {
	
	private Context mContext;
	
	private Spinner mSpinner;
	private EditText mCustomEditText;
	
	/**
	 * Constructor.
	 * @param context The current context.
	 */
	public UserAgentPreference(Context context) {
		this(context, null);
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs Attributes.
	 */
	public UserAgentPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs Attributes.
	 * @param defStyle Styles.
	 */
	public UserAgentPreference(Context context, AttributeSet attrs, int defStyle) {
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
		
		mSpinner.setPromptId(R.string.UserAgentPreferenceActivity_PromptTitle);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.UserAgentValues, android.R.layout.simple_spinner_item);
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
	    	editor.putString(Constants.PREFERENCES_BROWSER_USER_AGENT, mCustomEditText.getText().toString());
	    	editor.commit();
		}
	}
	
	/**
	 * Set the Spinner current value with the current value in preferences.
	 */
	private void setSpinnerValueFromPreferences() {
		String currentUserAgent = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.PREFERENCES_BROWSER_USER_AGENT, Constants.USER_AGENT_DEFAULT);
		
		if (currentUserAgent.equals(Constants.USER_AGENT_DEFAULT)) {
			mSpinner.setSelection(0);
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(Constants.USER_AGENT_DEFAULT);
		} else if (currentUserAgent.equals(Constants.USER_AGENT_DESKTOP)) {
			mSpinner.setSelection(1);
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(Constants.USER_AGENT_DESKTOP);
		} else {
			mSpinner.setSelection(2);
			mCustomEditText.setEnabled(true);
			mCustomEditText.setText(currentUserAgent);					
		}
	}
	
	/**
	 * Update UI when spinner selection changes.
	 * @param position The new spinner selection.
	 */
	protected void onSpinnerItemSelected(int position) {
		switch (position) {
		case 0: mCustomEditText.setEnabled(false); mCustomEditText.setText(Constants.USER_AGENT_DEFAULT); break;
		case 1: mCustomEditText.setEnabled(false); mCustomEditText.setText(Constants.USER_AGENT_DESKTOP); break;
		case 2: {
			mCustomEditText.setEnabled(true);
			
			if ((mCustomEditText.getText().toString().equals(Constants.USER_AGENT_DEFAULT)) ||
					(mCustomEditText.getText().toString().equals(Constants.USER_AGENT_DESKTOP))) {					
				mCustomEditText.setText(null);
			}
			break;
		}
		default:
			mCustomEditText.setEnabled(false);
			mCustomEditText.setText(Constants.USER_AGENT_DEFAULT);
			break;
		}
	}	

}
