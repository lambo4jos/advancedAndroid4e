package com.advancedandroidbook.pettracker2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.advancedandroidbook.pettracker2.PetTrackerDatabase.PetType;
import com.advancedandroidbook.pettracker2.PetTrackerDatabase.Pets;

public class PetTrackerListActivity extends PetTrackerActivity {
	
	private SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	private String asColumnsToReturn[] = {
			Pets.PETS_TABLE_NAME + "." + Pets.PET_NAME,
			Pets.PETS_TABLE_NAME + "." + Pets._ID,
			PetType.PETTYPE_TABLE_NAME + "." + PetType.PET_TYPE_NAME };
	private ListAdapter adapter = null;
	private ListView av = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.showpets);

		// Fill ListView from database
		fillPetList();

		// Handle Go enter more pets button
		final Button gotoEntry = (Button) findViewById(R.id.ButtonEnterMorePets);
		gotoEntry.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// We're done here. Finish and return to the entry screen
				finish();
			}
		});
	}

	public void fillPetList() {
		// SQL Query
		queryBuilder.setTables(Pets.PETS_TABLE_NAME + ", "
				+ PetType.PETTYPE_TABLE_NAME);
		queryBuilder.appendWhere(Pets.PETS_TABLE_NAME + "." + Pets.PET_TYPE_ID
				+ "=" + PetType.PETTYPE_TABLE_NAME + "." + PetType._ID);



//		CursorLoader loader = new CursorLoader(this, null, asColumnsToReturn,
//				null, null, Pets.DEFAULT_SORT_ORDER);
//		mCursor = loader.loadInBackground();

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null,
				null, Pets.DEFAULT_SORT_ORDER);

		// Use an adapter to bind the data to a ListView, where each item is
		// shown as a pet_item layout
		adapter = new SimpleCursorAdapter(this, R.layout.pet_item,
				mCursor, new String[] { Pets.PET_NAME, PetType.PET_TYPE_NAME },
				new int[] { R.id.TextView_PetName, R.id.TextView_PetType }, 1);		
		
		av = (ListView) findViewById(R.id.petList);
		av.setAdapter(adapter);

		av.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Check for delete button
				final long deletePetId = id;

				RelativeLayout item = (RelativeLayout) view;
				TextView nameView = (TextView) item
						.findViewById(R.id.TextView_PetName);
				String name = nameView.getText().toString();
				// Use an Alert dialog to confirm delete operation
				new AlertDialog.Builder(PetTrackerListActivity.this)
						.setMessage("Delete Pet Record for " + name + "?")
						.setPositiveButton("Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										// Delete that pet
										deletePet(deletePetId);

										// Refresh the data in our cursor and
										// therefore our List
										mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null,
												null, Pets.DEFAULT_SORT_ORDER, null);
										adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.pet_item,
												mCursor, new String[] { Pets.PET_NAME, PetType.PET_TYPE_NAME },
												new int[] { R.id.TextView_PetName, R.id.TextView_PetType }, 1);
										av.setAdapter(adapter);
									}
								}).show();
			}
		});
	}

	public void deletePet(Long id) {
		String astrArgs[] = { id.toString() };
		mDB.delete(Pets.PETS_TABLE_NAME, Pets._ID + "=?", astrArgs);
	}
}