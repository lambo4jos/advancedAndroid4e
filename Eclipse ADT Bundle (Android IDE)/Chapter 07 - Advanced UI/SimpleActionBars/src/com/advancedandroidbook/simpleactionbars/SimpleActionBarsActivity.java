package com.advancedandroidbook.simpleactionbars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SimpleActionBarsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cleaningoptions, menu);

		return true;
	}

	public void onOptionSweep(MenuItem i) {
		startActivity(new Intent(this, SweepActivity.class));
	}

	public void onOptionScrub(MenuItem i) {
		startActivity(new Intent(this, ScrubActivity.class));
	}

	public void onOptionVacuum(MenuItem i) {
		startActivity(new Intent(this, VacuumActivity.class));
	}

}