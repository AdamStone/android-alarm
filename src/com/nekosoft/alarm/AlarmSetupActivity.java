package com.nekosoft.alarm;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nekosoft.alarm.adapters.AlbumBrowserAdapter;
import com.nekosoft.alarm.adapters.TabsPagerAdapter;

public class AlarmSetupActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private TabsPagerAdapter mTabsPagerAdapter;
	private ViewPager mViewPager;
	public AlbumBrowserAdapter mAlbumAdapter;
	public Cursor mAlbumCursor;
	
	public TimePicker mTimePicker;
	public Integer mHour;
	public Integer mMinute;
	public String barcode = "00000000";
	public SparseBooleanArrayParcelable[] mSongSelection;
	
	public static final int SET_BARCODE_REQUEST_CODE = 0x0000c0de;
	public static final int CHECK_BARCODE_REQUEST_CODE = 0x0000c0df;

	//// STRINGS
	private final static String TAG = "ALARM-setupNewAlarmActivity";	
	
	// setup bundle
//	public final static String SETUP_BUNDLE = "com.nekosoft.alarm.SETUP_BUNDLE";
	
	// timepicker
//	public final static String HOUR = "com.nekosoft.alarm.HOUR";
//	public final static String MINUTE = "com.nekosoft.alarm.MINUTE";
	
	// barcode
//	public final static String BARCODE = "com.nekosoft.alarm.BARCODE";
	
	// audio selection
