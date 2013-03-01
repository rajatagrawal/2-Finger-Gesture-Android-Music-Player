package com.example.musicplayer;

import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureEngine {
		
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
    
    int reverseRightCounter;
    int reverseLeftCounter;
    int reverseSwipeLimit;
    String previousDirection;
    int horizontalMovementCounter,verticalMovementCounter;
    String ss_previousDirection,ss_currentDirection;
    int ss_movementLimit;
    
    OnTouchListener onTouchListener;
    MainActivity parentActivity;
    
    
    public GestureEngine(MainActivity parentActivity)
    {
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
        
        initializeOnTouchListener();
        this.parentActivity = parentActivity;
    }
    
    public void initializeOnTouchListener()
    {
    	onTouchListener = new View.OnTouchListener() {
    		@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if (event.getPointerCount() == 1)
				{
					//Log.d("GestureEngine","Two finger touched");
					if (event.getActionMasked() == MotionEvent.ACTION_MOVE);
						//Log.d("GestureEngine","One Finger is moving");
					else if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
						Log.d("GestureEngine","First finger touched the screen");
					else if (event.getActionMasked() == MotionEvent.ACTION_UP)
					{
						Log.d("GestureEngine","The gesture has been finished");
						return false;
					}
					return true;
				}
				else if (event.getPointerCount() == 2)
				{
					if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
					{
						Log.d("GestureEngine","In action move and swiping left = " + swipingLeft + " and swiping Right is " + swipingRight);
						
						if (tap1Down !=-1)
						{
							Log.d("GestureEngine","The difference in x is " + Math.abs(event.getX() - previousX) + " and difference in y is " + Math.abs(event.getY() - previousY));
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
							Log.d("GestureEngine","in tapping");
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
						//Log.d("GestureEngine","Coming in moving");
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
									Log.d("GestureEngine","SWIPING RIGHT and previous direction was " + previousDirection);
									swipingRight = true;
									if (previousDirection == null)
										previousDirection = "right";
									if (previousDirection.equals("left"))
										reverseLeftCounter = 0;
									previousDirection = "right";
									//Log.d("GestureEngine","Did i enter this loop");
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
										//Log.d("GestureEngine","Always swiping right");
										previousX = event.getX();
										previousY = event.getY();
										swipingRight = true;
										swipingLeft = false;
										return true;
									}
								}
								else if (event.getX() < previousX)
								{
									Log.d("GestureEngine","SWIPING LEFT and previous direction was " + previousDirection);
									swipingLeft = true;
									if (previousDirection == null)
										previousDirection = "left";
									if (previousDirection.equals("right"))
										reverseRightCounter = 0;
									previousDirection = "left";
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
							//Log.d("GestureEngine","Also did scrolling before quitting");
							
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
									Log.d("GestureEngine","GESTURE : Scrolling Down");
									int currentProgress = parentActivity.volumeControl.getProgress();
									parentActivity.volumeControl.setProgress(currentProgress-1);
									double volumeToSet = ((parentActivity.volumeControl.getProgress())/(float)parentActivity.seekBarMax) *parentActivity.maxVolume;
									parentActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int) volumeToSet,AudioManager.FLAG_VIBRATE);
									//audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_VIBRATE);
									//setInitialProgressBar();
								}
								else if (event.getY() < previousY)
								{
									Log.d("GestureEngine","GESTURE : Scrolling Up");
									int currentProgress = parentActivity.volumeControl.getProgress();
									parentActivity.volumeControl.setProgress(currentProgress+1);
									double volumeToSet = ((parentActivity.volumeControl.getProgress())/(float)parentActivity.seekBarMax) *parentActivity.maxVolume;
									parentActivity.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int) volumeToSet,AudioManager.FLAG_VIBRATE);
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
						Log.d("GestureEngine","Second Finger lifted up from the screen");
						reverseRightCounter = 0;
						reverseLeftCounter = 0;
						previousDirection = null;
						horizontalMovementCounter = 0;
						verticalMovementCounter = 0;
						ss_previousDirection = null;
						if (tap1Down !=-1 && tap2Down == -1)
						{
							Log.d("GestureEngine","The time difference is " + (System.currentTimeMillis() - tap1Down));
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
								Log.d("GestureEngine","GESTURE : Double Tap Detected");
								parentActivity.pauseThePlayer();
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
							Log.d("GestureEngine","Finished swiping with fastness is " + Math.abs(event.getX() - initialX)/(float)(System.currentTimeMillis() - initialTime) + " and threshold is " + swipePixels);
							if (Math.abs(event.getX() - initialX)/(float)(System.currentTimeMillis() - initialTime) > swipePixels)
							{
								
								if (event.getX() >= initialX)
								{
									Log.d("GestureEngine","GESTURE Swiped Right");
									parentActivity.playNextSong();
								}
								else
								{
									parentActivity.playPreviousSong();
									Log.d("GestureEngine","GESTURE Swiped Left");
								}
							}
							return true;
						}
						return true;
						
					}
					else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)
					{
						Log.d("GestureEngine","Second finger touched the screennnn");
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
		
			
		};
    }
}