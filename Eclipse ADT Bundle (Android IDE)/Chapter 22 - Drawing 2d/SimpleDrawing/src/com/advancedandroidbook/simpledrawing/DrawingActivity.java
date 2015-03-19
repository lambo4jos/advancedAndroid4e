package com.advancedandroidbook.simpledrawing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class DrawingActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.drawmenu, menu);

		menu.findItem(R.id.bitmap_menu_item).setIntent(
				new Intent(this, DrawBitmapActivity.class));
		menu.findItem(R.id.gradient_menu_item).setIntent(
				new Intent(this, DrawGradientActivity.class));
		menu.findItem(R.id.shape_menu_item).setIntent(
				new Intent(this, DrawShapeActivity.class));
		menu.findItem(R.id.text_menu_item).setIntent(
				new Intent(this, DrawTextActivity.class));
		menu.findItem(R.id.font_menu_item).setIntent(
				new Intent(this, DrawCustomFontActivity.class));
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(item.getIntent());
		super.onOptionsItemSelected(item);
		return true;
	}
}