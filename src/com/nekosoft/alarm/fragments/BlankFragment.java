package com.nekosoft.alarm.fragments;

import com.nekosoft.alarm.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BlankFragment extends Fragment {

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		 super.onCreate(savedInstanceState); // must call super
//	}
	
	@Override
	public View onCreateView(
			LayoutInflater inflater,
			ViewGroup root,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.blank_fragment, root, false);
	}
	
}
