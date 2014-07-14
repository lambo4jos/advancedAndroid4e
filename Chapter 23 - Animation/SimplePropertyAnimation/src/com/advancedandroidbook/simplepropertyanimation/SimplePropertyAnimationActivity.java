package com.advancedandroidbook.simplepropertyanimation;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SimplePropertyAnimationActivity extends Activity {

	private Button a;
	private Button b;
	private TextView tv;
	private RelativeLayout layout;
	private float t = 150f;
	private boolean scaleDown = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// save off some views for later use
		tv = (TextView) findViewById(R.id.myText);

		a = (Button) findViewById(R.id.buttonA);
		b = (Button) findViewById(R.id.buttonB);

		layout = (RelativeLayout) findViewById(R.id.layout);

		// load an animator to use on the text view; it will run continuously
		AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this,
				R.animator.blinky_anim);
		// work around to set the argb TypeEvaluator on an xml animator
		ArrayList<Animator> animations = set.getChildAnimations();
		for (Animator animator : animations) {
			if (animator instanceof ObjectAnimator) {
				ObjectAnimator anim = (ObjectAnimator) animator;
				if (anim.getPropertyName().compareTo("backgroundColor") == 0) {
					anim.setEvaluator(new ArgbEvaluator());
				}
			}
		}

		set.setInterpolator(new LinearInterpolator());
		set.setTarget(tv);
		set.start();

		// start another animation on the textview, this time using
		// ViewPropertyAnimator
		tv.animate().translationXBy(t / 2).rotationXBy(720).setDuration(1250);
	}

	public void onClickA(View view) {
		Toast.makeText(this, "Click A", Toast.LENGTH_SHORT).show();
		swap();

	}

	public void onClickB(View view) {
		Toast.makeText(this, "Click B", Toast.LENGTH_SHORT).show();
		swap();
	}

	private void swap() {
		float aX = a.getX();
		float bX = b.getX();
		float aY = a.getY();
		float bY = b.getY();

		// demonstrates how the buttons actually move; clicks are interpreted
		// correctly in the new location
		a.animate().x(bX).y(bY).rotationXBy(360).setDuration(1250);
		b.animate().x(aX).y(aY).rotationYBy(360).setDuration(1250);

		// move the text in the middle -- for fun
		t = 0 - t;
		tv.animate().rotationYBy(720).translationXBy(t).setDuration(1250);

		// demonstrates how even scaling the whole layout the buttons work
		// correctly.
		if (scaleDown) {
			scaleDown = false;
			layout.animate().scaleX(0.5f).scaleY(0.5f)
					.setInterpolator(new OvershootInterpolator(3f))
					.setDuration(1500);
		} else {
			scaleDown = true;
			layout.animate().scaleX(1f).scaleY(1f)
					.setInterpolator(new BounceInterpolator())
					.setDuration(1500);
		}
	}

	public void onClickFlip(View view) {
		flip();
	}

	private void flip() {
		// demonstrates how even a layout that's facing ... in? ... still can
		// interpret button clicks correctly
		layout.animate().rotationYBy(180).rotationXBy(180);
	}
}