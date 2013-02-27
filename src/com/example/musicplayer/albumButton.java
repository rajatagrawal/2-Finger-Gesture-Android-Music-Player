package com.example.musicplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class albumButton extends Button {
	
	Activity parentActivity;
	Activity activity;
	CursorAdapter albumListData;
	ListView albumListView;
	Button backButton;
	LinearLayout albumLayout;
	albumListLayout album_list_layout;
	Toast messageToast;

	public albumButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public albumButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println("Constructor without def style executed");
		//parentActivity = (Activity)context;
		this.setOnClickListener(onClickListener);
		//this.setBackground(getResources().getDrawable(R.drawable.background));
		/*this.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.setBackgroundColor(0x88FFFF00);
				return false;
			}
		});*/
		messageToast = new Toast(context);
		//this.setBackgroundResource(context.getResources().getIdentifier("buttonstates","drawable", context.getPackageName()));
		// TODO Auto-generated constructor stub
	}
	public void setParentActivity(Activity activity)
	{
		this.parentActivity = activity;
	}

	public albumButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	OnClickListener onClickListener = new OnClickListener ()
	{
		public void onClick(View view)
		{
			Intent childIntent = new Intent(parentActivity,albumListLayout.class);
			view.setBackgroundColor(0x88FFFF00);
			parentActivity.startActivityForResult(childIntent,1);
		}
	};
	private void loadFileSystem()
    {
    	ContentResolver contentResolver = activity.getContentResolver();
    	//Uri uri = Uri.withAppendedPath(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,"Beatles - All I've Gotta Do");
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.ALBUM
    		};
    	String [] columnProjection = 
    		{
    			MediaStore.Audio.Media.ALBUM
    		};
    	int [] to = new int [] {R.id.albumName};
    	System.out.println("The uri is "+uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,null,MediaStore.Audio.Media.ALBUM);
    	if (cursor == null)
    	{
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		System.out.println("There was an error reading music files from the music library.");
    		activity.setContentView(R.layout.album_layout);
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		System.out.println("There are no music files present in the library!");
    	}
    	else
    	{
    		albumListData = new SimpleCursorAdapter(activity.getApplicationContext(),R.layout.album_name_view,cursor,columnProjection,to);
    		//albumListData = new CursorAdapter(activity.getApplicationContext(),cursor);
    		if (albumListData == null)
    			System.out.println("album list data is null");
    		if (albumListView == null)
    		{
    			System.out.println("album list view is null");
    			//albumListView = (ListView) findViewById(R.id.albumList1);
    			System.out.println("after assigning list view");
    		}
    		//albumListView.setAdapter(albumListData);
    		album_list_layout.albumListView.setAdapter(albumListData);
    		//activity.setContentView(R.layout.album_layout);
    		System.out.println("Number of rows are " + cursor.getCount() + " and column is " + cursor.getColumnName(0));
    		//for (int i=0;i<cursor.getColumnCount();i++)
    			//System.out.println("ColumnName : " + cursor.getColumnName(i));
    		
    		//cursor.move(-30);
    		while(cursor.moveToNext())
    		{
    			System.out.println("Album is " + cursor.getString(0));
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
