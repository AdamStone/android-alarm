package com.nekosoft.alarm.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.nekosoft.alarm.R;
import com.nekosoft.alarm.fragments.AudioSelectionFragment;
import com.nekosoft.alarm.fragments.BarcodeSetupFragment;
import com.nekosoft.alarm.fragments.TimePickerFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
	private Context mContext;

	public TabsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return new TimePickerFragment();
			case 1:
				return new BarcodeSetupFragment();				
			case 2:
				Log.i("ALARM-TabsPagerAdapter", "returning NEW AudioSelectionFragment");
				return new AudioSelectionFragment();
//				return new BlankFragment();
			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		// make sure this matches the number of tabs
		return 3;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return mContext.getString(R.string.step1_tab);
		case 1:
			return mContext.getString(R.string.step2_tab);
		case 2:
			return mContext.getString(R.string.step3_tab);
		default:
			return "";
		}
	}

}
