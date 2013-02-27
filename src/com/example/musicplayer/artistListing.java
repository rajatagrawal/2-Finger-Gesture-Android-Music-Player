package com.example.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class artistListing extends Activity
{
	ListView albumSongs;
	Button backButton;
	Bundle receivedBundle;
	String albumName;
	ArrayAdapter <String> albumSongsNames;
	String selectedSong;
	Activity activity;
	Intent resultIntent;
	ArrayList <String> songNames;
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		//albumSongs = (ListView)findViewById(R.id.albumList1);
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		activity = this;
		songNames = new ArrayList<String>();
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				resultIntent = new Intent();
				resultIntent.putExtra("songName", selectedSong);
				resultIntent.putExtra("albumName", albumName);
				setResult(activity.RESULT_CANCELED);
				
				activity.finish();
			}
		});
		albumSongs.setClickable(true);
		albumSongs.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				System.out.println("the item selected is " + arg2);
				// TODO Auto-generated method stub
				//System.out.println("The item clicked is " + arg2);
				//System.out.println("The selected item is "+ albumSongs.getAdapter().ge);
				selectedSong = albumSongs.getAdapter().getItem(arg2).toString();
				System.out.println("The selected song is " + selectedSong);
				resultIntent = new Intent();
				resultIntent.putExtra("songName", selectedSong);
				resultIntent.putExtra("albumName", albumName);
				setResult(Activity.RESULT_OK,resultIntent);
				activity.finish();
				
			}
			
		});
		receivedBundle = getIntent().getExtras();
		albumName = receivedBundle.getString("albumName");
		System.out.println("The album name received in the child activity is " + albumName);
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
    	String [] columnProjection = 
    		{
    			MediaStore.Audio.Media.TITLE
    		};
    	String [] arguments = 
    		{
    			albumName
    		};
    	int [] to = new int [] {R.id.albumName};
    	System.out.println("The uri is "+uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,arguments,MediaStore.Audio.Media.TITLE);
    	if (cursor == null)
    	{
    		System.out.println("There was an error reading music files from the music library.");
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		System.out.println("There are no music files present in the library!");
    	}
    	else
    	{
    		System.out.println("in else");
    		//cursor.moveToNext();
    		//songNames.add(cursor.);
    		/*while(cursor.moveToNext())
    		{
    			songNames.add(cursor.getString(cursor.get))
    		}*/
    		//if (albumNames == null)
    			//System.out.println("album names is null");
    		albumSongsNames= new ArrayAdapter<String>(this.getApplicationContext(),R.layout.album_name_view,R.id.albumName,songNames);
    		//this.albumSongs.setAdapter(albumSongsNames);
    		
    		while(cursor.moveToNext())
    		{
    			albumSongsNames.add(cursor.getString(1));
    		}
    		this.albumSongs.setAdapter(albumSongsNames);
    		for (int i=0;i<this.albumSongs.getAdapter().getCount();i++)
    			System.out.println("The song name is " + this.albumSongs.getAdapter().getItem(i));
    		/*
    		if (albumSongsNames == null)
    			System.out.println("There was an error retrieving the songs of given album");
    		if (cursor == null)
    			System.out.println("Cursor is null");
    		for (int i=0;i<cursor.getColumnCount();i++)
    			System.out.println("The columns of cursor are " + cursor.getColumnName(i) + " adn the index is " + i);
    		cursor.moveToNext();
    		albumListData.add(cursor.getString(1));
    		while(cursor.moveToNext())
    		{
    			System.out.println(" Current position is " + cursor.getPosition());
    			if (cursor.getString(1).equalsIgnoreCase(albumNames.get(albumNames.size()-1)) == false)
    				albumListData.add(cursor.getString(1));
    		}
    		for (int j=0;j<albumNames.size();j++)
    		System.out.println("Array is " + albumNames.get(j));
    		if (albumListData == null)
    			System.out.println("album list data is null");
    		albumListView.setAdapter(albumListData);
    		System.out.println("Number of rows are " + cursor.getCount() + " and column is " + cursor.getColumnName(0));
    		*/
    	}
    }

}
