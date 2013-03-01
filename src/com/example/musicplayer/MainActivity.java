package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
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
    TextView songNameTextView;
    
    
    //declaring the media player and song queues 
    MediaPlayer songPlayer;
    ArrayList <Uri> songQueue;
    ArrayList <Long> songArtURLs;
    ArrayList <String> songQueueNames;
    String currentSong;
    int currentSongIndex;
    
    //A global copy of Toast object to show messages to the user
    Toast messageToast;
    
    //declaring the volume controls for the song player
    SeekBar volumeControl;
    AudioManager audioManager;
    int seekBarMin;
    int seekBarMax;
    int minVolume;
    int maxVolume;
    int initialSeekBarPosition;
    
    
    //declaring the variables to display the cover art for the album being played
    Uri albumArtParentUri;
    Uri albumArtUri;
    InputStream songArtStream;
    ContentResolver contentResolver;
    Bitmap songArtBitmap;
    
    //declaring the gesture engine and touch listeners
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
        
        //disable the change in the orientation of the activity on rotating the phone
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        activity = this;
        Log.d("MainActivity","Starting Main Activity");
        
        //setting up the media player and the song queue variables
        songPlayer = new MediaPlayer();
        songPlayer.reset();
        songQueue = new ArrayList<Uri>();
        songQueueNames = new ArrayList<String>();
        currentSongIndex = -1;
        currentSong = null;
        
        //setting up the buttons in the layout
        playPauseButton = (Button) findViewById(R.id.playSongButton);
        previousSongButton = (Button) findViewById(R.id.previousSongButton);
        nextSongButton = (Button) findViewById(R.id.nextSongButton);
        albumButton = (AlbumButton) findViewById(R.id.albumButton);
        albumButton.setParentActivity(this);
        songButton = (SongButton) findViewById(R.id.songsButton);
        songButton.setParentActivity(this);
        artistButton = (ArtistButton) findViewById(R.id.artistButton);
        artistButton.setParentActivity(this);
        backgroundLayout = (LinearLayout) findViewById(R.id.backgroundLayout);
        songImage = (ImageView) findViewById(R.id.songArt);
        songNameTextView = (TextView) findViewById(R.id.currentSongText);
        
        
        //setting up the volume controls
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        volumeControl = (SeekBar) findViewById(R.id.seekBar1);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        minVolume = 0;
        seekBarMin = 0;
        seekBarMax = volumeControl.getMax();
        this.setInitialProgressBar();
        
        messageToast = new Toast(this);
        
        //setting up the variables to show the album art for the song being played
        this.albumArtParentUri = Uri.parse("content://media/external/audio/albumart");
        this.albumArtUri = null;
        this.songArtStream = null;
        this.songArtBitmap = null;
        this.songArtURLs = new ArrayList<Long>();
        
        //the gesture engine recognizes the touch gestures made by the user
        gestureEngine = new GestureEngine(this);
        
        //activityResultProcessing processes the result sent by the child activities, i.e album,song and artist listing
        activityResultProcessing = new ActivityResultProcessing();
        
        
        
        //defining the on click listeners for the music player buttons
        this.nextSongListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				/*
				 * if there has been no song selected, tell user that no song has been selected
				 * and return from the function
				 */
				if (songQueue.size() == 0)
				{
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Please make a selection!", Toast.LENGTH_SHORT);
					messageToast.show();
					songNameTextView.setText("Playing song : None");
					return;
				}
				
				//move on to the next song
				currentSongIndex = (currentSongIndex + 1)%songQueue.size();
				songPlayer.reset();
				try {
					
					//prepare the song player and the set the data source to the new song to be played
					songPlayer.setDataSource(activity,songQueue.get(currentSongIndex));
					songPlayer.prepare();
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Playing Next Song\n" + songQueueNames.get(currentSongIndex),Toast.LENGTH_SHORT);
					messageToast.show();
					songPlayer.start();
					
					//update the song name text on the splash screen
					songNameTextView.setText("Playing Song : " + songQueueNames.get(currentSongIndex));
					
					
					//load the album art for the song
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
				}
				
				//catch errors incase there is problem opening the music file or loading the album art
				
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
				
				//load the default album art if the album art for a song doesn't exist
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
			
				/*
				 * if there has been no song selected, tell user that no song has been selected
				 * and return from the function
				 */
				if (songQueue.size() ==0)
				{
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Please make a song selection!",Toast.LENGTH_SHORT);
					messageToast.show();
					songNameTextView.setText("Playing Song : None");
					return;
				}
				
				//move on to the previous song
				if (currentSongIndex ==0)
					currentSongIndex = songQueue.size()-1;
				else
					currentSongIndex = currentSongIndex - 1;
				
				
				songPlayer.reset();
				try {
					
					//prepare the song player and the set the data source to the new song to be played
					songPlayer.setDataSource(activity, songQueue.get(currentSongIndex));
					songPlayer.prepare();
					messageToast.cancel();
					messageToast = Toast.makeText(activity,"Playing Previous Song\n" + songQueueNames.get(currentSongIndex),Toast.LENGTH_SHORT);
					messageToast.show();
					songPlayer.start();
					
					//update the song name text on the splash screen
					songNameTextView.setText("Playing Song : " + songQueueNames.get(currentSongIndex));
					
					//load the album art for the song
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
	    			
				}
				//catch errors incase there is problem opening the music file or loading the album art
				
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
				
				//load the default album art if the album art for a song doesn't exist
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
					
					// if the song player is playing a song, pause it
					if (songPlayer.isPlaying())
					{
						songPlayer.pause();
						playPauseButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_button));
						messageToast.cancel();
						messageToast = Toast.makeText(activity,"Paused the Playback!",Toast.LENGTH_SHORT);
						messageToast.show();
					}
					// if the song player is paused, resume playing the song player
					else
					{
						songPlayer.start();
						playPauseButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_button));
					}
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
		
		//setup the seek bar for showing the current volume
        volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
        	
        	// increase the volume of playback whenever the progress on the seekbar changes
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

			/**
			 * Display the final volume set to the user
			 */
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				messageToast.cancel();
				messageToast = Toast.makeText(activity,"Volume in increasing to set is " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
				messageToast.show();
			}
        });
        
        
        //initialize the music buttons with their respective on click listeners
        playPauseButton.setOnClickListener(this.playPauseListener);
        previousSongButton.setOnClickListener(this.previousSongListener);
        nextSongButton.setOnClickListener(this.nextSongListener);
        
        
        //setting up the song player
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
        
        //initializing the touch gesture listener for the application
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