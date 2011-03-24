package org.tint.ui.activities.preferences;

import org.tint.R;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class TestDialogPreference extends DialogPreference {
	
	protected Context mContext;
	
	protected Spinner mSpinner;
	protected EditText mCustomEditText;
	
	public TestDialogPreference(Context context) {
		this(context, null);
	}
	
	public TestDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public TestDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }
	
	private void initialize(Context context) {
		mContext = context;
		setPersistent(false);
		setDialogLayoutResource(R.layout.base_spinner_custom_preference_activity);
	}

	@Override
	protected void onBindDialogView(View view) {		
		super.onBindDialogView(view);
		
		mSpinner = (Spinner) view.findViewById(R.id.BaseSpinnerCustomPreferenceSpinner);
		mCustomEditText = (EditText) view.findViewById(R.id.BaseSpinnerCustomPreferenceEditText);		
		
		//mSpinner.setPromptId(getSpinnerPromptId());
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.UserAgentValues, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		mSpinner.setAdapter(adapter);				
		
		//setSpinnerValueFromPreferences();
		
		/*
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				onSpinnerItemSelected(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		*/
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		if (positiveResult) {
			
		}
	}

}
