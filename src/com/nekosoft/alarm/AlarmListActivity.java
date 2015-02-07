package com.nekosoft.alarm;

import java.net.URI;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

public class AlarmListActivity extends ListActivity {
	
	public final static int REQUEST_SETUP_NEW_ALARM = 1;
	public final static String TAG = "ALARM-AlarmListActivity";
//	private final static String PREFS_MAIN_KEY = "com.nekosoft.alarm.prefs_main";
//	private final static String PREFS_MAIN_NALARMS = "com.nekosoft.alarm.prefs_main_nalarms";
//	private final static String PREFS_ALARMS_KEY = "com.nekosoft.alarm.prefs_alarm_";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_alarm_list); // NOT NEEDED if just using the full-screen list view
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
				
			case R.id.action_setupNewAlarm:
				// start new alarm setup activity
				Intent intent = new Intent(this, AlarmSetupActivity.class);				
				startActivityForResult(intent, REQUEST_SETUP_NEW_ALARM);
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SETUP_NEW_ALARM && resultCode == RESULT_OK) {
			// TODO implement results
			
			int hour = data.getIntExtra(AlarmModel.HOUR, 0);
			int minute = data.getIntExtra(AlarmModel.MINUTE, 0);
			String barcode = data.getStringExtra(AlarmModel.BARCODE);
			
			// array cast doesn't work, but can iterate and cast individually
			Parcelable[] songSelectionParcelable = data.getParcelableArrayExtra(AlarmModel.AUDIO);
			SparseBooleanArrayParcelable[] songSelection = new SparseBooleanArrayParcelable[songSelectionParcelable.length];
			for (int i=0; i<songSelectionParcelable.length; i++) {
				songSelection[i] = (SparseBooleanArrayParcelable) songSelectionParcelable[i];
			}
			
			Log.i(TAG, String.format("Obtained saved alarm with hour %d, minute %d, barcode %s", hour, minute, barcode));
			
			// TODO save to persistent alarm
			
//			SharedPreferences mainPref = getSharedPreferences(PREFS_MAIN_KEY, Context.MODE_PRIVATE);
//			int nAlarms = mainPref.getInt(PREFS_MAIN_NALARMS, 0);
//			
//			SharedPreferences newAlarmPref = getSharedPreferences(PREFS_ALARMS_KEY + "", Context.MODE_PRIVATE);
//			SharedPreferences.Editor mainEditor = mainPref.edit();

			// get URIs of audio selection
			for (int i=0; i<songSelection.length; i++) {
				SparseBooleanArray album = songSelection[i];
				Log.i(TAG, String.format("album.size() is %d", album.size()));
				if (album.size() > 0) {
					int size = album.size();
					for (int j=0; j<size; j++) {
						if (album.valueAt(j)) {
							// track selection detected, get URI
							
//							URI uri = 
						}
					}
				}
			}
			
			
			// TODO refresh list to display newly saved alarm
			
		}
	}
	

	private String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	    if (cursor == null) {
	      return contentUri.getPath();
	    }
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}
