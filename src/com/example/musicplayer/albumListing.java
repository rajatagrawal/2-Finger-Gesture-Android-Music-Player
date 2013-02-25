package com.example.musicplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class albumListing extends Activity
{
	ListView albumSongs;
	Button backButton;
	Bundle receivedBundle;
	String albumName;
	CursorAdapter albumSongsNames;
	String selectedSong;
	Activity activity;
	Intent resultIntent;
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		albumSongs = (ListView)findViewById(R.id.albumList1);
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		activity = this;
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				resultIntent = new Intent();
				resultIntent.putExtra("songName", selectedSong);
				resultIntent.putExtra("albumName", albumName);
				setResult(1,resultIntent);
				*/
				activity.finish();
			}
		});
		albumSongs.setClickable(true);
		albumSongs.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//System.out.println("The item clicked is " + arg2);
				//System.out.println("The selected item is "+ albumSongs.getAdapter().getItem(arg2));
				selectedSong = albumSongs.getAdapter().getItem(arg2).toString();
				System.out.println("The selected song is " + selectedSong);
				resultIntent = new Intent();
				resultIntent.putExtra("songName", selectedSong);
				resultIntent.putExtra("albumName", albumName);
				setResult(1,resultIntent);
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
    		//if (albumNames == null)
    			//System.out.println("album names is null");
    		albumSongsNames= new SimpleCursorAdapter(this.getApplicationContext(),R.layout.album_name_view,cursor,columnProjection,to);
    		this.albumSongs.setAdapter(albumSongsNames);
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
