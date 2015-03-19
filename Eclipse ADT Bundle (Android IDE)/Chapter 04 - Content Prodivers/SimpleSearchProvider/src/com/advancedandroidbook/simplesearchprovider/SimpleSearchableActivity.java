package com.advancedandroidbook.simplesearchprovider;

import android.app.ListActivity;
import android.app.SearchManager;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SimpleSearchableActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		checkIntent(intent);
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		// update the activity launch intent
		setIntent(newIntent);
		// handle it
		checkIntent(newIntent);
	}

	private void checkIntent(Intent intent) {
		String query = "";
		String intentAction = intent.getAction();
		if (Intent.ACTION_SEARCH.equals(intentAction)) {
			query = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(this, "Search received: " + query, Toast.LENGTH_LONG)
					.show();
		} else if (Intent.ACTION_VIEW.equals(intentAction)) {
			// pass this off to the details view activity
			Uri details = intent.getData();
			Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
			startActivity(detailsIntent);
			finish();
		}
		fillList(query);
	}

	private void fillList(String query) {
		// if we used FTS3 (http://j.mp/aQsyQN), we could use MATCH instead of
		// LIKE
		// But we're not here to show advanced sqlite usage
		String wildcardQuery = "%" + query + "%";
		
    	CursorLoader loader = new CursorLoader(getApplicationContext(), 
    			SimpleFieldnotesContentProvider.CONTENT_URI, 
    			null, 
    			SimpleFieldnotesContentProvider.FIELDNOTES_TITLE + " LIKE ? OR " + SimpleFieldnotesContentProvider.FIELDNOTES_BODY + " LIKE ?",
    			new String[] { wildcardQuery, wildcardQuery },	
    			null);
		Cursor cursor = loader.loadInBackground();
		
		ListAdapter adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_1,
				cursor,
				new String[] { SimpleFieldnotesContentProvider.FIELDNOTES_TITLE },
				new int[] { android.R.id.text1 }, 1);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri details = Uri.withAppendedPath(
				SimpleFieldnotesContentProvider.CONTENT_URI, "" + id);
		Intent intent = new Intent(Intent.ACTION_VIEW, details);
		startActivity(intent);
	}

}
