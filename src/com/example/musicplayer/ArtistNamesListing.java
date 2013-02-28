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

public class ArtistNamesListing extends Activity{

	ListView artistListView;
	Button backButton;
	ArrayAdapter<String> artistListDataAdapter;
	ArrayList<String>artistNames;
	Activity activity;
	Toast messageToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		
		Log.d("ArtistNamesListing","In artist names listing activity");
		artistListView = (ListView)findViewById(R.id.albumList1);
		activity = this;
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		artistNames = new ArrayList<String>();
		messageToast = new Toast(this);
		if (artistListView == null || backButton == null)
		{
			Log.d("ArtistNamesListing","There is an error reading the layout structure. Quitting selection of artist.");
			
			// error messages on the intent that is returned to the main activity
			finish();
		}
		this.setupButtonLayout();
		artistListView.setClickable(true);
		artistListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("ArtistNamesListing","The selected item is "+ artistListView.getAdapter().getItem(arg2));
				Intent childIntent = new Intent(activity,SingleArtistListing.class);
				childIntent.putExtra("artistName", artistListView.getAdapter().getItem(arg2).toString());
				activity.startActivityForResult(childIntent, 1);
			}
			
		});
		loadFileSystem();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		Log.d("ArtistNamesListing","Returned to one of the parent activities");
		if (resultCode == Activity.RESULT_CANCELED && requestCode == 1)
			return;
		else if (resultCode == Activity.RESULT_OK && requestCode == 1) 
		{
			setResult(Activity.RESULT_OK,intent);
			Log.d("ArtistNamesListing","The results are in the intent : " + intent.getStringExtra("artistName").toString() + " and song name is " + intent.getStringExtra("songName").toString());
			finish();
		}
	}
	public void setupButtonLayout()
	{
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("ArtistNamesListing","on click for back button");
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
    			MediaStore.Audio.Media.ARTIST
    		};
    	Log.d("ArtistNamesListing","The changed uri is "+ uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,null,MediaStore.Audio.Media.ARTIST);
    	if (cursor == null)
    	{
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		Log.d("ArtistNamesListing","There was an error reading music files from the music library.");
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("ArtistNamesListing","There are no music files present in the library!");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There are no songs present in the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else
    	{
    		Log.d("ArtistNamesListing","Music files present in the music library");
    		artistListDataAdapter = new ArrayAdapter<String>(this,R.layout.album_name_view,R.id.albumName,artistNames);
    		if (artistListDataAdapter == null)
    		{
    			Log.d("ArtistNamesListing","Album List data is null");
    			messageToast.cancel();
    			messageToast = Toast.makeText(activity,"Error reading music files from the music library!",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    		}
    		while(cursor.moveToNext())
    		{
    			if (artistNames.size() == 0)
    				artistListDataAdapter.add(cursor.getString(1));
    			else if (cursor.getString(1).equalsIgnoreCase(artistNames.get(artistNames.size()-1)) == false)
    			{
    				artistListDataAdapter.add(cursor.getString(1));
    			}
    		}
    		artistListView.setAdapter(artistListDataAdapter);
    		Log.d("ArtistNamesListing","Number of Albums are " + cursor.getCount());
    	}
    }
}