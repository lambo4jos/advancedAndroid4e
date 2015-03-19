package com.advancedandroidbook.simplemultimedia;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AudioActivity extends Activity {
	final private static String RECORDED_FILE = "/audio.mp4";
	MediaRecorder audioListener;
	MediaPlayer player;

	@Override
	protected void onPause() {
		if (audioListener != null) {
			audioListener.release();
			audioListener = null;
		}
		if (player != null) {
			player.release();
			player = null;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		audioListener = new MediaRecorder();
		player = new MediaPlayer();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);

		final Button record = (Button) findViewById(R.id.record);
		final Button stop = (Button) findViewById(R.id.stop);
		final Button stopPlayback = (Button) findViewById(R.id.stop_playback);
		final Button play = (Button) findViewById(R.id.play);

		record.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (audioListener == null) {
					audioListener = new MediaRecorder();
				}

				// Fully qualified path name. In this case, we use the Files
				// subdir
				String pathForAppFiles = getFilesDir().getAbsolutePath();
				pathForAppFiles += RECORDED_FILE;
				Log.d("Audio filename:", pathForAppFiles);

				audioListener.setAudioSource(MediaRecorder.AudioSource.MIC);
				audioListener
						.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				audioListener
						.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				audioListener.setOutputFile(pathForAppFiles);

				try {
					audioListener.prepare();
					audioListener.start();
				} catch (Exception e) {
					Log.e("Audio",
							"Failed to prepare and start audio recording", e);
				}

				stop.setVisibility(View.VISIBLE);
				record.setVisibility(View.GONE);
				play.setVisibility(View.GONE);

			}

		});

		stop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (audioListener == null)
					return;
				audioListener.stop();
				audioListener.release();
				audioListener = null;

				String pathForAppFiles = getFilesDir().getAbsolutePath();
				pathForAppFiles += RECORDED_FILE;
				Log.d("Audio filename:", pathForAppFiles);

				ContentValues values = new ContentValues(10);

				values.put(MediaColumns.TITLE, "RecordedAudio");
				values.put(AudioColumns.ALBUM, "Your Groundbreaking Album");
				values.put(AudioColumns.ARTIST, "Your Name");
				values.put(MediaColumns.DISPLAY_NAME,
						"The Audio File You Recorded In Media App");
				values.put(AudioColumns.IS_RINGTONE, 1);
				values.put(AudioColumns.IS_MUSIC, 1);
				values.put(MediaColumns.TITLE, "RecordedAudio");
				values.put(MediaColumns.DATE_ADDED,
						System.currentTimeMillis() / 1000);
				values.put(MediaColumns.MIME_TYPE, "audio/mp4");
				values.put(MediaColumns.DATA, pathForAppFiles);

				Uri audioUri = getContentResolver().insert(
						MediaStore.Audio.Media.INTERNAL_CONTENT_URI, values);
				if (audioUri == null) {
					Log.d("Audio", "Content resolver failed");
					return;
				}

				// Force Media scanner to refresh now. Technically, this is
				// unnecessary, as the media scanner will run periodically but
				// helpful for testing.
				Log.d("Audio URI", "Path = " + audioUri.getPath());
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						audioUri));

				RingtoneManager.setActualDefaultRingtoneUri(
						getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
						audioUri);

				stop.setVisibility(View.GONE);
				record.setVisibility(View.VISIBLE);
				play.setVisibility(View.VISIBLE);

			}

		});

		play.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (player == null) {
					player = new MediaPlayer();
				}
				try {

					// Fully qualified path name. In this case, we use the Files
					// subdir
					String audioFilePath = getFilesDir().getAbsolutePath();
					audioFilePath += RECORDED_FILE;
					Log.d("Audio filename:", audioFilePath);

					player.setDataSource(audioFilePath);
					player.prepare();
					player.start();
				} catch (Exception e) {
					Log.e("Audio", "Playback failed.", e);
				}

				stopPlayback.setVisibility(View.VISIBLE);
				record.setVisibility(View.GONE);
				play.setVisibility(View.GONE);

			}

		});

		stopPlayback.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (player == null)
					return;
				player.stop();
				player.release();
				player = null;
				stopPlayback.setVisibility(View.GONE);
				record.setVisibility(View.VISIBLE);
				play.setVisibility(View.VISIBLE);

			}

		});
	}

}
