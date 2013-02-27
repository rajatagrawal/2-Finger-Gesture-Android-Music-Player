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
import android.widget.Toast;

public class AlbumButton extends Button {
	
	Activity parentActivity;
	Activity activity;
	CursorAdapter albumListData;
	ListView albumListView;
	Button backButton;
	LinearLayout albumLayout;
	AlbumNamesListing album_list_layout;
	Toast messageToast;

	public AlbumButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AlbumButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("AlbumButton","Album Button Constructor executed");
		this.setOnClickListener(onClickListener);
		messageToast = new Toast(context);
	}
	public void setParentActivity(Activity activity)
	{
		this.parentActivity = activity;
	}

	public AlbumButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	OnClickListener onClickListener = new OnClickListener ()
	{
		public void onClick(View view)
		{
			Intent childIntent = new Intent(parentActivity,AlbumNamesListing.class);
			view.setBackgroundColor(0x88FFFF00);
			parentActivity.startActivityForResult(childIntent,1);
		}
	};
}