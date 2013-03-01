package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

/**
 * This class parses and interprets the result returned by the child activities to select songs either in song list, album list or artist list
 * and loads the song queue and the song player with the song to be played
 * @author rajatagrawal
 *
 */
public class ActivityResultProcessing {
	
	void processActivityResults(int requestCode, int resultCode, Intent data, MainActivity parentActivity)
	{

	    {
	    	// if any of the child activities returned without returning any result
	    	if (resultCode == Activity.RESULT_CANCELED && (requestCode == 1 || requestCode == 2 || requestCode == 3))
	    	{
	    		return;
	    	}
	    	
	    	Log.d("ActivityResultProcessing","Activity just returned to the parent activity");
	    	
	    	// if the child activity just finished is either the album listing or only songs listing activity
	    	if (requestCode == 1 || requestCode == 2)
	    	{
	    		Log.d("ActivityResultProcessing","The album selected is " + data.getStringExtra("albumName"));
	    		Log.d("ActivityResultProcessing","The selected song is " + data.getStringExtra("songName"));
	    	}
	    	
	    	
	    	// do the processing for reading the file system on the SD card of the phone
	    	parentActivity.contentResolver = parentActivity.getContentResolver();
	    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	    	parentActivity.albumArtParentUri = Uri.parse("content://media/external/audio/albumart");
	    	String [] projection=
	    		{
	    			MediaStore.Audio.Media._ID,
	    			MediaStore.Audio.Media.DATA,
	    			MediaStore.Audio.Media.ALBUM,
	    			MediaStore.Audio.Media.TITLE,
	    			MediaStore.Audio.Media.ALBUM_ID,
	    			MediaStore.Audio.Media.ARTIST
	    		};
	    	String selection;
	    	Cursor cursor = null;
	    	
	    	// if the child activity is only song listing activity
	    	if (requestCode == 1)
	    	{
	    		selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 ";
	    		cursor = parentActivity.contentResolver.query(uri,projection, selection,null,MediaStore.Audio.Media.TITLE);
	    	}
	    	// if the child activity is album listing activity
	    	else if (requestCode == 2)
	    	{
	    		selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 " + "AND " + MediaStore.Audio.Media.ALBUM + " LIKE ?";
	    		String [] arguments = 
	    		{
	    				data.getStringExtra("albumName")
	    		};
	    		cursor = parentActivity.contentResolver.query(uri,projection, selection, arguments,MediaStore.Audio.Media.TITLE);
	    	}
	    	
	    	// if the child activity is artist listing activity
	    	else if (requestCode == 3)
	    	{
	    		selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 " + "AND " + MediaStore.Audio.Media.ARTIST + " LIKE ?";
	    		String [] arguments = 
	    		{
	    				data.getStringExtra("artistName")
	    		};
	    		cursor = parentActivity.contentResolver.query(uri,projection, selection, arguments,MediaStore.Audio.Media.TITLE);
	    	}
	    	Log.d("ActivityResultProcessing","After doing the cursor in the first activity");
	    	
	    	// If the music folder on the SD card cannot be accessed
	    	if (cursor == null)
	    		Log.d("ActivityResultProcessing","There is an error getting the song");
	    	
	    	// if there are no songs present in the music folder
	    	else if (cursor.getCount() == 0)
	    		Log.d("ActivityResultProcessing","The album songs are not found in the library");
	    	
	    	//songs present in the music library
	    	else
	    	{
	    		Log.d("ActivityResultProcessing", "in else for cursor");
	    		cursor.moveToNext();
	    		
	    		//initialize the song queue and album art names array
	    		parentActivity.songQueue.clear();
	    		parentActivity.songArtURLs.clear();
	    		parentActivity.songQueueNames.clear();
	    		
	    		
	    		//populate the song queue
	    		for (int i=0;i<cursor.getCount();i++)
	    		{
	    			// find the current song that needs to be played
	    			if (cursor.getString(3).equalsIgnoreCase(data.getStringExtra("songName")) )
	    			{
	    			
	    				Log.d("ActivityResultProcessing","Came in if");
	    				parentActivity.currentSongIndex = i;
	    				parentActivity.currentSong = cursor.getString(3);
	    			}
	    			
	    			// add the song data, song name and album art urls in the respective arrays
	    			parentActivity.songQueue.add(Uri.parse(cursor.getString(1)));
	    			parentActivity.songQueueNames.add(cursor.getString(3));
	    			parentActivity.songArtURLs.add(cursor.getLong(4));
	    			cursor.moveToNext();
	    		}
	    		Log.d("ActivityResultProcessing","The current song index is " + parentActivity.currentSongIndex + " and the name of the song is " + parentActivity.currentSong);
	    		
	    		// if there was an error filling the song queue
	    		if (parentActivity.songQueue.size() == 0)
	    		{
	    			parentActivity.messageToast.cancel();
	    			parentActivity.messageToast = Toast.makeText(parentActivity,"There is an error retrieving song files from the library",Toast.LENGTH_SHORT);
	    			parentActivity.messageToast.show();
	    			return;
	    		}
	    		
	    		/*play the song queue starting with the current song
	    		 * load the album art for the current song
	    		 */
	    		try {
	    			//start the song queue with the current song
	    			parentActivity.songPlayer.reset();
	    			parentActivity.songPlayer.setDataSource(parentActivity,parentActivity.songQueue.get(parentActivity.currentSongIndex));
					parentActivity.songPlayer.prepare();
					parentActivity.messageToast = Toast.makeText(parentActivity,"Playing Song \n" + parentActivity.songQueueNames.get(parentActivity.currentSongIndex), Toast.LENGTH_LONG);
					parentActivity.messageToast.show();
					parentActivity.songPlayer.start();
					
					parentActivity.songNameTextView.setText("Playing Song : " + parentActivity.songQueueNames.get(parentActivity.currentSongIndex));
					
					// load the album art for the current song
					parentActivity.albumArtUri = ContentUris.withAppendedId(parentActivity.albumArtParentUri,parentActivity.songArtURLs.get(parentActivity.currentSongIndex));
	    			if (parentActivity.albumArtUri != null)
	    				parentActivity.songArtStream = parentActivity.contentResolver.openInputStream(parentActivity.albumArtUri);
	    			
	    			// if the song doesn't have an album art, load a default album art for the song
	    			else
	    			{
	    				parentActivity.songImage.setImageBitmap(null);
	    				parentActivity.songImage.setBackgroundDrawable(parentActivity.getResources().getDrawable(R.drawable.music_no_album_art));
	    			}
	    			
	    			//set the album art for the current song
	    			Bitmap songArtBitmap = BitmapFactory.decodeStream(parentActivity.songArtStream);
	    			parentActivity.songImage.setImageBitmap(songArtBitmap);
	    			
				}
	    		/* if there are any exceptions playing the current song
	    		 * or loading the album art for the current song
	    		 */
	    		
	    		catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		catch (FileNotFoundException e)
	    		{
	    			parentActivity.songImage.setImageBitmap(null);
					parentActivity.songImage.setBackgroundDrawable(parentActivity.getResources().getDrawable(R.drawable.music_no_album_art));
	    		}
	    		catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		catch(Exception error)
	    		{
	    			parentActivity.messageToast.cancel();
	    			parentActivity.messageToast =Toast.makeText(parentActivity,"Sorry there was an error playing this song",Toast.LENGTH_SHORT);
	    			parentActivity.messageToast.show();
	    			return;
	    			
	    		}
	    		Log.d("ActivityResultProcessing","Finished setting up the source");
	    	}
	    }

	}

}