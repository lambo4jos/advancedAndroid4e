package com.advancedandroidbook.simplewireless;

import java.nio.charset.Charset;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidBeamActivity extends Activity {
	private static final String DEBUG_TAG = "AndroidBeamActivity";
	public static final String MIMETYPE = "application/com.advancedandroidbook.simplewireless";
	NfcAdapter mNfcAdapter;
	TextView mStatusText;
	EditText messageToBeam;
	private static final int BEAM_BEAMED = 0x1001;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc);

		mStatusText = (TextView) findViewById(R.id.status);
		messageToBeam = (EditText) findViewById(R.id.messageToBeam);

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			mStatusText.setText("NFC is not available on this device.");
		}

		// Register to create and NDEF message when another device is in range
		mNfcAdapter.setNdefPushMessageCallback(new CreateNdefMessageCallback() {
			@Override
			public NdefMessage createNdefMessage(NfcEvent event) {
				Time time = new Time();
				time.setToNow();
				String message = messageToBeam.getText().toString();
				String text = (message + " \n[Sent @ "
						+ time.format("%H:%M:%S") + "]");
				byte[] mime = MIMETYPE.getBytes(Charset.forName("US-ASCII"));
				NdefRecord mimeMessage = new NdefRecord(
						NdefRecord.TNF_MIME_MEDIA, mime, new byte[0], text
								.getBytes());
				NdefMessage msg = new NdefMessage(
						new NdefRecord[] {
								mimeMessage,
								NdefRecord
										.createApplicationRecord("com.advancedandroidbook.simplewireless") });
				return msg;
			}
		}, this);

		// And handle the send status
		mNfcAdapter.setOnNdefPushCompleteCallback(
				new OnNdefPushCompleteCallback() {

					@Override
					public void onNdefPushComplete(NfcEvent event) {
						mHandler.obtainMessage(BEAM_BEAMED).sendToTarget();
					}
				}, this);
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case BEAM_BEAMED:
				mStatusText.setText("Your message has been beamed");
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// Did we receive an NDEF message?

		Intent intent = getIntent();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			try {
				Parcelable[] rawMsgs = intent
						.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

				// we created the message, so we know the format
				NdefMessage msg = (NdefMessage) rawMsgs[0];
				NdefRecord[] records = msg.getRecords();
				byte[] firstPayload = records[0].getPayload();
				String message = new String(firstPayload);
				mStatusText.setText(message);
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Error retrieving beam message.", e);
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// handle singleTop so we don't launch a bunch of instances..
		setIntent(intent);
	}

	public void onNFCSettingsClick(View view) {
		Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
		startActivity(intent);
	}

}
