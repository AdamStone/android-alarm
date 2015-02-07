package com.nekosoft.alarm;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class AudioListSongView extends RelativeLayout {
	
	private final static int[] STATE_SONG_SELECTED = {R.attr.state_song_selected};
	private boolean songSelected = false;

	// Constructors
	public AudioListSongView(Context context) {
		this(context, null);
	}
	
	public AudioListSongView(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadViews();
	}
	
	public AudioListSongView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		loadViews();
	}
	
	private void loadViews() {
		Log.i("ALARM-songView", "loadViews called");
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listviewrow_song, this, true);
	}
	
	// Customization
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		Log.i("ALARM-songView", "onCreateDrawableState called");
		// If the song is selected, merge custom state into the existing
		// state before returning it
		if (songSelected) {
			// adding 1 extra state
			Log.i("ALARM-songView", "merging drawable states");
			final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
			mergeDrawableStates(drawableState, STATE_SONG_SELECTED);
			return drawableState;
		}
		else {
			// don't merge
//			Log.i("ALARM-songView", "not merging drawable states (not selected)");
			return super.onCreateDrawableState(extraSpace);
		}
	}
	
	public void setSongSelected(boolean songSelected) {
		if (this.songSelected != songSelected) {
			this.songSelected = songSelected;
			Log.i("ALARM-songView", "toggling songSelected state and refreshing drawable");
			this.refreshDrawableState();
		}
	}	
}
