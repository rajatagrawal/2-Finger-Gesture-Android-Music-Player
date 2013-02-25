package com.example.musicplayer;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.musicplayer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivityOld extends Activity {
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
    protected void onCreate(Bundle savedInstanceState) {
    	
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        
        activity = this;
        System.out.println("Hi this is a log statement");
        initialX = initialY = finalX = finalY = 0;
        previousX = previousY = 0;
        swiping = scrolling = false;
        swipePixels = (float) 0.03;
        swipingRight = false;
        swipingLeft = false;
        cancelEvent = false;
        
        System.out.println("The current time is " + System.currentTimeMillis());
        
        albumButton albumButton = (albumButton) findViewById(R.id.albumButton);
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
				
				//boolean gestureReturn = false;
				//System.out.println("Application finger touch count is " + event.getPointerCount());
				/*if (event.getPointerCount() == 1)
				{
					System.out.println("One finger touched");
					//gestureDetector.onTouchEvent(event);
					System.out.println("Action mask is " + event.getActionMasked());
					return true;
				}*/
				
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
					if (cancelEvent)
					{
						event.setAction(MotionEvent.ACTION_CANCEL);
					}
					if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
					{
						event.setAction(MotionEvent.ACTION_CANCEL);
						//System.out.println("Two fingers are moving");
						//System.out.println("Previous X is " + initialX + " and previous Y is " + initialY);
						//System.out.println("Current X is " + event.getX() + " and current y is " + event.getY());
						
						if (Math.abs(event.getX() - previousX) > Math.abs(event.getY() - previousY))
						{
							//System.out.println("X difference is greater and is equal to " + Math.abs(event.getX() - previousX));
							//check if previous not scrolling
							if (scrolling == true)
							{
								cancelEvent = true;
								scrolling = false;
								return false;
							}
							swiping = true;
							if (event.getX() > previousX)
							{
								/*if (swipingLeft == true)
								{
									swiping = false;
									if ((Math.abs(event.getX() - initialX)/(System.currentTimeMillis() - initialTime)) > swipePixels)
									{
										System.out.println("Swiped Left 1");
										//return true;
									}
									initialX = event.getX();
									initialY = event.getY();
									initialTime = System.currentTimeMillis();
									swipingLeft = false;
									
									//else
										//return false;
								}*/
								swipingRight = true;
							}
							else
							{
								/*if (swipingRight == true)
								{
									// I have finished processing the swipe feature
									swiping =false;
									
									//check if the gesture was completed.
									if ((Math.abs(event.getX() - initialX)/(System.currentTimeMillis() - initialTime)) > swipePixels)
									{
										System.out.println("Swiped right 1");
										//return true;
									}
									swipingRight = false;
									//System.out.println("The threshold is " + (Math.abs(event.getX() - initialX)/(System.currentTimeMillis() - initialTime)));
									initialX = event.getX();
									initialY = event.getY();
									initialTime = System.currentTimeMillis();
								}*/
								swipingLeft = true;
							}
							
							
						}
						else
						{
							//System.out.println(" y difference is greater and is " + Math.abs(event.getY() - previousY));
							
							//check if the previously swiping was being processed
							if (swiping == true)
							{
								//finished processing swipe
								swiping = false;
								cancelEvent = true;
								return false;
								
								
								/*
								//store the references of previous x and previous y for calculation in the amount of scrolling
								//from here on
								previousX = event.getX();
								previousY = event.getY();
								
								//and reinitialize the initial value of x and y for new gesture processing
								
								
								if ((Math.abs(event.getX() - initialX)/(System.currentTimeMillis() - initialTime)) > swipePixels)
								{
									if (swipingRight == true)
									{
										System.out.println("Swiping Right 2");
										swipingRight = false;
									}
									if (swipingLeft == true)
									{
										System.out.println("Swiping Left 2");
										swipingLeft =false;
									}
									//return true;
								}
								else
								{
									swipingRight = false;
									swipingLeft = false;
								}
								//System.out.println("After swiping 2");
								initialX = event.getX();
								initialY = event.getY();
								initialTime = System.currentTimeMillis();
								*/
							}	//check if swiping was processed fully
							
							
							scrolling = true;
							//System.out.println("Setting scrolling true and the value of swiping is " + swiping);
							if (Math.abs(event.getY() - previousY) > 3)
								if (event.getY() > previousY)
								System.out.println("Scrolling Down");
							else
								System.out.println("Scrolling Up");
							previousX = event.getX();
							previousY = event.getY();
						}
						previousX = event.getX();
						previousY = event.getY();
						
					}
					else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP)
					{
						System.out.println("Second Finger lifted up from the screen");
						finalX = event.getX();
						finalY = event.getY();
						if (swiping)
						{
							System.out.println("We were swiping before");
							//finished the processing of swiping
							swiping = false;
							
							if ((Math.abs(finalX - initialX)/(System.currentTimeMillis() - initialTime))>swipePixels)
							{
								System.out.print(" Swipes were detected");
								if (swipingRight)
									System.out.println("Swiped Right 3");
								if (swipingLeft)
									System.out.println("Swiped Left 3");
								
								swipingRight = false;
								swipingLeft = false;
								return true;
							}
							else
							{
								System.out.println("Swipes were not detected with threshold " + (Math.abs(finalX - initialX)/(System.currentTimeMillis() - initialTime)));
								// no gesture detected since the gesture was very slow
								swipingLeft = false;
								swipingRight = false;
								return false;
							}
						}
						else if (scrolling)
						{
							System.out.println("We were scrolling");
							//scrolling processing is now finished
							scrolling = false;
							return true;
						}
							
						
						System.out.println("Initial X = " + initialX + " and initial Y is " + initialY);
						System.out.println("Final X = " + finalX + " and final Y is " + finalY);
					}
					else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
					{	System.out.println("Second finger touched the screennnn");
						initialX = event.getX();
						initialY = event.getY();
						initialTime = System.currentTimeMillis();
						swipingRight = false;
						swipingLeft = false;
						
						//store the first values of previous X's and previous Y's
						previousX = event.getX();
						previousY = event.getY();
					}
					else if (event.getActionMasked() == MotionEvent.ACTION_CANCEL)
					{
						System.out.println("Cancelled the event");
						//return true;
						cancelEvent = false;
						return true;
					}
				}
				else
					return false;
				/*
				else
				{
					//System.out.println("No gesture is being recognized");
					return true;
				}*/
				//System.out.println("The value returned by gesture detector is " + gestureReturn);
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	System.out.println("Activity just returned to the parent activity");
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
