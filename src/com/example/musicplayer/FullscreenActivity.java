package com.example.musicplayer;

import java.io.File;
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
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
public class FullScreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    int counter = 0;
    private GestureDetector gestureDetector;
    ImageView songImage;
    Activity activity;
    float initialX;
    float initialY;
    float finalX;
    float finalY;
    float previousX;
    float previousY;
    Boolean swiping,scrolling;
    Boolean swipingRight;
    Boolean swipingLeft;
    Boolean scrollingUp;
    long initialTime;
    float swipePixels;
    boolean cancelEvent;
    long tap1Up,tap1Down;
    long tap2Up,tap2Down;
    long tapTimeLimit;
    MediaPlayer songPlayer;
    Button playPauseButton;
    Button previousSongButton;
    Button nextSongButton;
    AlbumButton albumButton;
    SongButton songButton;
    ArrayList <Uri> songQueue;
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
    int reverseRightCounter;
    int reverseLeftCounter;
    int reverseSwipeLimit;
    String previousDirection;
    int horizontalMovementCounter,verticalMovementCounter;
    String ss_previousDirection,ss_currentDirection;
    int ss_movementLimit;
    View.OnClickListener nextSongListener;
    View.OnClickListener previousSongListener;
    View.OnClickListener playPauseListener;
    OnGestureListener flingGesture = new OnGestureListener()
    {
	    @Override
	    public boolean onDown(MotionEvent e1)
	    {
	    	System.out.println("this is a on down event and number of fingers is " + e1.getPointerCount());
	    	System.out.println("Value of action masked is " + e1.getActionMasked());
	    	if (e1.getActionMasked() == MotionEvent.ACTION_MOVE)
	    		System.out.println(" I am moving my finger after touching");
	    	
	    	return true;
	    }
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float x, float y)
	    {
	    	System.out.println("getactionmasked returns in fling : " + e2.getActionMasked());
	    	if (e2.getActionMasked() == MotionEvent.ACTION_MOVE)
	    		System.out.println("I am moving my finger");
	    	System.out.println("Numbe of fingers in fling is beginning :  " + e1.getPointerCount() + " end : " + e2.getPointerCount());
	    	System.out.println("The x velocity is " + x + " and the y velocity is " + y);
	    	
	    	if (Math.abs(e2.getX() - e1.getX()) > Math.abs(e2.getY() - e1.getY()))
	    	{
	    		
	    		if (x >= 0)
	    			System.out.println("Right Swipe");
	    		else if (x<=0)
	    			System.out.println("Left Swipe");
	    		
	    	}
	    	else if (Math.abs(e2.getY() - e1.getY()) > Math.abs(e2.getX() - e1.getX()))
	    	{
	    		
	    		if (y >= 0)
	    		{
	    			System.out.println("Down Swipe");
	    		}
	    		else if (y<=0)
	    			System.out.println("Up Swipe");
	    	}
	    	/*if (e2.getPointerCount() == 1)
	    		System.out.println("One finger fling");
	    	else if (e2.getPointerCount() == 2)
	    		System.out.println("Two finger fling");
	    	*/
	    	//System.out.println("this is a fling gesture");
	    	return true;
	    }
	    @Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float x, float y)
	    {
	    	
	    	System.out.println("Finger count in scoll is " + e1.getPointerCount() + " and second finger count is " + e2.getPointerCount());
	    	
	    	//System.out.println("X is " + x + " and y is " + y);
	    	if (x >=0 && y >=0)
	    	{
	    		if (Math.abs(x)>=Math.abs(y))
	    			System.out.println("Left Scroll");
	    		else
	    			System.out.println("Up Scroll");
	    		
	    		/*
	    		if ((e2.getX() - e1.getX()) >= 0)
	    			System.out.println("Right Scroll");
	    		else if ((e2.getX() - e1.getX()) <=0)
	    			System.out.println("Left Scroll");
	    		*/
	    		
	    	}
	    	else if (x>=0 && y<=0)
	    	{
	    		if (Math.abs(x)>=Math.abs(y))
	    			System.out.println("Left Scroll");
	    		else
	    			System.out.println("Down Scroll");
	    	}
	    	else if (x<=0 && y>=0)
	    	{
	    		if (Math.abs(x) >= Math.abs(y))
	    			System.out.println("RightScroll");
	    		else
	    			System.out.println("Up Scroll");
	    	}
	    	else if (x<=0 && y <=0)
	    	{
	    		if (Math.abs(x)>= Math.abs(y))
	    			System.out.println("Right Scroll");
	    		else
	    			System.out.println("Down Scroll");
	    	}	    	
	    	
	    	//System.out.println("Action masked is " + e2.getActionMasked());
	    	//System.out.println("Scroll Event and the number of fingers is " + e2.getPointerCount());
	    	return true;
	    }
	    /*
	    @Override
	    public boolean onTouch()
	    {
	    	return true;
	    }
	    */
		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			//return true;
			
		}
		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return true;
		}
    };
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
        System.out.println("Hi this is a log statement");
        initialX = initialY = finalX = finalY = 0;
        previousX = previousY = 0;
        swiping = scrolling = false;
        swipePixels = (float) 0.15;
        swipingRight = false;
        swipingLeft = false;
        cancelEvent = false;
        tap1Up = -1;
        tap1Down = -1;
        tap2Up = -1;
        tap2Down = -1;
        tapTimeLimit = (long) 1000;
        reverseRightCounter = 0;
        reverseLeftCounter = 0;
        reverseSwipeLimit = 4;
        previousDirection = null;
        horizontalMovementCounter = verticalMovementCounter = 0;
        ss_previousDirection = ss_currentDirection = null;
        ss_movementLimit = 3;
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
		};
		this.playPauseListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				System.out.println("Play pause button clicked");
				try
				{
					if (songPlayer.isPlaying())
						songPlayer.pause();
					else
						songPlayer.start();
				}
				catch(NullPointerException e)
				{
					System.out.println("The media player hasn't been initialized yet");
				}
				finally
				{
					System.out.println("Please make a selection");
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
        /*playPauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Play pause button clicked");
				try
				{
					if (songPlayer.isPlaying())
						songPlayer.pause();
					else
						songPlayer.start();
				}
				catch(NullPointerException e)
				{
					System.out.println("The media player hasn't been initialized yet");
				}
				finally
				{
					System.out.println("Please make a selection");
				}
				
			}
		});*/
        
        previousSongButton.setOnClickListener(this.previousSongListener);
        /*previousSongButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Previous button clicked");
				
				System.out.println("Current song index is " + currentSongIndex);
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
		});*/
        
        nextSongButton.setOnClickListener(this.nextSongListener);
        /*nextSongButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Next Button Clicked");
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
		});*/
        
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
				
				System.out.println("Came in the on error listener");
				messageToast.cancel();
				messageToast = Toast.makeText(activity,"There is an error with the music player!",Toast.LENGTH_SHORT);
				messageToast.show();
				return true;
			}
		});
        
        System.out.println("The current time is " + System.currentTimeMillis());
        
        albumButton = (AlbumButton) findViewById(R.id.albumButton);
        albumButton.setParentActivity(this);
        
        songButton = (SongButton) findViewById(R.id.songsButton);
        songButton.setParentActivity(this);
        songImage = (ImageView) findViewById(R.id.songArt);
        if (songImage == null)
        	System.out.println("Song Image is null");
        else
        	System.out.println("Song Image is not null");
        //albumButton.setOnClickListener(albumButtonClickListener);
        

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        //final View contentView = findViewById(R.id.fullscreen_content);
        
        gestureDetector = new GestureDetector(this.getApplicationContext(),flingGesture);
        
        songImage.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if (event.getPointerCount() == 1)
				{
					//System.out.println("Two finger touched");
					if (event.getActionMasked() == MotionEvent.ACTION_MOVE);
						//System.out.println("One Finger is moving");
					else if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
						System.out.println("First finger touched the screen");
					else if (event.getActionMasked() == MotionEvent.ACTION_UP)
					{
						System.out.println("The gesture has been finished");
						return false;
					}
					
					/*else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
						System.out.println("Second finger touched the screen");*/
					return true;
					//System.out.println(" the value by gesture detector is " + gestureDetector.onTouchEvent(event));
					//return gestureDetector.onTouchEvent(event);
				}
				else if (event.getPointerCount() == 2)
				{
					if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
					{
						System.out.println("In action move and swiping left = " + swipingLeft + " and swiping Right is " + swipingRight);
						
						if (tap1Down !=-1)
						{
							System.out.println("The difference in x is " + Math.abs(event.getX() - previousX) + " and difference in y is " + Math.abs(event.getY() - previousY));
							if (Math.abs(event.getX() - previousX) > 5 || Math.abs(event.getY() - previousY) > 3)
							{
								tap1Down = -1;
								tap1Up = -1;
								tap2Up = -1;
								tap2Down = -1;
								previousX = event.getX();
								previousY = event.getY();
								return true;
								
							}
							else
							{
								previousX = event.getX();
								previousY = event.getY();
							}
							System.out.println("in tapping");
							if ((System.currentTimeMillis() - tap1Down) < tapTimeLimit)
								return true;
							else if (tap1Up == -1)
							{
								tap1Down = -1;
								tap1Up = -1;
								tap2Up = -1;
								tap2Down = -1;
							}
							else if (tap1Up !=-1)
							{
								if (tap2Down == -1)
								{
									if ((System.currentTimeMillis() - tap1Up) < (tapTimeLimit/3))
										return true;
									else
									{
										tap1Up = -1;
										tap1Down = -1;
										tap2Up = -1;
										tap2Down = -1;
									}
										
								}
								else if (tap2Down !=-1)
								{
									if ((System.currentTimeMillis() - tap2Down) < tapTimeLimit)
										return true;
									else
									{
										tap1Up = -1;
										tap1Down = -1;
										tap2Up = -1;
										tap2Down = -1;
									}
								}
							}
						}
						//System.out.println("Coming in moving");
						if (Math.abs(event.getX() - previousX)>= Math.abs(event.getY() - previousY))
						{
							
							if (ss_currentDirection == null)
								ss_currentDirection = "horizontal";
							
							if (ss_previousDirection == null)
								ss_previousDirection = "horizontal";
							if (ss_previousDirection.equals("vertical"))
								verticalMovementCounter = 0;
							ss_previousDirection = "horizontal";
							
							if (ss_currentDirection == "vertical")
							{
								horizontalMovementCounter++;
								if (horizontalMovementCounter<=ss_movementLimit)
								{
									previousX = event.getX();
									previousY = event.getY();
									return true;
								}
								else
								{
									ss_currentDirection = "horizontal";
									horizontalMovementCounter = 0;
									verticalMovementCounter = 0;
									initialX = previousX = event.getX();
									initialY = previousY = event.getY();
									initialTime = System.currentTimeMillis();
								}
							}
							else if (ss_currentDirection == "horizontal")
							{
								if (event.getX() >= previousX)
								{
									System.out.println("SWIPING RIGHT and previous direction was " + previousDirection);
									swipingRight = true;
									if (previousDirection == null)
										previousDirection = "right";
									if (previousDirection.equals("left"))
										reverseLeftCounter = 0;
									previousDirection = "right";
									//System.out.println("Did i enter this loop");
									if (swipingLeft == true)
									{
										
										reverseRightCounter++;
										if (reverseRightCounter <= reverseSwipeLimit)
										{
											swipingRight = false;
											swipingLeft = true;
											previousX = event.getX();
											previousY = event.getY();
											return true;
										}
										else if (reverseRightCounter > reverseSwipeLimit)
										{
											swipingRight = true;
											swipingLeft = false;
											reverseRightCounter = 0;
											reverseLeftCounter = 0;
											initialX = event.getX();
											initialY = event.getY();
											previousX = event.getX();
											previousY = event.getY();
											initialTime = System.currentTimeMillis();
											return true;
										}
									}
									if (swipingRight == true)
									{
										//System.out.println("Always swiping right");
										previousX = event.getX();
										previousY = event.getY();
										swipingRight = true;
										swipingLeft = false;
										return true;
									}
								}
								else if (event.getX() < previousX)
								{
									System.out.println("SWIPING LEFT and previous direction was " + previousDirection);
									swipingLeft = true;
									if (previousDirection == null)
										previousDirection = "left";
									if (previousDirection.equals("right"))
										reverseRightCounter = 0;
									previousDirection = "left";
									//System.out.println(" did try to swipe left once");
									if (swipingRight == true)
									{
										reverseLeftCounter++;
										if (reverseLeftCounter <= reverseSwipeLimit)
										{
											swipingRight = true;
											swipingLeft = false;
											previousX = event.getX();
											previousY = event.getY();
											return true;
										}
										else if (reverseLeftCounter>reverseSwipeLimit)
										{
											swipingLeft = true;
											swipingRight = false;
											reverseRightCounter = 0;
											reverseLeftCounter = 0;
											initialX = event.getX();
											initialY = event.getY();
											previousX = event.getX();
											previousY = event.getY();
											initialTime = System.currentTimeMillis();
											return true;
										
										}
									}
									else if (swipingLeft == true)
									{
										previousX = event.getX();
										previousY = event.getY();
										swipingLeft = true;
										swipingRight = false;
										return true;
									}
								}
							}
							
						}
						else if (Math.abs(event.getX() - previousX) < Math.abs(event.getY() - previousY))
						{
							//System.out.println("Also did scrolling before quitting");
							
							if (ss_currentDirection == null)
								ss_currentDirection = "vertical";
							
							if (ss_previousDirection == null)
								ss_previousDirection = "vertical";
							if (ss_previousDirection.equals("horizontal"))
								horizontalMovementCounter = 0;
							ss_previousDirection = "vertical";
							
							if (ss_currentDirection.equals("horizontal"))
							{
								verticalMovementCounter++;
								if (verticalMovementCounter <=ss_movementLimit)
								{
									previousX = event.getX();
									previousY = event.getY();
									verticalMovementCounter++;
									return true;
								}
								else
								{
									ss_currentDirection = "vertical";
									horizontalMovementCounter = 0;
									verticalMovementCounter = 0;
									initialX = previousX = event.getX();
									initialY = previousY = event.getY();
									initialTime = System.currentTimeMillis();
								}
									
							}
							else if (ss_currentDirection.equals("vertical"))
							{
								if (event.getY()>= previousY)
								{
									System.out.println("GESTURE : Scrolling Down");
									int currentProgress = volumeControl.getProgress();
									volumeControl.setProgress(currentProgress-1);
									double volumeToSet = ((volumeControl.getProgress())/(float)seekBarMax) *maxVolume;
									audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int) volumeToSet,AudioManager.FLAG_VIBRATE);
									//audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_VIBRATE);
									//setInitialProgressBar();
								}
								else if (event.getY() < previousY)
								{
									System.out.println("GESTURE : Scrolling Up");
									int currentProgress = volumeControl.getProgress();
									volumeControl.setProgress(currentProgress+1);
									double volumeToSet = ((volumeControl.getProgress())/(float)seekBarMax) *maxVolume;
									audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int) volumeToSet,AudioManager.FLAG_VIBRATE);
								}
								previousX = event.getX();
								previousY = event.getY();
								return true;
							}
							
						}
						return true;
					}
					else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
					{
						System.out.println("Second Finger lifted up from the screen");
						reverseRightCounter = 0;
						reverseLeftCounter = 0;
						previousDirection = null;
						horizontalMovementCounter = 0;
						verticalMovementCounter = 0;
						ss_previousDirection = null;
						if (tap1Down !=-1 && tap2Down == -1)
						{
							System.out.println("The time difference is " + (System.currentTimeMillis() - tap1Down));
							if ((System.currentTimeMillis() - tap1Down) < tapTimeLimit)
								tap1Up = System.currentTimeMillis();
							else
							{
								tap1Up = -1;
								tap1Down = -1;
								tap2Up = -1;
								tap2Down = -1;
							}
							
						}
						else if (tap1Down !=-1 && tap2Down !=-1)
						{
							if ((System.currentTimeMillis() - tap2Down) < tapTimeLimit)
							{
								System.out.println("GESTURE : Double Tap Detected");
								pauseThePlayer();
								tap1Up = -1;
								tap1Down = -1;
								tap2Up = -1;
								tap2Down = -1;
								return true;
							}
							else
							{
								tap1Up = -1;
								tap1Down = -1;
								tap2Up = -1;
								tap2Down = -1;
							}
								
						}
						if (ss_currentDirection!= null && ss_currentDirection.equals("horizontal"))
						{
							System.out.println("Finished swiping with fastness is " + Math.abs(event.getX() - initialX)/(float)(System.currentTimeMillis() - initialTime) + " and threshold is " + swipePixels);
							if (Math.abs(event.getX() - initialX)/(float)(System.currentTimeMillis() - initialTime) > swipePixels)
							{
								
								if (event.getX() >= initialX)
								{
									System.out.println("GESTURE Swiped Right");
									playNextSong();
								}
								else
								{
									playPreviousSong();
									System.out.println("GESTURE Swiped Left");
								}
								//System.out.println("came inside if and swiping right is "+ swipingRight + " and swiping left is " + swipingLeft);
								/*if (swipingRight == true)
								{
									System.out.println("GESTURE Swiping Right 3");
									playNextSong();
								}
								else if (swipingLeft == true)
								{
									System.out.println("GESTURE Swiping Left 3");
									playPreviousSong();
								}*/
							}
							//System.out.println("Didn't come inside if");
							return true;
						}
						//System.out.println("scrolling when lifted finger up");
						return true;
						
					}
					else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
					{
						System.out.println("Second finger touched the screennnn");
						if (tap1Down == -1)
							tap1Down = System.currentTimeMillis();
						else if (tap2Down == -1 && (System.currentTimeMillis() - tap1Up) < (tapTimeLimit/3))
							tap2Down = System.currentTimeMillis();
						else
						{
							tap1Up = -1;
							tap2Up = -1;
							tap1Down = -1;
							tap2Down = -1;
						}
						initialX = event.getX();
						initialY = event.getY();
						previousX = event.getX();
						previousY = event.getY();
						swiping = false;
						scrolling = false;
						swipingLeft = false;
						swipingRight = false;
						initialTime = System.currentTimeMillis();
						
						reverseRightCounter = 0;
						reverseLeftCounter = 0;
						previousDirection = null;
						
						ss_currentDirection = null;
						ss_previousDirection = null;
						horizontalMovementCounter = 0;
						verticalMovementCounter = 0;
						return true;
					}
					
				}
				return true;
			}
		});
        
        //loadFileSystem();

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        //mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        /*
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });
		
        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
                System.out.println("the main screen is touched");
            }
        });
        */
        /*contentView.setOnTouchListener(new View.OnTouchListener()
        {
        	@Override
        	public boolean onTouch(final View view, final MotionEvent me)
        	{
        		System.out.println("content view touch listener");
        		boolean valueGesture = gestureDetector.onTouchEvent(me);
        		System.out.println("the value returned by gesture listener is " + valueGesture);
        		return valueGesture;
        	}
        });*/
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }
    public void setInitialProgressBar()
    {
    	int initialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	volumeControl.setProgress((int)((initialVolume/(float)maxVolume)*seekBarMax));
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if (resultCode == Activity.RESULT_CANCELED && (requestCode == 1 || requestCode == 2))
    		return;
    	System.out.println("Activity just returned to the parent activity");
    	System.out.println("The album selected is " + data.getStringExtra("albumName"));
    	System.out.println("The selected song is " + data.getStringExtra("songName"));
    	ContentResolver contentResolver = this.getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	Uri albumArtBasicUri = Uri.parse("content://media/external/audio/albumart");
    	String [] projection=
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.DATA,
    			MediaStore.Audio.Media.ALBUM,
    			MediaStore.Audio.Media.TITLE,
    			MediaStore.Audio.Media.ALBUM_ID
    		};
    	String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0 " + "AND " + MediaStore.Audio.Media.ALBUM + " LIKE ?";
    	String [] arguments = {
    			data.getStringExtra("albumName")
    	};
    	Uri albumArtUri;
    	//albumArtUri = ContentUris.withAppendedId(albumArtBasicUri,data.getLongArrayExtra("albumName"));
    	Cursor cursor = contentResolver.query(uri,projection, selection, arguments,MediaStore.Audio.Media.TITLE);
    	System.out.println("After doing the cursor in the first activity");
    	if (cursor == null)
    		System.out.println("There is an error getting the song");
    	else if (cursor.getCount() == 0)
    		System.out.println("The album songs are not found in the library");
    	else
    	{
    		cursor.moveToNext();
    		songQueue.clear();
    		this.songQueueNames.clear();
    		albumArtUri = ContentUris.withAppendedId(albumArtBasicUri,cursor.getLong(4));
    		InputStream songArtStream = null;
			try {
				songArtStream = contentResolver.openInputStream(albumArtUri);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (songArtStream != null)
			{
					Bitmap songArtBitmap = BitmapFactory.decodeStream(songArtStream);
					this.songImage.setImageBitmap(songArtBitmap);
			}
			else
			{
				this.songImage.setImageBitmap(null);
				this.songImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.background1));
			}
    		
    		for (int i=0;i<cursor.getCount();i++)
    		{
    			if (cursor.getString(3).equalsIgnoreCase(data.getStringExtra("songName")) )
    			{
    				System.out.println("Came in if");
    				currentSongIndex = i;
    				currentSong = cursor.getString(3);
    			}
    			songQueue.add(Uri.parse(cursor.getString(1)));
    			songQueueNames.add(cursor.getString(3));
    			cursor.moveToNext();
    		}
    		System.out.println("The current song index is " + currentSongIndex + " and the name of the song is " + currentSong);
    		if (songQueue.size() == 0)
    		{
    			messageToast.cancel();
    			messageToast = Toast.makeText(this,"There is an error retrieving song files from the library",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    		}
    		try {
    			songPlayer.reset();
    			songPlayer.setDataSource(this,songQueue.get(currentSongIndex));
				songPlayer.prepare();
				//messageToast.cancel();
				messageToast = Toast.makeText(this,"Playing song" + songQueueNames.get(currentSongIndex), Toast.LENGTH_LONG);
				messageToast.show();
				songPlayer.start();
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
    		catch(Exception error)
    		{
    			messageToast.cancel();
    			messageToast =Toast.makeText(this,"Sorry there was an error playing this song",Toast.LENGTH_SHORT);
    			messageToast.show();
    			return;
    			
    		}
    		System.out.println("Finished setting up the source");
    	}
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
    	//System.out.println("MAIN SCREEN TOUCHED");
    	return true;
    	//return gestureDetector.onTouchEvent(me);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    /*
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            System.out.println("a button has been pressed" + (counter++));
            return false;
        }
    };*/

    /*
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };*/

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    /*
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }*/
    void playNextSong()
    {
    	System.out.println("Playing next song");
    	this.nextSongListener.onClick(null);
    	//currentSongIndex = (currentSongIndex + 1)%songQueue.size();
    	/*songPlayer.reset();
    	try {
			songPlayer.setDataSource(this,songQueue.get(currentSongIndex));
			songPlayer.prepare();
			messageToast.cancel();
			messageToast = Toast.makeText(this,"Playing next song\n" + songQueueNames.get(currentSongIndex),Toast.LENGTH_SHORT);
			messageToast.show();
			songPlayer.start();
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
		}*/
    }
    void playPreviousSong()
    {
    	System.out.println("Playing previous song");
    	this.previousSongListener.onClick(null);
    	/*if (currentSongIndex == 0)
    		currentSongIndex = songQueue.size()-1;
    	else
    		currentSongIndex = currentSongIndex - 1;*/
    	/*
    	try {
			songPlayer.setDataSource(this,songQueue.get(currentSongIndex));
			songPlayer.prepare();
			messageToast.cancel();
			messageToast = Toast.makeText(this,"Playing previous Song\n" + songQueue.get(currentSongIndex), Toast.LENGTH_SHORT);
			messageToast.show();
			songPlayer.start();
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
		}*/
    }
    void pauseThePlayer()
    {
    	System.out.println("Pausing the song");
    	this.playPauseListener.onClick(null);
    	/*
    	if (songPlayer.isPlaying())
    	{
    		songPlayer.pause();
    		messageToast.cancel();
    		messageToast = Toast.makeText(this,"Paused the song playback",Toast.LENGTH_SHORT);
    		messageToast.show();
    	}
    	else
    	{
    		songPlayer.start();
    		messageToast.cancel();
    		messageToast = Toast.makeText(this,"Resumed playback",Toast.LENGTH_SHORT);
    		messageToast.show();
    	}*/
    }
    
    private void loadFileSystem()
    {
    	
    	System.out.println("reading file system");
    	String memoryState = Environment.getExternalStorageState();
    	if (memoryState.equals(Environment.MEDIA_MOUNTED)|| memoryState.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
    		System.out.println("Media is mounted and can be read");
    	else
    	{
    		System.out.println("cannot read media");
    		System.out.println(memoryState);
    	}
    	
    	//get the path of the music directory
    	File musicDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    	System.out.println("File path is " + musicDirectoryPath.getPath());
    	if (!musicDirectoryPath.mkdirs())
    		System.out.println("The file folder exists");
    	else
    		System.out.println("The file folder didn't exist");
    	
    	File [] musicFile = musicDirectoryPath.listFiles();
    	
    	if (musicFile == null)
    		System.out.println("music file directory is null");
    	else
    	{
    		System.out.println("the length of music files is " + musicFile.length);
    	
	    	if (musicFile.length == 0)
	    		System.out.println("There are no files in the directory");
	    	else
	    	{
	    		for (int i=0;i<musicFile.length;i++)
	    	
	    		{
	    			System.out.println(musicFile[i].getPath());
	    		}
	    	}
    	}
    	ContentResolver contentResolver = getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	//Uri uri = Uri.parse(musicDirectoryPath.getPath());
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media.ARTIST
    		};
    	//android.provider.MediaStore.Audio.Media.
    	System.out.println("The uri is "+uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,null,selection,null,MediaStore.Audio.Media.TITLE);
    	if (cursor == null)
    	{
    		System.out.println("Cursor error returning from the program");
    	}
    	else if (cursor.getCount() == 0)
    	{
    		System.out.println("There are no rows returned");
    	}
    	else
    	{
    		System.out.println("Number of rows are " + cursor.getCount() + " and column is " + cursor.getColumnName(0));
    		for (int i=0;i<cursor.getColumnCount();i++)
    			System.out.println("ColumnName : " + cursor.getColumnName(i));
    		
    		//cursor.moveToNext();
    		while(cursor.moveToNext())
    		{
    			System.out.println("Title is " + cursor.getString(23));
    		}
    		//cursor.moveToNext();
    		//System.out.println("the song title is " + cursor.getString(0));
    		/*
    		for (int i=0;i<cursor.getCount();i++)
    			System.out.println("Found row " + cursor.getString(0));
    			cursor.moveToNext();
    		*/
    	}
    }
}
