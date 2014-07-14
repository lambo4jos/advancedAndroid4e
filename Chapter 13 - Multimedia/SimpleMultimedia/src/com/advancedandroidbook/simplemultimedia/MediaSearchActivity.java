package com.advancedandroidbook.simplemultimedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MediaSearchActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
	}

	public void searchMedia(View button) {

		Intent searchMusic = new Intent(
				android.provider.MediaStore.INTENT_ACTION_MEDIA_SEARCH);
		searchMusic.putExtra(android.provider.MediaStore.EXTRA_MEDIA_ARTIST,
				"Cyndi Lauper");
		searchMusic.putExtra(android.provider.MediaStore.EXTRA_MEDIA_TITLE,
				"I Drove All Night");
		searchMusic.putExtra(android.provider.MediaStore.EXTRA_MEDIA_FOCUS,
				"audio/*");
		startActivity(searchMusic);
	}
}
