package com.nekosoft.alarm;

public class AlarmModel {
	
	public final static String ID = "com.nekosoft.alarm.ID";
//	public final static String NAME = "com.nekosoft.alarm.NAME";
	public final static String HOUR = "com.nekosoft.alarm.HOUR";
	public final static String MINUTE = "com.nekosoft.alarm.MINUTE";
	public final static String BARCODE = "com.nekosoft.alarm.BARCODE";
	public final static String AUDIO = "com.nekosoft.alarm.AUDIO";

	public long id;
//	public String name;
	public int hour;
	public int minute;
	public String barcode;
	public SparseBooleanArrayParcelable[] songSelection;
	// TODO establish audio model
	
	
}
