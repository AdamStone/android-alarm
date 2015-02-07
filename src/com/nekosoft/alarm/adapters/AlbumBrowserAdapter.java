package com.nekosoft.alarm.adapters;

import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import com.nekosoft.alarm.AudioListSongView;
import com.nekosoft.alarm.R;
import com.nekosoft.alarm.SparseBooleanArrayParcelable;

public class AlbumBrowserAdapter extends CursorTreeAdapter {
	
	private LayoutInflater mInflater;
	private Context mContext;
	public Cursor mAlbumCursor;
	public int mAlbumCount;
	public SparseBooleanArrayParcelable[] mSongSelection; // keep track of songSelected state
	
	// constructor
	public AlbumBrowserAdapter(Cursor cursor, Context context) {
		super(cursor, context);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mAlbumCursor = cursor;
		mAlbumCount = mAlbumCursor.getCount();
		
		// initialize array
		mSongSelection = new SparseBooleanArrayParcelable[mAlbumCount];
		for (int i=0; i < mAlbumCount; i++) {
			mSongSelection[i] = new SparseBooleanArrayParcelable();
		}
	}
	
	// constructor with initial mSongSelection
	public AlbumBrowserAdapter(Cursor cursor, Context context, SparseBooleanArrayParcelable[] selection) {
		this(cursor, context);
		
		mSongSelection = selection;
	}
	
	
	// logic for setting songSelected state
	public void setSongSelected(int groupPosition, int childPosition, boolean songSelected) {
		mSongSelection[groupPosition].put(childPosition, songSelected);
	}
	
