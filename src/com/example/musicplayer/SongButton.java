package com.example.musicplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SongButton extends Button {
	
	Activity parentActivity;
	OnClickListener onClickListener;

	public SongButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SongButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Log.d("songButton","Constructor of the song button class executed");
		
		onClickListener = new OnClickListener ()
		{
			public void onClick(View view)
			{
				Intent childIntent = new Intent(parentActivity,SongListLayout.class);
				parentActivity.startActivityForResult(childIntent,1);
			}
		};
		
		this.setOnClickListener(onClickListener);
	}
	public void setParentActivity(Activity activity)
	{
		this.parentActivity = activity;
	}

	public SongButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
}
