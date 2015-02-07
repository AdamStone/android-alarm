package com.nekosoft.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDBOpenHelper extends SQLiteOpenHelper {
	
	private final static int DATABASE_VERSION = 1;
	private final static String ALARM_TABLE_NAME = "Alarms";
	private final static String ALARM_TABLE_CREATE = 
			"CREATE TABLE " + ALARM_TABLE_NAME + " (" +
			"hour int, " + 
			"minute int, " +
			"barcode text, ";

	public AlarmDBOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
