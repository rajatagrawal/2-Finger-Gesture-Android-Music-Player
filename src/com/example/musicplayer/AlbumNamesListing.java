package com.example.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class AlbumNamesListing extends Activity{

	ListView albumListView;
	Button backButton;
	ArrayAdapter<String> albumListDataAdapter;
	ArrayList<String>albumNames;
	Activity activity;
	Toast messageToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_layout);
		
		
		Log.d("AlbumNamesListing","In album names listing activity");
		albumListView = (ListView)findViewById(R.id.albumList1);
		activity = this;
		backButton = (Button) findViewById(R.id.backButtonAlbum);
		albumNames = new ArrayList<String>();
		messageToast = new Toast(this);
		if (albumListView == null || backButton == null)
		{
			Log.d("AlbumNamesListing","There is an error reading the layout structure. Quitting selection of album.");
			
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
				Log.d("AlbumNamesListing","The selected item is "+ albumListView.getAdapter().getItem(arg2));
				Intent childIntent = new Intent(activity,albumListing.class);
				childIntent.putExtra("albumName", albumListView.getAdapter().getItem(arg2).toString());
				activity.startActivityForResult(childIntent, 1);
			
			}
			
		});
		loadFileSystem();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		Log.d("AlbumNamesListing","Returned to one of the parent activities");
		if (resultCode == Activity.RESULT_CANCELED && requestCode == 1)
			return;
		else if (resultCode == Activity.RESULT_OK && requestCode == 1) 
		{
			setResult(Activity.RESULT_OK,intent);
			Log.d("AlbumNamesListing","The results are in the intent : " + intent.getStringExtra("albumName").toString() + " and song name is " + intent.getStringExtra("songName").toString());
			finish();
		}
	}
	public void setupButtonLayout()
	{
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("AlbumNamesListing","on click for back button");
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
    	Log.d("AlbumNamesListing","The changed uri is "+ uri.getEncodedPath());
    	Cursor cursor = contentResolver.query(uri,projection,selection,null,MediaStore.Audio.Media.ALBUM);
    	if (cursor == null)
    	{
    		messageToast.cancel();
    		messageToast = Toast.makeText(activity,"There was an error reading music files from the music library",Toast.LENGTH_SHORT);
    		messageToast.show();
    		Log.d("AlbumNamesListing","There was an error reading music files from the music library.");
    		return;
    	}
    	else if (cursor.getCount() == 0)
    	{
    		Log.d("AlbumNamesListing","There are no music files present in the library!");
    	}
    	else
    	{
    		Log.d("AlbumNamesListing","in else");
    		if (albumNames == null)
    			Log.d("AlbumNamesListing","album names is null");
    		albumListDataAdapter = new ArrayAdapter<String>(this,R.layout.album_name_view,R.id.albumName,albumNames);
    		if (albumListDataAdapter == null)
    			Log.d("AlbumNamesListing","Album List data is null");
    		if (cursor == null)
    			Log.d("AlbumNamesListing","Cursor is null");
    		//cursor.moveToNext();
    		//cursor.moveToPosition(1);
    		/*while(cursor.moveToNext());
    		{
    			Log.d("AlbumNamesListing","The file name is " + cursor.getString(3) + " and the mime type is " + cursor.getString(2));
    			//cursor.moveToNext();
    		}
    		cursor.moveToPosition(-1);*/
    		//cursor.moveToNext();
    		//Log.d("AlbumNamesListing","position after initial movement is " + cursor.getPosition());
    		//albumListData.add(cursor.getString(1));
    		//Uri basicUri = Uri.parse("content://media/external/audio/albumart");
    		//Uri albumArtUri;
    		//Bitmap artwork = null;
    		while(cursor.moveToNext())
    		{
    			//Log.d("AlbumNamesListing"," Current position is " + cursor.getPosition());
    			if (albumNames.size() == 0)
    				albumListDataAdapter.add(cursor.getString(1));
    			else if (cursor.getString(1).equalsIgnoreCase(albumNames.get(albumNames.size()-1)) == false)
    			{
    				albumListDataAdapter.add(cursor.getString(1));
    			}
    			/*albumArtUri = ContentUris.withAppendedId(basicUri,cursor.getLong(4));
    			InputStream inputStream = null;
				try {
					inputStream = contentResolver.openInputStream(albumArtUri);
					Log.d("AlbumNamesListing","Opened the album art");
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
    		//Log.d("AlbumNamesListing","Array is " + albumNames.get(j));
    		if (albumListDataAdapter == null)
    			Log.d("AlbumNamesListing","album list data is null");
    		albumListView.setAdapter(albumListDataAdapter);
    		Log.d("AlbumNamesListing","Number of rows are " + cursor.getCount() + " and column is " + cursor.getColumnName(0));
    	}
    }
}