package com.nekosoft.alarm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.nekosoft.alarm.AlarmSetupActivity;
import com.nekosoft.alarm.R;

public class TimePickerFragment extends Fragment {
	
//	public final static String HOUR = "com.nekosoft.alarm.HOUR";
//	public final static String MINUTE = "com.nekosoft.alarm.MINUTE";
	public final static String TAG = "ALARM-timePickerFragment";
	private AlarmSetupActivity parentActivity;
	Integer mHour;
	Integer mMinute;
	TimePicker mTimePicker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate called");
		
//		if (savedInstanceState != null) {
//			Log.i(TAG, "onCreate: savedInstanceState not null, restoring mHour and mMinute");
//			mHour = savedInstanceState.getInt(HOUR);
//			mMinute = savedInstanceState.getInt(MINUTE);
//		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView called");
		View layout = inflater.inflate(R.layout.fragment_time_picker, root, false);
//		mTimePicker = (TimePicker) layout.findViewById(R.id.step1_time_picker);
//		mTimePicker = new TimePicker(getActivity());
//		mFrame = (FrameLayout) layout.findViewById(R.id.step1_time_picker_frame);
//		mFrame.addView(mTimePicker);
		
//		if (mHour != null && mMinute != null) {
//			Log.i(TAG, "onCreateView: mHour and mMinute not null, setting timepicker");
//			Log.i(TAG, String.format("mHour: %d", mHour));
//			Log.i(TAG, String.format("mMinute: %d", mMinute));
//			mTimePicker.setCurrentHour(mHour);
//			mTimePicker.setCurrentMinute(mMinute);
//		}
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "onActivityCreated called");
		
		// get Hour and Minute from parent activity, in the case of a reorient they
		// will have been saved to the instance state and reassigned during onCreate
		parentActivity = (AlarmSetupActivity) getActivity();
		mHour = parentActivity.mHour;
		mMinute = parentActivity.mMinute;
		mTimePicker = (TimePicker) parentActivity.findViewById(R.id.step1_time_picker);
		
		// if non null, a time was stored; reset the time picker to show it.
		if (mHour != null && mMinute != null) {
			Log.i(TAG, "onCreateView: mHour and mMinute not null, setting timepicker");
			Log.i(TAG, String.format("mHour: %d", mHour));
			Log.i(TAG, String.format("mMinute: %d", mMinute));
			mTimePicker.setCurrentHour(mHour);
			mTimePicker.setCurrentMinute(mMinute);
		}
	}
	
	/*
	 * Between here problem occurs: Saved cursor position 2/2 out of range for (restored) text
	 * Since it happens between log outputs, it must occur in super.onStart(). Seems to relate
	 * to the cursor in the timepicker text inputs, but seems to cause no practical consequence.
	 */

	
//	@Override
//	public void onStart() {
//		super.onStart();
//		Log.i(TAG, "onStart called");
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		Log.i(TAG, "onResume called");
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause(); // super goes first
//		Log.i(TAG, "onPause called");		
//	}
//	
//	@Override
//	public void onStop() {
//		super.onStop();
//		Log.i(TAG, "onStop called");
//	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, "onDestroyView called");
		
		// Tabbing away from this fragment, so the value of the TimePicker should be
		// stored to the parent activity.
		mTimePicker = (TimePicker) parentActivity.findViewById(R.id.step1_time_picker);
		parentActivity.mHour = mTimePicker.getCurrentHour();
		parentActivity.mMinute = mTimePicker.getCurrentMinute();
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
////		Log.i(TAG, "onSaveInstanceState called");
////		if (mTimePicker != null) {
////			mHour = mTimePicker.getCurrentHour();
////			mMinute = mTimePicker.getCurrentMinute();
////		} 
////		Log.i(TAG, String.format("TimePickerFragment onSavedInstanceState called; mHour %d, mMinute %d", mHour, mMinute));
////		if (mHour != null && mMinute != null) {
////			savedInstanceState.putInt(HOUR, mHour);
////			savedInstanceState.putInt(MINUTE, mMinute);
////		}
//		super.onSaveInstanceState(savedInstanceState);
//	}
	
}
