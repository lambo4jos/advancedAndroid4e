package com.advancedandroidbook.simplestats;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.HitBuilders.TransactionBuilder;
import com.google.android.gms.analytics.HitBuilders.ItemBuilder;
import com.google.android.gms.analytics.GoogleAnalytics;


public class SimpleStatsActivity extends Activity {

	GoogleAnalytics analytics;
	Tracker mTracker;
	EventBuilder redButtonEvent;
	EventBuilder blueButtonEvent;
	EventBuilder die1ButtonEvent;
	EventBuilder die2ButtonEvent;
	EventBuilder buyButtonEvent;
	TransactionBuilder transactionEvent;
	ItemBuilder item1Event;
	ItemBuilder item2Event;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Start your statistics tracking
		analytics = GoogleAnalytics.getInstance(this);

		mTracker = analytics.newTracker("UA-49600277-1");
		mTracker.setAppName("SimpleStats");
		mTracker.setPage("Home");
		mTracker.setScreenName("Choose a color");
		setContentView(R.layout.main);

		analytics.reportActivityStart(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		analytics.reportActivityStop(this);
	}

	public void onClickRedButton(View v) {
		// Red button clicked.
		Toast.makeText(SimpleStatsActivity.this, "Clicked the Red Button",
				Toast.LENGTH_SHORT).show();
		redButtonEvent = new EventBuilder();
		redButtonEvent.setCategory("Clicks");
		redButtonEvent.setAction("Button");
		redButtonEvent.setLabel("Red");
		redButtonEvent.setValue(0);
		mTracker.send(redButtonEvent.build());
	}

	public void onClickBlueButton(View v) {
		// Blue button clicked.
		Toast.makeText(SimpleStatsActivity.this, "Clicked the Blue Button",
				Toast.LENGTH_SHORT).show();
		blueButtonEvent = new EventBuilder();
		blueButtonEvent.setCategory("Clicks");
		blueButtonEvent.setAction("Button");
		blueButtonEvent.setLabel("Blue");
		blueButtonEvent.setValue(0);
		mTracker.send(blueButtonEvent.build());
	}

	public void onClickDie1Button(View v) {
		// Blue button clicked.
		Toast.makeText(SimpleStatsActivity.this, "Clicked the Die1 Button",
				Toast.LENGTH_SHORT).show();
		die1ButtonEvent = new EventBuilder();
		die1ButtonEvent.setCategory("Die");
		die1ButtonEvent.setAction("Hard");
		die1ButtonEvent.setLabel("One");
		die1ButtonEvent.setValue(15);
		mTracker.send(die1ButtonEvent.build());
	}

	public void onClickDie2Button(View v) {
		// Blue button clicked.
		int num = (int) (Math.random() * 10) + 1000;
		Toast.makeText(SimpleStatsActivity.this,
				"Clicked the Die2 (" + num + ") Button", Toast.LENGTH_SHORT)
				.show();
		die2ButtonEvent = new EventBuilder();
		die2ButtonEvent.setCategory("Die");
		die2ButtonEvent.setAction("Easy");
		die2ButtonEvent.setLabel("Two");
		die2ButtonEvent.setValue(num);
		mTracker.send(die2ButtonEvent.build());
	}

	public void onClickBuyButton(View v) {
		// Buy button clicked.
		Toast.makeText(SimpleStatsActivity.this, "Clicked the Buy Button",
				Toast.LENGTH_SHORT).show();
		
		buyButtonEvent = new EventBuilder();
		buyButtonEvent.setCategory("Buy");
		buyButtonEvent.setAction("Purchase");
		buyButtonEvent.setLabel("One");
		buyButtonEvent.setValue(0);
		mTracker.send(buyButtonEvent.build());
		
		// Transaction sample
		String orderID = "1001" + new Date().toString();
		
		transactionEvent = new TransactionBuilder();
		transactionEvent.setTransactionId(orderID);
		transactionEvent.setAffiliation("My Game Store");
		transactionEvent.setShipping(0);
		transactionEvent.setRevenue(1.99);
		transactionEvent.setTax(0);
		transactionEvent.setCurrencyCode("USD");
		mTracker.send(transactionEvent.build());

		item1Event = new ItemBuilder();
		item1Event.setTransactionId(orderID);
		item1Event.setName("1 Game Credit");
		item1Event.setSku("SKU_123");
		item1Event.setPrice(1.99);
		item1Event.setQuantity(1);
		item1Event.setCategory("LIFE POINTS");
		item1Event.setCurrencyCode("USD");
		mTracker.send(item1Event.build());
	}
}