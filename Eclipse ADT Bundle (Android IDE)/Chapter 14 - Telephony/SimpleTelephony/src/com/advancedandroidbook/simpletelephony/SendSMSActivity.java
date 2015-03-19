package com.advancedandroidbook.simpletelephony;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendSMSActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);

		final EditText messageEntry = (EditText) findViewById(R.id.short_message);

		final SmsManager sms = SmsManager.getDefault();

		// format the number
		final EditText numberEntry = (EditText) findViewById(R.id.number_entry);
		numberEntry
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		// handle the send message button
		Button sendSMS = (Button) findViewById(R.id.send_sms);
		sendSMS.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String destination = numberEntry.getText().toString();
				String message = messageEntry.getText().toString();
				sms.sendTextMessage(destination, null, message, null,
						null);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onStop();
	}

}
