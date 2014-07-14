package com.advancedandroidbook.simpletelephony;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MakeCallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.call);

		// format the number
		final EditText numberEntry = (EditText) findViewById(R.id.number_entry);
		numberEntry
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		Button call = (Button) findViewById(R.id.call_button);
		call.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Uri number = Uri.parse("tel:"
						+ numberEntry.getText().toString());
				Intent dial = new Intent(Intent.ACTION_DIAL, number);
				startActivity(dial);
			}

		});

	}
}
