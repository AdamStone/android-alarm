package com.nekosoft.alarm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;

import com.nekosoft.alarm.AlarmSetupActivity;
import com.nekosoft.alarm.R;
import com.nekosoft.alarm.adapters.AlbumBrowserAdapter;

public class AudioSelectionFragment extends Fragment {
	
	private final static String TAG = "ALARM-audioFragment";
	
	AlarmSetupActivity parentActivity;
	AlbumBrowserAdapter mAlbumAdapter;
	ExpandableListView mAudioListView;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "onCreate called");
		parentActivity = (AlarmSetupActivity) getActivity();
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView called");
		return inflater.inflate(R.layout.fragment_audio_selection, root, false);
	}
	
	
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		
//		Log.i(TAG, "onActivityCreated called");
//		
//		setupAudioListView();
//	}
	

	
	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called");
		
		mAlbumAdapter = parentActivity.mAlbumAdapter;
		
		Log.i(TAG, "setting up AudioListView");
		setupAudioListView();
	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		Log.i(TAG, "onResume called");
//	}
//	
//	@Override
//	public void onPause() {
//		super.onPause();
//		Log.i(TAG, "onPause called");
//	}
//	
//	@Override
//	public void onStop() {
//		super.onStop();
//		Log.i(TAG, "onStop called");
//	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, "onDestroyView called");

	}
	
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//		Log.i(TAG, "onSaveInstanceState called");
//	}
	
	
	private void setupAudioListView() {
		
		/*
		 * This is called after the views are created, so work that depends 
		 * on inflated views can be done here.
		 */
		
		// set ListView adapter
		mAudioListView = (ExpandableListView) parentActivity.findViewById(R.id.audio_selection_listview);
		if (!(mAlbumAdapter != null)) {
			Log.i(TAG, "mAlbumAdapter in onActivityCreated is NULL");
		} 
		if (!(mAudioListView != null)) {
			Log.i(TAG, "mAudioListView in onActivityCreated is NULL");
		}
		if (mAlbumAdapter != null && mAudioListView != null) {
			Log.i(TAG, "setting mAlbumAdapter to mAudioListView");
			mAudioListView.setAdapter(mAlbumAdapter);
		}
		
		// expand any groups with existing selections
		for (int i=0; i<mAlbumAdapter.mSongSelection.length; i++) {
			if (mAlbumAdapter.mSongSelection[i].size() > 0) {
				mAudioListView.expandGroup(i);
				for (int j=0; j<mAlbumAdapter.getChildrenCount(i); j++) {
					if (mAlbumAdapter.mSongSelection[i].get(j)) {
						
						int flatPosition = mAudioListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, j));
						Log.i(TAG, String.format("Expanded group %d and setting checked=true for track at position %d (flat position %d)", i, j, flatPosition));
						mAudioListView.setItemChecked(flatPosition, true);
					}
				}
			}
		}
		
		
		
		//// ALBUM BROWSER FUNCTIONALITY
		
		// onChildClick should toggle checked/songSelected state on children:
		mAudioListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				
				// get flattened index of clicked item
				long packedPosition = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
				int index = parent.getFlatListPosition(packedPosition);
				
				// get array of currently checked positions
				SparseBooleanArray currentlyChecked = parent.getCheckedItemPositions();
				
				// check if clicked item currently checked; check or uncheck
				if (currentlyChecked.get(index)) {
					parent.setItemChecked(index, false);
					mAlbumAdapter.setSongSelected(groupPosition, childPosition, false);
				}
				else {
					parent.setItemChecked(index, true);
					mAlbumAdapter.setSongSelected(groupPosition, childPosition, true);
				}
				return true;
			}
		});
		

		// onClick on groups should expand/collapse them (does by default)
		// and uncheck/unselect songs on collapse
		mAudioListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// iterate through songs in group
				for (int childPosition=0; childPosition < mAlbumAdapter.getChildrenCount(groupPosition); childPosition++) {
					// checked state unsets automatically, setSongSelected must be set manually
					mAlbumAdapter.setSongSelected(groupPosition, childPosition, false);
				}
			}
			
		});
		
		// onLongClick on groups should expand group and check/select all children
		mAudioListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int flatPosition, long id) {
				
				// get packed position from flat position
				long packedPosition = mAudioListView.getExpandableListPosition(flatPosition);
				
				
				// check item type from packed position
				if (ExpandableListView.getPackedPositionType(packedPosition) == 
						ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
					//// item is a group
					
					// get group and child positions from flat position
					int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);					
					
					// expand group
					mAudioListView.expandGroup(groupPosition);					
					
					// set all child views checked
					int childrenCount = mAlbumAdapter.getChildrenCount(groupPosition);
					for (int i=0; i < childrenCount; i++) {
						mAudioListView.setItemChecked(flatPosition+i+1, true);
						
						/*
						 * finding the song view itself and doing setSongSelected doesn't work
						 * because song view will not always be inflated, and selection should
						 * persist even when it's offscreen or recycled. So the songSelected
						 * state of all views should be tracked by the listAdapter instead
						 * and checked within the overridden methods to build the views.
						 */
						
						mAlbumAdapter.setSongSelected(groupPosition, i, true);
					}

					// testing
//					int count = audioListView.getCount();
//					int childCount = audioListView.getChildCount();
//					Log.i(TAG, String.format("getCount yields %d", count));
//					Log.i(TAG, String.format("getChildCount yields %d", childCount));
					// getCount yields full count of flat list (groups and expanded children)
					// getChildCount yields only visible number of items on screen (~7-8)
					// getChildAt(position) probably only considers "children" those visible items on screen
				}
				return true;
			}
		});
	}
	
	
}
