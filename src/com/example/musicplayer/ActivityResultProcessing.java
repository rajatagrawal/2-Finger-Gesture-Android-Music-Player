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

public class ActivityResultProcessing {
	
	void processActivityResults(int requestCode, int resultCode, Intent data, MainActivity parentActivity)
	{

	    {
	    	if (resultCode == Activity.RESULT_CANCELED && (requestCode == 1 || requestCode == 2 || requestCode == 3))
	    	{
	    		return;
	    	}
	    	
	    	Log.d("ActivityResultProcessing","Activity just returned to the parent activity");
	    	
	    	if (requestCode == 1 || requestCode == 2)
	    	{
	    		Log.d("ActivityResultProcessing","The album selected is " + data.getStringExtra("albumName"));
	    		Log.d("ActivityResultProcessing","The selected song is " + data.getStringExtra("songName"));
	    	}
	    	
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
	    	if (requestCode == 1)
	    	{
	    		selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 ";
	    		cursor = parentActivity.contentResolver.query(uri,projection, selection,null,MediaStore.Audio.Media.TITLE);
	    	}
	    	else if (requestCode == 2)
	    	{
	    		selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 " + "AND " + MediaStore.Audio.Media.ALBUM + " LIKE ?";
	    		String [] arguments = 
	    		{
	    				data.getStringExtra("albumName")
	    		};
	    		cursor = parentActivity.contentResolver.query(uri,projection, selection, arguments,MediaStore.Audio.Media.TITLE);
	    	}
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
	    	if (cursor == null)
	    		Log.d("ActivityResultProcessing","There is an error getting the song");
	    	else if (cursor.getCount() == 0)
	    		Log.d("ActivityResultProcessing","The album songs are not found in the library");
	    	else
	    	{
	    		Log.d("ActivityResultProcessing", "in else for cursor");
	    		cursor.moveToNext();
	    		parentActivity.songQueue.clear();
	    		parentActivity.songArtURLs.clear();
	    		parentActivity.songQueueNames.clear();
	    		
	    		
	    		for (int i=0;i<cursor.getCount();i++)
	    		{
	    			if (cursor.getString(3).equalsIgnoreCase(data.getStringExtra("songName")) )
	    			{
	    				Log.d("ActivityResultProcessing","Came in if");
	    				parentActivity.currentSongIndex = i;
	    				parentActivity.currentSong = cursor.getString(3);
	    			}
	    			parentActivity.songQueue.add(Uri.parse(cursor.getString(1)));
	    			parentActivity.songQueueNames.add(cursor.getString(3));
	    			parentActivity.songArtURLs.add(cursor.getLong(4));
	    			cursor.moveToNext();
	    		}
	    		Log.d("ActivityResultProcessing","The current song index is " + parentActivity.currentSongIndex + " and the name of the song is " + parentActivity.currentSong);
	    		if (parentActivity.songQueue.size() == 0)
	    		{
	    			parentActivity.messageToast.cancel();
	    			parentActivity.messageToast = Toast.makeText(parentActivity,"There is an error retrieving song files from the library",Toast.LENGTH_SHORT);
	    			parentActivity.messageToast.show();
	    			return;
	    		}
	    		try {
	    			parentActivity.songPlayer.reset();
	    			parentActivity.songPlayer.setDataSource(parentActivity,parentActivity.songQueue.get(parentActivity.currentSongIndex));
					parentActivity.songPlayer.prepare();
					parentActivity.messageToast = Toast.makeText(parentActivity,"Playing song" + parentActivity.songQueueNames.get(parentActivity.currentSongIndex), Toast.LENGTH_LONG);
					parentActivity.messageToast.show();
					parentActivity.songPlayer.start();
					
					parentActivity.albumArtUri = ContentUris.withAppendedId(parentActivity.albumArtParentUri,parentActivity.songArtURLs.get(parentActivity.currentSongIndex));
	    			if (parentActivity.albumArtUri != null)
	    				parentActivity.songArtStream = parentActivity.contentResolver.openInputStream(parentActivity.albumArtUri);
	    			else
	    			{
	    				parentActivity.songImage.setImageBitmap(null);
	    				parentActivity.songImage.setBackgroundDrawable(parentActivity.getResources().getDrawable(R.drawable.music_no_album_art));
	    			}
	    			Bitmap songArtBitmap = BitmapFactory.decodeStream(parentActivity.songArtStream);
	    			parentActivity.songImage.setImageBitmap(songArtBitmap);
	    			
				} catch (IllegalArgumentException e) {
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