	private View populateSongView(AudioListSongView view, Cursor cursor) {
		TextView songName = (TextView) view.findViewById(R.id.song_row_name);
		TextView songDetail = (TextView) view.findViewById(R.id.song_row_detail);

		songName.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
		
		int seconds = (int) Math.round(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))/1000.);
		songDetail.setText(durationString(seconds));
		
		// check songSelected state
		int groupPosition = cursor.getInt(cursor.getColumnIndexOrThrow("groupPosition"));
		int childPosition = cursor.getInt(cursor.getColumnIndexOrThrow("childPosition"));
		
		Log.i("ALARM-adapter", String.format("view populated at group %d and child %d", groupPosition, childPosition));
		if (mSongSelection[groupPosition].get(childPosition)) {
			Log.i("ALARM-adapter", "setting songSelected true");
			view.setSongSelected(true);
		}
		else {
			Log.i("ALARM-adapter", "setting songSelected false");
			view.setSongSelected(false);
		}
		return view;
	}
	
	@Override // view for child row (song view)
	protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
		AudioListSongView view = new AudioListSongView(context);
		return populateSongView(view, cursor);
	}
	
	@Override // view for group row (album view)
	protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
		View mView = mInflater.inflate(R.layout.listviewrow_album, null);
		TextView albumName = (TextView) mView.findViewById(R.id.album_row_name);
		TextView albumArtist = (TextView) mView.findViewById(R.id.album_row_artist);		
		albumName.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
		albumArtist.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));		
		return mView;
	}	
	@Override
	protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
		AudioListSongView songView = (AudioListSongView) view; 
		populateSongView(songView, cursor);
	}
		
	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
		TextView albumName = (TextView) view.findViewById(R.id.album_row_name);
		TextView albumArtist = (TextView) view.findViewById(R.id.album_row_artist);

		if (albumName != null) {
			albumName.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM))); 
			albumArtist.setText(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));		
		} 
		else {
			Log.i("ALARM-albumBrowserAdapter", "in bindGroupView, albumName is NULL"); 
		}
	}
	
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		// id of the album selected
		String groupId = groupCursor.getString(groupCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
		
		// get cursor pointing to songs in the album
		String[] songColumns = {
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ALBUM_ID};
		
		String selectionClause = MediaStore.Audio.Media.ALBUM_ID + " = ?";
		String[] selectionArgs = { groupId };
		
		Cursor albumSongsCursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				songColumns,
				selectionClause,
				selectionArgs,
				null); // sort order
		
		/*
		 *  Need the group and child positions of the particular song from within the newChildView
		 *  and bindChildView methods in order to check songSelection state and change view 
		 *  accordingly. No obvious way to identify the view's group/child position from 
		 *  within these methods, so one option is to include them in the cursor passed
		 *  to these methods, which is produced here. To add these columns to the cursor:
		 *  
		 *  1) find group position
		 *  2) build positionCursor with columns group position and child position,
		 *     with child values set by iterating over the albumSongsCursor count
		 *  3) use CursorJoiner to merge the albumSongsCursor with the positionCursor
		 *  4) populate resultCursor via cursorJoiner result
		 *  
		 *  (in this case the cursors should overlap entirely, so CursorJoiner is 
		 *  somewhat unnecessary (all results should be BOTH, so the BOTH logic could be 
		 *  implemented directly), but this demonstrates the more general approach)
		 */
		
		// find group position
		int groupPosition = getCursorRowPosition(mAlbumCursor, groupId);
		
		// build positionCursor
		String[] positionColumns = new String[] { "_id", "groupPosition", "childPosition"};
		MatrixCursor positionCursor = new MatrixCursor(positionColumns);
		if (albumSongsCursor.moveToFirst()) {
			for (int i=0; i < albumSongsCursor.getCount(); i++) {
				String songId = albumSongsCursor.getString(albumSongsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
				positionCursor.addRow(new String[] { songId, String.valueOf(groupPosition), String.valueOf(i) });
				albumSongsCursor.moveToNext();
			}			
		}
		
		// initialize result cursor
		String[] resultColumns = {
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ALBUM_ID,
				"groupPosition",
				"childPosition" };
		MatrixCursor resultCursor = new MatrixCursor(resultColumns);		
		
		// join albumSongCursor with positionCursor
		CursorJoiner joiner = new CursorJoiner(
				albumSongsCursor, new String[] {MediaStore.Audio.Media._ID}, 
				positionCursor, new String[] {"_id"});
				
		for (CursorJoiner.Result result : joiner) {
			switch (result) {
			case LEFT:
				// debug: should not occur unless something goes wrong
				Log.i("ALARM-adapter", "warning: case LEFT invoked");
				break;
			case RIGHT:
				// debug: should not occur unless something goes wrong
				Log.i("ALARM-adapter", "warning: case RIGHT invoked");
				break;
			case BOTH:
				// should occur once for each song in the album 
//					Log.i("ALARM-adapter", "case BOTH invoked");
				
				// build new row and add to result cursor
				String[] newRow = new String[resultColumns.length];
				for (int i=0; i < resultColumns.length; i++) {
					if (i < songColumns.length) {
//						Log.i("ALARM-adapter", String.format("result %d should be in songColumns", i));
						newRow[i] =  albumSongsCursor.getString(albumSongsCursor.getColumnIndexOrThrow(resultColumns[i]));
					}
					else {
//						Log.i("ALARM-adapter", String.format("result %d should be in positionColumns", i));
						newRow[i] = positionCursor.getString(positionCursor.getColumnIndexOrThrow(resultColumns[i]));
					}
				}
				resultCursor.addRow(newRow);
				break;
			}
		} 
		
		// debug: log the contents of the cursors
//		DatabaseUtils.dumpCursor(albumSongsCursor);
//		DatabaseUtils.dumpCursor(positionCursor);
//		DatabaseUtils.dumpCursor(resultCursor);
		
		albumSongsCursor.close();
		positionCursor.close();
		return resultCursor;
	}
	
	
	private int getCursorRowPosition(Cursor cursor, String id) {
		if (cursor.moveToFirst()) {
			for (int i=0; i < cursor.getCount(); i++) {
				String currentId = cursor.getString(mAlbumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
//				Log.i("ALARM-adapter", String.format("currentId %s; id %s", currentId, id));
				
				if (id.equals(currentId)) {
//					Log.i("ALARM-adapter", "match detected");
					return i;
				}
				else {
					cursor.moveToNext();
				}
			}
		}

		// if something goes wrong
		return -1;
	}
	
	
	private String durationString(int seconds) {
		String output;
		int minutes = 0;
		int hours = 0;
		if (seconds > 60) {
			minutes = seconds/60;
			seconds = (int) Math.round((seconds/60. - minutes)*60);
			
			if (minutes > 60) {
				hours = minutes/60;
				minutes = (int) Math.round((minutes/60. - hours)*60);
				output = String.format(Locale.US, "%d:%d:%02d", hours, minutes, seconds);
			}
			else {
				output = String.format(Locale.US, "%d:%02d", minutes, seconds);
			}
		}
		else {
			output = String.format(Locale.US, "%d:%02d", minutes, seconds);
		}
		
		return output;
	}
}