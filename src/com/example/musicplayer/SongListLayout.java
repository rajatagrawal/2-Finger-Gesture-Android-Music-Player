package com.example.musicplayer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class SongListLayout extends Activity{

	ListView albumListView;
	Button backButton;
	ArrayAdapter<String> albumListData;
	ArrayList<String>albumNames;
	Activity activity;
	
	
	/*public albumListLayout(Context context) {
		super(context);
		albumListViews = new ListView(context);
		backButton = new Button(context);
		this.setOrientation(VERTICAL);
		this.addView(albumListViews);
		this.addView(backButton);
		this.setupButtonLayout();
		this.setupalbumListViews();
		System.out.println("This constructor called");
		// TODO Auto-generated constructor stub
	}*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		
		System.out.println("In child activity");
		albumListView = (ListView)findViewById(R.id.albumList1);
		activity = this;
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		albumNames = new ArrayList<String>();
		if (albumListView == null || backButton == null)
		{
			System.out.println("There is an error reading the layout structure. Quitting selection of album.");
			
			// error messages on the intent that is returned to the main activity
			finish();
		}
		this.setupButtonLayout();
		albumListView.setClickable(true);
		albumListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//System.out.println("The item clicked is " + arg2);
				System.out.println("The selected item is "+ albumListView.getAdapter().getItem(arg2));
				Intent childIntent = new Intent(activity,albumListing.class);
				childIntent.putExtra("albumName", albumListView.getAdapter().getItem(arg2).toString());
				activity.startActivityForResult(childIntent, 1);
			
			}
			
		});
		loadFileSystem();
	}
	
	/*
	public albumListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println(" This constructor executed");
		// TODO Auto-generated constructor stub
	}

	public albumListLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/
	public void setupalbumListViews()
	{
		//LayoutParams albumListParams = new LayoutParams(LayoutParams.FILL_PARENT,170);
		//albumListViews.setLayoutParams(albumListParams);
		
		/*albumListViews.setLeft(this.getLeft());
		albumListViews.setRight(this.getRight());
		albumListViews.setTop(this.getTop());
		albumListViews.setBottom(this.backButton.getTop());
		*/
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		System.out.println("Returned to one of the parent activities");
		if (resultCode == Activity.RESULT_CANCELED && requestCode == 1)
			return;
		else if (resultCode == Activity.RESULT_OK && requestCode == 1) 
		{
			setResult(Activity.RESULT_OK,intent);
			System.out.println("The results are in the intent : " + intent.getStringExtra("albumName").toString() + " and song name is " + intent.getStringExtra("songName").toString());
			finish();
		}
	}
	public void setupButtonLayout()
	{
		//this.backButton.setBottom(this.getBottom());
		//this.backButton.setHeight(50);
		//this.backButton.setWidth(50);
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("on click for back button");
				Intent returnIntent = new Intent();
				returnIntent.putExtra("data1", "hi");
				setResult(Activity.RESULT_CANCELED);;
				finish();
				
			}
		});
	}
	
	private void loadFileSystem()
    {
    	ContentResolver contentResolver = this.getContentResolver();
    	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	//Uri uri = Uri.withAppendedPath(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,"Beatles - All I've Gotta Do");
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
    	String [] projection = 
    		{
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.ALBUM,
    			MediaStore.Audio.Media.MIME_TYPE,
    			MediaStore.Audio.Media.DATA,
    			MediaStore.Audio.Media.ALBUM_ID
    		};
    	String [] columnProjection = 
    		{
    			MediaStore.Audio.Media.ALBUM
    		};
    	int [] to = new int [] {R.id.albumName};
    	System.out.println("The changed uri is "+ uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,null,MediaStore.Audio.Media.ALBUM);
    	if (cursor == null)
    	{
    		System.out.println("There was an error reading music files from the music library.");
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		System.out.println("There are no music files present in the library!");
    	}
    	else
    	{
    		System.out.println("in else");
    		if (albumNames == null)
    			System.out.println("album names is null");
    		albumListData = new ArrayAdapter<String>(this,R.layout.album_name_view,R.id.albumName,albumNames);
    		if (albumListData == null)
    			System.out.println("Album List data is null");
    		if (cursor == null)
    			System.out.println("Cursor is null");
    		//cursor.moveToNext();
    		//cursor.moveToPosition(1);
    		/*while(cursor.moveToNext());
    		{
    			System.out.println("The file name is " + cursor.getString(3) + " and the mime type is " + cursor.getString(2));
    			//cursor.moveToNext();
    		}
    		cursor.moveToPosition(-1);*/
    		//cursor.moveToNext();
    		//System.out.println("position after initial movement is " + cursor.getPosition());
    		//albumListData.add(cursor.getString(1));
    		//Uri basicUri = Uri.parse("content://media/external/audio/albumart");
    		//Uri albumArtUri;
    		//Bitmap artwork = null;
    		while(cursor.moveToNext())
    		{
    			//System.out.println(" Current position is " + cursor.getPosition());
    			if (albumNames.size() == 0)
    				albumListData.add(cursor.getString(1));
    			else if (cursor.getString(1).equalsIgnoreCase(albumNames.get(albumNames.size()-1)) == false)
    			{
    				albumListData.add(cursor.getString(1));
    			}
    			/*albumArtUri = ContentUris.withAppendedId(basicUri,cursor.getLong(4));
    			InputStream inputStream = null;
				try {
					inputStream = contentResolver.openInputStream(albumArtUri);
					System.out.println("Opened the album art");
					break;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			artwork = BitmapFactory.decodeStream(inputStream);*/
    			
    			
    		}
    		//ImageView imageView  = (ImageView)findViewById(R.id.imageView1);
    		//imageView.setImageBitmap(artwork);
    		//for (int j=0;j<albumNames.size();j++)
    		//System.out.println("Array is " + albumNames.get(j));
    		if (albumListData == null)
    			System.out.println("album list data is null");
    		albumListView.setAdapter(albumListData);
    		System.out.println("Number of rows are " + cursor.getCount() + " and column is " + cursor.getColumnName(0));
    	}
    }
}