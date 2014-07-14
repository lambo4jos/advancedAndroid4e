package com.advancedandroidbook.simplendk;

public class SimpleNDKMenuActivity extends MenuActivity {
	@Override
	void prepareMenu() {
		addMenuItem("1. Native Basics", NativeBasicsActivity.class);
		addMenuItem("2. Native OpenGL ES 2.0", NativeOpenGL2Activity.class);
	}
}