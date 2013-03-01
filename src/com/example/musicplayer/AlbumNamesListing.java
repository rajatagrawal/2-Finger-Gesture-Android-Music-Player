package com.example.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class displays all the album names present for all the songs in the music library present on the SD memory card of the phone
 * @author rajatagrawal
 *
 */
public class AlbumNamesListing extends Activity{

	ListView albumListView;
	Button backButton;
	ArrayAdapter<String> albumListDataAdapter;
	ArrayList<String>albumNames;
	Activity activity;
	Toast messageToast;
	Button listCaption;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		//disable the change in the orientation of the activity on rotating the phone
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		
		Log.d("AlbumNamesListing","In album names listing activity");
		
		//setting up the member variables of the class
		albumListView = (ListView)findViewById(R.id.albumList1);
		activity = this;
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		listCaption = (Button) findViewById(R.id.listCaption);
		listCaption.setText("ALBUMS");
		albumNames = new ArrayList<String>();
		messageToast = new Toast(this);
		
		
		if (albumListView == null || backButton == null)
		{
			Log.d("AlbumNamesListing","There is an error reading the layout structure. Quitting selection of album.");
			
			// error messages on the intent that is returned to the main activity
			finish();
		}
		
		this.setupButtonLayout();
		albumListView.setClickable(true);
		
		// this on click listener starts a new activity passing the selected album name as the argument.
		albumListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("AlbumNamesListing","The selected item is "+ albumListView.getAdapter().getItem(arg2));
				Intent childIntent = new Intent(activity,SingleAlbumListing.class);
				childIntent.putExtra("albumName", albumListView.getAdapter().getItem(arg2).toString());
				activity.startActivityForResult(childIntent, 1);
			
			}
			
		});
		loadFileSystem();
	}
	/**
	 * This function returns the album name and the song selected by the user.
	 * If the users returns back without selecting any song, this function returns RESULT_CANCELLED flag message on returning the
	 * result of the activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		Log.d("AlbumNamesListing","Returned to one of the parent activities");
		if (resultCode == Activity.RESULT_CANCELED && requestCode == 1)
			return;
		else if (resultCode == Activity.RESULT_OK && requestCode == 1) 
		{
			setResult(Activity.RESULT_OK,intent);
			Log.d("AlbumNamesListing","The results are in the intent : " + intent.getStringExtra("albumName").toString() + " and song name is " + intent.getStringExtra("songName").toString());
			finish();
		}
	}
	public void setupButtonLayout()
	{
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("AlbumNamesListing","on click for back button");
				setResult(Activity.RESULT_CANCELED);;
				finish();
				
			}
		});
	}
	
	/**
	 * This function loads all the album names corresponding to all the songs present in the music library of the 
	 * SD card of the phone.
	 * 
	 * If there are no songs in the music library, or there is an error retreiving them, the function returns in between without
	 * loading any album names.
	 */
	private void loadFileSystem()
    {
    	ContentResolver contentResolver = this.getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.ALBUM
    		};
    	Log.d("AlbumNamesListing","The changed uri is "+ uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,null,MediaStore.Audio.Media.ALBUM);
    	
    	// if the program couldn't get the files from the SD card of the phone
    	if (cursor == null)
    	{
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		Log.d("AlbumNamesListing","There was an error reading music files from the music library.");
    		return;
    	}
    	
    	// if the number of songs present in the music library is zero
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("AlbumNamesListing","There are no music files present in the library!");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There are no songs present in the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	
    	//found music files in the SD card of the phone
    	else
    	{
    		Log.d("AlbumNamesListing","Music files present in the music library");
    		albumListDataAdapter = new ArrayAdapter<String>(this,R.layout.album_name_view,R.id.albumName,albumNames);
    		if (albumListDataAdapter == null)
    		{
    			Log.d("AlbumNamesListing","Album List data is null");
    			messageToast.cancel();
    			messageToast = Toast.makeText(activity,"Error reading music files from the music library!",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    		}
    		
    		// add unique album names for the songs present in the music library to an array storing the unique album names
    		while(cursor.moveToNext())
    		{
    			if (albumNames.size() == 0)
    				albumListDataAdapter.add(cursor.getString(1));
    			else if (cursor.getString(1).equalsIgnoreCase(albumNames.get(albumNames.size()-1)) == false)
    			{
    				albumListDataAdapter.add(cursor.getString(1));
    			}
    		}
    		
    		//set the adapter for the list view to the array holding unique album names
    		albumListView.setAdapter(albumListDataAdapter);
    		Log.d("AlbumNamesListing","Number of Albums are " + cursor.getCount());
    	}
    }
}