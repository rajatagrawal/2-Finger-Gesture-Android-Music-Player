package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.musicplayer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
    
    int counter = 0;
    ImageView songImage;
    Activity activity;
    MediaPlayer songPlayer;
    
    //declaring all the buttons and their listeners
    Button playPauseButton;
    Button previousSongButton;
    Button nextSongButton;
    AlbumButton albumButton;
    SongButton songButton;
    ArtistButton artistButton;
    View.OnClickListener nextSongListener;
    View.OnClickListener previousSongListener;
    View.OnClickListener playPauseListener;
    
    
    ArrayList <Uri> songQueue;
    ArrayList <Long> songArtURLs;
    ArrayList <String> songQueueNames;
    String currentSong;
    int currentSongIndex;
    Toast messageToast;
    SeekBar volumeControl;
    AudioManager audioManager;
    int seekBarMin;
    int seekBarMax;
    int minVolume;
    int maxVolume;
    int initialSeekBarPosition;
    
    
    Uri albumArtParentUri;
    Uri albumArtUri;
    InputStream songArtStream;
    ContentResolver contentResolver;
    Bitmap songArtBitmap;
    LinearLayout backgroundLayout;
    GestureEngine gestureEngine;
    ActivityResultProcessing activityResultProcessing;
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	songPlayer.reset();
    	songPlayer.release();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        
        activity = this;
        Log.d("MainActivity","Starting Main Activity");
        
        songPlayer = new MediaPlayer();
        songPlayer.reset();
        playPauseButton = (Button) findViewById(R.id.playSongButton);
        previousSongButton = (Button) findViewById(R.id.previousSongButton);
        nextSongButton = (Button) findViewById(R.id.nextSongButton);
        songQueue = new ArrayList<Uri>();
        songQueueNames = new ArrayList<String>();
        currentSongIndex = -1;
        currentSong = null;
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        volumeControl = (SeekBar) findViewById(R.id.seekBar1);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        minVolume = 0;
        seekBarMin = 0;
        seekBarMax = volumeControl.getMax();
        messageToast = new Toast(this);
        this.setInitialProgressBar();
        this.albumArtParentUri = Uri.parse("content://media/external/audio/albumart");
        this.albumArtUri = null;
        this.songArtStream = null;
        this.songArtBitmap = null;
        this.songArtURLs = new ArrayList<Long>();
        gestureEngine = new GestureEngine(this);
        activityResultProcessing = new ActivityResultProcessing();
        
        backgroundLayout = (LinearLayout) findViewById(R.id.backgroundLayout);
        
        this.nextSongListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (songQueue.size() == 0)
				{
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Please make a selection!", Toast.LENGTH_SHORT);
					messageToast.show();
					return;
				}
				currentSongIndex = (currentSongIndex + 1)%songQueue.size();
				songPlayer.reset();
				try {
					
					songPlayer.setDataSource(activity,songQueue.get(currentSongIndex));
					songPlayer.prepare();
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Playing next song " + songQueueNames.get(currentSongIndex),Toast.LENGTH_SHORT);
					messageToast.show();
					songPlayer.start();
					
					albumArtUri = ContentUris.withAppendedId(albumArtParentUri,songArtURLs.get(currentSongIndex));
					if (albumArtUri != null)
						songArtStream = contentResolver.openInputStream(albumArtUri);
					else
					{
						songImage.setImageBitmap(null);
						songImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.music_no_album_art));						
					}
					songImage.setBackgroundDrawable(null);
	    			songArtBitmap = BitmapFactory.decodeStream(songArtStream);
	    			songImage.setImageBitmap(songArtBitmap);
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
				catch(FileNotFoundException e)
				{
					songImage.setImageBitmap(null);
    				songImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.music_no_album_art));
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
        
		this.previousSongListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				if (songQueue.size() ==0)
				{
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Please make a song selection!",Toast.LENGTH_SHORT);
					messageToast.show();
					return;
				}
				if (currentSongIndex ==0)
					currentSongIndex = songQueue.size()-1;
				else
					currentSongIndex = currentSongIndex - 1;
				songPlayer.reset();
				try {
					
					songPlayer.setDataSource(activity, songQueue.get(currentSongIndex));
					songPlayer.prepare();
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Playing previous song " + songQueueNames.get(currentSongIndex),Toast.LENGTH_SHORT);
					messageToast.show();
					songPlayer.start();
					
					albumArtUri = ContentUris.withAppendedId(albumArtParentUri,songArtURLs.get(currentSongIndex));
					if (albumArtUri != null)
						songArtStream = contentResolver.openInputStream(albumArtUri);
					else
					{
						songImage.setImageBitmap(null);
						songImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.music_no_album_art));						
					}
					songImage.setBackgroundDrawable(null);
	    			songArtBitmap = BitmapFactory.decodeStream(songArtStream);
	    			songImage.setImageBitmap(songArtBitmap);
	    			
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
					songImage.setImageBitmap(null);
    				songImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.music_no_album_art));
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		this.playPauseListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("MainActivity","Play pause button clicked");
				try
				{
					if (songPlayer.isPlaying())
						songPlayer.pause();
					else
						songPlayer.start();
				}
				catch(NullPointerException e)
				{
					Log.d("MainActivity","The media player hasn't been initialized yet");
				}
				finally
				{
					Log.d("MainActivity","Please make a selection");
				}
				
			}
		};
        volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
        	@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				double volumeToSet = ((arg1/(float)seekBarMax)*maxVolume);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int)(volumeToSet),AudioManager.FLAG_VIBRATE);
				
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub	
				initialSeekBarPosition = volumeControl.getProgress();
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				messageToast.cancel();
				messageToast = Toast.makeText(activity,"Volume in increasing to set is " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
				messageToast.show();
			}
        });
        
        
        
        playPauseButton.setOnClickListener(this.playPauseListener);
        previousSongButton.setOnClickListener(this.previousSongListener);
        nextSongButton.setOnClickListener(this.nextSongListener);
        songPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.reset();
				currentSongIndex = (currentSongIndex + 1)%songQueue.size();
				try {
					mp.setDataSource(activity,songQueue.get(currentSongIndex));
					mp.prepare();
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Playing Song " + songQueueNames.get(currentSongIndex),Toast.LENGTH_SHORT);
					messageToast.show();
					mp.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
        
        songPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				
				Log.d("MainActivity","Came in the on error listener");
				messageToast.cancel();
				messageToast = Toast.makeText(activity,"There is an error with the music player!",Toast.LENGTH_SHORT);
				messageToast.show();
				return true;
			}
		});
        
        Log.d("MainActivity","The current time is " + System.currentTimeMillis());
        
        albumButton = (AlbumButton) findViewById(R.id.albumButton);
        albumButton.setParentActivity(this);
        
        songButton = (SongButton) findViewById(R.id.songsButton);
        songButton.setParentActivity(this);
        
        artistButton = (ArtistButton) findViewById(R.id.artistButton);
        artistButton.setParentActivity(this);
        songImage = (ImageView) findViewById(R.id.songArt);
        if (songImage == null)
        	Log.d("MainActivity","Song Image is null");
        else
        	Log.d("MainActivity","Song Image is not null");
          
        backgroundLayout.setOnTouchListener(gestureEngine.onTouchListener);
    }
    public void setInitialProgressBar()
    {
    	int initialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	volumeControl.setProgress((int)((initialVolume/(float)maxVolume)*seekBarMax));
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	activityResultProcessing.processActivityResults(requestCode, resultCode,data,this);
    }
    void playNextSong()
    {
    	Log.d("MainActivity","Playing next song");
    	this.nextSongListener.onClick(null);
    }
    void playPreviousSong()
    {
    	Log.d("MainActivity","Playing previous song");
    	this.previousSongListener.onClick(null);
    }
    void pauseThePlayer()
    {
    	Log.d("MainActivity","Pausing the song");
    	this.playPauseListener.onClick(null);
    }    
}