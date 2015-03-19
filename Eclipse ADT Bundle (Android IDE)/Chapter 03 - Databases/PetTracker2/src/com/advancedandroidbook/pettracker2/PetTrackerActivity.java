package com.advancedandroidbook.pettracker2;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

// Similar to PetTracker, although we are keeping persistent Cursor and Database instances around
public class PetTrackerActivity extends Activity {

	protected PetTrackerDatabaseHelper mDatabase = null; 
	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDatabase = new PetTrackerDatabaseHelper(this.getApplicationContext());
		mDB = mDatabase.getWritableDatabase();	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mDB != null)
		{
			mDB.close();
		}
		
		if(mDatabase != null)
		{
			mDatabase.close();
		}
	}
}