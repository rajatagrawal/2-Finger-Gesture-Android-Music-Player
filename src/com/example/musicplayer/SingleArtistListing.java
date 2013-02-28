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

public class SingleArtistListing extends Activity
{
	ListView artistSongs;
	Button backButton;
	Bundle receivedBundle;
	String artistName;
	ArrayAdapter <String> artistSongNamesAdapter;
	String selectedSong;
	Activity activity;
	Intent resultIntent;
	ArrayList <String> songNames;
	Toast messageToast;
	Button listCaption;
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		Log.d("SingleArtistListing","started SingleAlbumListing and getting album songs");
		artistSongs = (ListView)findViewById(R.id.albumList1);
		Log.d("SingleArtistListing","after getting list view and is " + artistSongs);
		
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		listCaption = (Button) findViewById(R.id.listCaption);
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
		artistSongs.setClickable(true);
		artistSongs.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Log.d("SingleArtistListing","the item selected is " + arg2);
				// TODO Auto-generated method stub

				selectedSong = artistSongs.getAdapter().getItem(arg2).toString();
				Log.d("SingleArtistListing","The selected song is " + selectedSong);
				resultIntent = new Intent();
				resultIntent.putExtra("songName", selectedSong);
				resultIntent.putExtra("artistName", artistName);
				setResult(Activity.RESULT_OK,resultIntent);
				activity.finish();
				
			}
			
		});
		receivedBundle = getIntent().getExtras();
		Log.d("SingleArtistListing","Before receving album name in album listing");
		artistName = receivedBundle.getString("artistName");
		listCaption.setText(receivedBundle.getString("artistName").toUpperCase());
		Log.d("SingleArtistListing","The album name received in the child activity is " + artistName);
		loadFileSystem();
	}

	private void loadFileSystem()
    {
    	ContentResolver contentResolver = this.getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.ARTIST + " LIKE ?";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.TITLE,
    			MediaStore.Audio.Media.ARTIST
    		};
    	String [] arguments = {artistName};
    	
    	Log.d("SingleArtistListing","The uri is "+uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,arguments,MediaStore.Audio.Media.TITLE);
    	if (cursor == null)
    	{
    		Log.d("SingleArtistListing","There was an error reading music files from the music library.");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("SingleArtistListing","There are no music files present in the library!");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There are no songs for this artist",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	else
    	{
    		Log.d("SingleArtistListing","Songs present for the artist");
    		artistSongNamesAdapter= new ArrayAdapter<String>(this.getApplicationContext(),R.layout.album_name_view,R.id.albumName,songNames);
    		
    		if (artistSongNamesAdapter== null)
    		{
    			Log.d("songListing","Artist List data is null");
    			messageToast.cancel();
    			messageToast = Toast.makeText(activity,"Error reading music files from the music library!",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    		}
    		while(cursor.moveToNext())
    		{
    			artistSongNamesAdapter.add(cursor.getString(1));
    		}
    		this.artistSongs.setAdapter(artistSongNamesAdapter);
    	}
    }

}