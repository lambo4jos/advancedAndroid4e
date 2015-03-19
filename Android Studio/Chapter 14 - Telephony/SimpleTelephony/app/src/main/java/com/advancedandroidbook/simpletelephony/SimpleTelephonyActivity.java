package com.advancedandroidbook.simpletelephony;

public class SimpleTelephonyActivity extends MenuActivity {
	public void prepareMenu() {
		addMenuItem("1 Telephone Status", CheckStatusActivity.class);
		addMenuItem("2 SMS Send", SendSMSActivity.class);
		addMenuItem("3 Make Call", MakeCallActivity.class);
	}
}