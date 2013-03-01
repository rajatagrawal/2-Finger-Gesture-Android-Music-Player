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

/**
 * This class shows all the songs for a single artist selected by the user
 * @author rajatagrawal
 *
 */
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
		artistSongs = (ListView)findViewById(R.id.albumList1);
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		listCaption = (Button) findViewById(R.id.listCaption);
		activity = this;
		songNames = new ArrayList<String>();
		messageToast = new Toast(this);
		
		// if the user doesn't select any song and returns without selecting a song.
		// this click listener returns RESULT_CANCELLED as the result when the user clicks this button.
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_CANCELED);
				activity.finish();
			}
		});
		artistSongs.setClickable(true);
		
		// this click listener sets the song and the artist for the song selected by the user to be returned to the parent activity.
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
		artistName = receivedBundle.getString("artistName");
		listCaption.setText(receivedBundle.getString("artistName").toUpperCase());
		loadFileSystem();
	}

	/**
	 * This function loads all the songs corresponding to the artist selected by the user.
	 * 
	 * If there are no songs for that artist, or there is an error retrieving them, the function returns in between without
	 * loading any album names.
	 */
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
    	
    	// if there is an error reading the songs on the SD card of the phone
    	if (cursor == null)
    	{
    		Log.d("SingleArtistListing","There was an error reading music files from the music library.");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	// if there are no songs present in the music library of the SD card of the phone memory.
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("SingleArtistListing","There are no music files present in the library!");
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There are no songs for this artist",Toast.LENGTH_SHORT);
    		messageToast.show();
    		return;
    	}
    	
    	// songs are present in the music library on the SD card of the phone memory.
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