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

public class SingleAlbumListing extends Activity
{
	ListView albumSongs;
	Button backButton;
	Bundle receivedBundle;
	String albumName;
	ArrayAdapter <String> albumSongNamesAdapter;
	String selectedSong;
	Activity activity;
	Intent resultIntent;
	ArrayList <String> songNames;
	Toast messageToast;
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		Log.d("SingleAlbumListing","started SingleAlbumListing and getting album songs");
		albumSongs = (ListView)findViewById(R.id.albumList1);
		Log.d("SingleAlbumListing","after getting list view and is " + albumSongs);
		
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		activity = this;
		songNames = new ArrayList<String>();
		
		messageToast = new Toast(this);
		
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_CANCELED);
				activity.finish();
			}
		});
		albumSongs.setClickable(true);
		albumSongs.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Log.d("SingleAlbumListing","the item selected is " + arg2);
				// TODO Auto-generated method stub

				selectedSong = albumSongs.getAdapter().getItem(arg2).toString();
				Log.d("SingleAlbumListing","The selected song is " + selectedSong);
				resultIntent = new Intent();
				resultIntent.putExtra("songName", selectedSong);
				resultIntent.putExtra("albumName", albumName);
				setResult(Activity.RESULT_OK,resultIntent);
				activity.finish();
				
			}
			
		});
		receivedBundle = getIntent().getExtras();
		Log.d("SingleAlbumListing","Before receving album name in album listing");
		albumName = receivedBundle.getString("albumName");
		Log.d("SingleAlbumListing","The album name received in the child activity is " + albumName);
		loadFileSystem();
	}

	private void loadFileSystem()
    {
    	ContentResolver contentResolver = this.getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.ALBUM + " LIKE ?";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.TITLE
    		};
    	String [] arguments = {albumName};
    	
    	Log.d("SingleAlbumListing","The uri is "+uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,arguments,MediaStore.Audio.Media.TITLE);
    	if (cursor == null)
    	{
    		Log.d("SingleAlbumListing","There was an error reading music files from the music library.");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("SingleAlbumListing","There are no music files present in the library!");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There are no songs in the album",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else
    	{
    		Log.d("SingleAlbumListing","Songs present in the album");
    		albumSongNamesAdapter= new ArrayAdapter<String>(this.getApplicationContext(),R.layout.album_name_view,R.id.albumName,songNames);
    		
    		if (albumSongNamesAdapter== null)
    		{
    			Log.d("songListing","Album List data is null");
    			messageToast.cancel();
    			messageToast = Toast.makeText(activity,"Error reading music files from the music library!",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    		}
    		while(cursor.moveToNext())
    		{
    			albumSongNamesAdapter.add(cursor.getString(1));
    		}
    		this.albumSongs.setAdapter(albumSongNamesAdapter);
    	}
    }

}
