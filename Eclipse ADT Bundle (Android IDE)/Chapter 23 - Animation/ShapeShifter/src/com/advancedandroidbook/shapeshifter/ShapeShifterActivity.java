package com.advancedandroidbook.shapeshifter;

public class ShapeShifterActivity extends MenuActivity {

	@Override
	void prepareMenu() {
		addMenuItem("Tweened Animation", TweenActivity.class);
		addMenuItem("Tweened Animation - Layout", TweenLayoutActivity.class);
		addMenuItem("Frame-By-Frame Animation - ImageView Background",
				FrameAnimationActivity.class);
		addMenuItem("Frame-By-Frame Animation - ImageSwitcher",
				FrameAnimationActivity2.class);
	}

}