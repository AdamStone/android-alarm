package com.nekosoft.alarm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nekosoft.alarm.AlarmSetupActivity;
import com.nekosoft.alarm.R;

public class BarcodeSetupFragment extends Fragment {
	
	String barcode;
	AlarmSetupActivity parentActivity;
	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}

	@Override
	public View onCreateView(
			LayoutInflater inflater,
			ViewGroup root,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_barcode_setup, root, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		parentActivity = (AlarmSetupActivity) getActivity();
		barcode = parentActivity.barcode;
		TextView result = (TextView) parentActivity.findViewById(R.id.step2_scan_result);
		if (barcode != null) {
//			String resultPhrase = parentActivity.getResources().getString(R.string.step2_scan_result);
//			result.setText(String.format(resultPhrase, result));
			parentActivity.updateDisplayedBarcode(barcode);
		}
	}
}
