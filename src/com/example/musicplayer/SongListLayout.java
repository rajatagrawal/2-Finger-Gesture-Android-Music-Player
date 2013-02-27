package com.example.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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

public class SongListLayout extends Activity{

	ListView songListView;
	Button backButton;
	ArrayAdapter<String> songNamesAdapter;
	ArrayList<String>songNames;
	ArrayList<String>songAlbumName;
	Activity activity;
	Toast messageToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		
		Log.d("songLisiting","In song listing activity");
		songListView = (ListView)findViewById(R.id.albumList1);
		songAlbumName = new ArrayList<String>();
		activity = this;
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		songNames = new ArrayList<String>();
		messageToast = new Toast(this);
		if (songListView == null || backButton == null)
		{
			messageToast.cancel();
			messageToast = Toast.makeText(this,"There is an error reading the layout of the xml file. Quitting",Toast.LENGTH_SHORT);
			messageToast.show();
			Log.d("songListing","There is an error reading the layout structure. Quitting selection of album.");
			
			// error messages on the intent that is returned to the main activity
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		this.setupButtonLayout();
		songListView.setClickable(true);
		songListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				Log.d("songListing","The item clicked is " + arg2);
				if (songListView.getAdapter() != null)
				{
					Log.d("songListing","The selected item is "+ songListView.getAdapter().getItem(arg2));
					Intent returnIntent = new Intent();
					returnIntent.putExtra("songName", songListView.getAdapter().getItem(arg2).toString());
					returnIntent.putExtra("albumName",songAlbumName.get(arg2));
					setResult(Activity.RESULT_OK,returnIntent);
				}
				finish();
			
			}
			
		});
		loadFileSystem();
	}
	public void setupButtonLayout()
	{
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				Log.d("songListing","on click for back button");
				setResult(Activity.RESULT_CANCELED);;
				finish();
				
			}
		});
	}
	
	private void loadFileSystem()
    {
    	ContentResolver contentResolver = this.getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.ALBUM,
    			MediaStore.Audio.Media.TITLE
    		};
    	Log.d("songListing","The uri of the media directory is "+ uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,null,MediaStore.Audio.Media.TITLE);
    	if (cursor == null)
    	{
    		Log.d("songListing","There was an error reading music files from the music library.");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("songListing","There are no music files present in the library!");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There are no songs present in the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else
    	{
    		Log.d("songListing","Songs present in the music library");
    		songNamesAdapter = new ArrayAdapter<String>(this,R.layout.album_name_view,R.id.albumName,songNames);
    		if (songNamesAdapter == null)
    		{
    			Log.d("songListing","Album List data is null");
    			messageToast.cancel();
    			messageToast = Toast.makeText(activity,"Error reading music files from the music library!",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    		}
    		songAlbumName.clear();
    		while(cursor.moveToNext())
    		{
    				songNamesAdapter.add(cursor.getString(2));
    				songAlbumName.add(cursor.getString(1));
    		}
    		songListView.setAdapter(songNamesAdapter);
    		Log.d("songListing","Number of songs in list view are " + cursor.getCount());
    	}
    }
}