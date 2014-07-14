package com.advancedandroidbook.simplespeech;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SimpleSpeechActivity extends Activity implements
		TextToSpeech.OnInitListener {

	private static final String DEBUG_TAG = "SimpleSpeech";

	private static final int VOICE_RECOGNITION_REQUEST = 1;

	private TextToSpeech mTts;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialize TTS (async)
		mTts = new TextToSpeech(this, this);

	}
	

	@Override
	protected void onDestroy() {
		mTts.shutdown();
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		Button readButton = (Button) findViewById(R.id.ButtonRead);

		if (status == TextToSpeech.SUCCESS) {
			//int result = mTts.setLanguage(Locale.US);
			int result = mTts.setLanguage(Locale.UK);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e(DEBUG_TAG, "TTS Language not available.");
				readButton.setEnabled(false);
			} else {
				readButton.setEnabled(true);
			}
		} else {
			Log.e(DEBUG_TAG, "Could not initialize TTS Engine.");
			readButton.setEnabled(false);
		}
	}

	public void recordSpeech(View view) {
		// Record Speech
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Please speak slowly and clearly");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST && resultCode == RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			TextView textSaid = (TextView) findViewById(R.id.TextSaid);
			textSaid.setText(matches.get(0));
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void readText(View view) {
		TextView textSaid = (TextView) findViewById(R.id.TextSaid);
		mTts.speak((String) textSaid.getText(), TextToSpeech.QUEUE_FLUSH, null);
	}
}