//	public final static String SELECTION = "com.nekosoft.alarm.SELECTION";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate CALLED FOR ALARMSETUPACTIVITY");
		setContentView(R.layout.activity_setup_new_alarm);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mTabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mTabsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mTabsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mTabsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		
		// SAVED INSTANCE STATE
		if (savedInstanceState != null) {
			
			// get setupBundle (when editing existing alarm)
//			setupBundle = savedInstanceState.getBundle(SETUP_BUNDLE);
		
			// get stored mHour and mMinute
			mHour = savedInstanceState.getInt(AlarmModel.HOUR);
			mMinute = savedInstanceState.getInt(AlarmModel.MINUTE);
			
			// get stored barcode
			barcode = savedInstanceState.getString(AlarmModel.BARCODE);
			
			// get stored audio selection (on recreating)
			mSongSelection = (SparseBooleanArrayParcelable[]) savedInstanceState.getParcelableArray(AlarmModel.AUDIO);
			
			Parcelable[] songSelectionParcelable = savedInstanceState.getParcelableArray(AlarmModel.AUDIO);
			mSongSelection = new SparseBooleanArrayParcelable[songSelectionParcelable.length];
			for (int i=0; i<songSelectionParcelable.length; i++) {
				mSongSelection[i] = (SparseBooleanArrayParcelable) songSelectionParcelable[i];
			}
						
		}
		
		
		// ALBUM ADAPTER
		if (mAlbumAdapter != null) {
			Log.i(TAG, "mAlbumAdapter NOT NULL");
		}
		else {
			Log.i(TAG, "mAlbumAdapter IS NULL; creating new");
			
			// Get album/song cursor for album selection
			if (mAlbumCursor != null) {
				Log.i(TAG, "mAlbumCursor NOT NULL");
			}
			else {
				Log.i(TAG, "mAlbumCursor IS NULL; creating new");
				mAlbumCursor = getAlbumCursor();
			}			
			
			if (mSongSelection != null) {
				Log.i(TAG, "mSongSelection from savedInstanceState is NOT NULL");
				
				// just logging...
				for (int i=0; i < mSongSelection.length; i++) {
					if (mSongSelection[i].size() > 0) {
						Log.i(TAG, String.format("Album %d has %d tracks selected", i, mSongSelection[i].size()));
					}
				}
				
				// apply to new adapter
				mAlbumAdapter = new AlbumBrowserAdapter(mAlbumCursor, this, mSongSelection);
				
				
			}
			else {
				Log.i(TAG, "mSongSelection from savedInstanceState is NULL");
				
				// make new blank adapter
				mAlbumAdapter = new AlbumBrowserAdapter(mAlbumCursor, this);
			}
						
			
			
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i(TAG, "onDestroy called");
		
		// CLOSE ADAPTER CURSOR
		if (mAlbumAdapter != null) {
			if (mAlbumAdapter.mAlbumCursor != null) {
				mAlbumAdapter.mAlbumCursor.close();
				Log.i(TAG, "(CLOSING CURSOR)");
				
			}
			else {
				Log.i(TAG, "mAlbumAdapter.mAlbumCursor is NULL");
			}
		}
		else {
			Log.i(TAG, "mAlbumAdapter is NULL");
		}
		
	}
	
	
	//// MENUS AND TABS

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_new_alarm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
		
		case R.id.action_settings:
			// TODO implement settings action
			return true;
			
		case R.id.action_save:
			
			boolean ready = true;
			mSongSelection = mAlbumAdapter.mSongSelection;
			
			// check if barcode was scanned
			if (!(barcode != null)) {
				Toast.makeText(this, R.string.toast_scan, Toast.LENGTH_LONG).show();
				Log.i(TAG, "SAVE not ready; barcode null");
				ready = false;
			}
			
			// check if any audio was selected using check state
			if (mSongSelection != null) {
				boolean selectionMade = false;
				for (int i=0; i < mSongSelection.length; i++) {
					if (mSongSelection[i].size() > 0) {
						Log.i(TAG, String.format("Album %d has %d tracks selected", i, mSongSelection[i].size()));
						selectionMade = true;
					}
				}
				if (!selectionMade) {
					Toast.makeText(this, R.string.toast_audio, Toast.LENGTH_LONG).show();
					Log.i(TAG, "SAVE not ready; audio not selected");
					ready = false;
				}
			}
			else {
				Log.i(TAG, "SAVE not ready; mSongSelection null");
				ready = false;
			}
			
			// save alarm if ready
			if (ready) {
				Log.i(TAG, "SAVING ALARM");
				saveAlarm();
				
				return true;
			}
			else {
				Log.i(TAG, "NOT SAVING ALARM");
				return false;
			}

			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	
	//// FUNCTIONALITY
	
	public void setBarcode(View view) {
		Log.i(TAG, "setBarcode called");
		IntentIntegrator integrator = new IntentIntegrator(this, SET_BARCODE_REQUEST_CODE);
		Log.i(TAG, "initiating scan");
		integrator.initiateScan();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanResult != null) {
			// handle barcode scan result
			
			barcode = scanResult.getContents();
			
			if (barcode != null && requestCode == SET_BARCODE_REQUEST_CODE) {
				Log.i(TAG, String.format("Scan result acquired:%s", barcode));
				
				// update layout
				updateDisplayedBarcode(barcode);
			}
		}
	}
	
	public void updateDisplayedBarcode(String barcode) {
		Button scanButton = (Button) findViewById(R.id.step2_button_scan);
		TextView scanResult = (TextView) findViewById(R.id.step2_scan_result);
		
		String buttonText = getResources().getString(R.string.button_rescan);
		scanButton.setText(buttonText); 
		
		String resultText = getResources().getString(R.string.step2_scan_result);
		scanResult.setText(String.format(resultText, barcode));
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		mTimePicker = (TimePicker) findViewById(R.id.step1_time_picker);
		
		Log.i(TAG, "onSaveInstanceState called for AlarmSetupActivity");
		if (mTimePicker != null) {
			mHour = mTimePicker.getCurrentHour();
			mMinute = mTimePicker.getCurrentMinute();
		} 
		Log.i(TAG, String.format("TimePickerFragment onSavedInstanceState called; mHour %d, mMinute %d", mHour, mMinute));
		if (mHour != null && mMinute != null) {
			savedInstanceState.putInt(AlarmModel.HOUR, mHour);
			savedInstanceState.putInt(AlarmModel.MINUTE, mMinute);
		}
		
		if (barcode != null) {
			savedInstanceState.putString(AlarmModel.BARCODE, barcode);
		}
		
		// put audio selection state
		
		mSongSelection = mAlbumAdapter.mSongSelection;
		
		if (mSongSelection != null) {
			// mSongSelection is an Array of SparseBooleanArrays, one for each album
			
			// just logging...
			for (int i=0; i < mSongSelection.length; i++) {
				if (mSongSelection[i].size() > 0) {
					Log.i(TAG, String.format("Album %d has %d tracks selected", i, mSongSelection[i].size()));
				}
			}
			
			Log.i(TAG, "Saving selection to savedInstanceState");
			savedInstanceState.putParcelableArray(AlarmModel.AUDIO, mSongSelection);
			
		}
		else {
			Log.i(TAG, "mSongSelection is NULL");
		}
	}
	
	private void saveAlarm() {
		// build intent with alarm data
		Intent data = new Intent();
		
		// put data into intent
		data.putExtra(AlarmModel.HOUR, mHour);
		data.putExtra(AlarmModel.MINUTE, mMinute);
		data.putExtra(AlarmModel.BARCODE, barcode);
		data.putExtra(AlarmModel.AUDIO, mSongSelection);
		
		// set result and finish
		this.setResult(RESULT_OK, data);
		finish();
	}
	
	private Cursor getAlbumCursor() {
		Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		String[] projection = {
			MediaStore.Audio.Albums._ID,
			MediaStore.Audio.Albums.ARTIST,
			MediaStore.Audio.Albums.ALBUM,
			MediaStore.Audio.Albums.NUMBER_OF_SONGS
		};
		return getContentResolver().query(uri, projection, null, null, null);
	}

}
