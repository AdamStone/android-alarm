package com.nekosoft.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_alarmList:
			// start the alarmsList activity
			Intent intent = new Intent(this, AlarmListActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.action_settings:
			// TODO start the settings activity
			
			return true;
		
		default:
			// not handled; pass on to super
			return super.onOptionsItemSelected(item);
		}
	}
}
