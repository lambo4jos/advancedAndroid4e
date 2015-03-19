package com.advancedandroidbook.simplesearchprovider;

import com.advancedandroidbook.simplesearchprovider.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

public class SimpleSearchMenuActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onResetData(View view) {
		deleteAllFieldnotes();
		addFieldnote(
				"The Zebra",
				"I was surprised when the zebras started making noise. I'm used to quiet horses, but zebras make a sort of donkey-like noise, and they buck and such as well. \nMore: http://j.mp/afz0Yn");
		addFieldnote(
				"The Baboon",
				"Baboons are found in surprisingly varied habitats and are extremely adaptable. All they need is a water source and a safe sleeping place, such as a tall tree or a cliff face. \nMore: http://j.mp/cZycRs");
		addFieldnote(
				"The Leopard",
				"The leopard is the most elusive of the Big Five, those being the most dangerous animals to hunt in Africa. The Big Five are: the lion, elephant, buffalo, leopard and rhinoceros. \nMore: http://j.mp/ap221U");
		addFieldnote(
				"The Wildebeest",
				"Both males and female wildebeests have curving horns. You'll often see them grazing along with zebras. \nMore: http://j.mp/bpKwff");
		addFieldnote(
				"Cape Buffalo",
				"Both male and female buffaloes have heavy, ridged horns. The horns are formidable weapons against predators and for jostling for space within the herd; males also use the horns in fights for dominance. \nMore: http://j.mp/9qlBTe");
		addFieldnote(
				"The Spotted Hyena",
				"Hyenas have some of the strongest jaws in the animal kingdom. With their powerful teeth and jaws and efficient digestion, the spotted hyena can utilize virtually everything on a carcass except the rumen contents and horns. The parts they cannot eat are regurgitated. Even desiccated carcasses yield protein and minerals during lean times. Because they eat bones, the hyena leaves behind white droppings.\nMore: http://j.mp/ajm2aY");
		addFieldnote(
				"The Duiker",
				"Duikers are small antelopes that inhabit forest or dense bushland.\nMore: http://j.mp/bkK1w6");
		addFieldnote(
				"The Impala",
				"One of the first animals you're likely to see on a game drive is a herd of impala. My friend Monika calls them the rats of the desert, the most common type of African antelope you're likely to see.\nMore: http://j.mp/9Jslga");
		addFieldnote(
				"The Waterbuck",
				"It's easy to identify a waterbuck from behind - it's got a big white ring or target, on its backside.\nMore: http://j.mp/dq7v44");
	}

	private void deleteAllFieldnotes() {
		getContentResolver().delete(
				SimpleFieldnotesContentProvider.CONTENT_URI, null, null);
	}

	private void addFieldnote(String headline, String story) {
		ContentValues values = new ContentValues();
		values.put(SimpleFieldnotesContentProvider.FIELDNOTES_TITLE, headline);
		values.put(SimpleFieldnotesContentProvider.FIELDNOTES_BODY, story);
		getContentResolver().insert(
				SimpleFieldnotesContentProvider.CONTENT_URI, values);
	}

	public void onForceSearch(View view) {
		onSearchRequested();
	}

	public void onLaunchGlobalSearchSettings(View view) {
		Intent intent = new Intent(SearchManager.INTENT_ACTION_SEARCH_SETTINGS);
		if(intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else {
			Toast notify = Toast.makeText(getApplicationContext(), "The emulator does not have access to Google Play for Global Search settings", Toast.LENGTH_SHORT);
			notify.show();
		}
	}

	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the options menu from XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Get the SearchView and set the searchable configuration
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
					.getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(new ComponentName(this,
							SimpleSearchableActivity.class)));
			searchView.setIconifiedByDefault(true);
		}
		return true;
	}
}