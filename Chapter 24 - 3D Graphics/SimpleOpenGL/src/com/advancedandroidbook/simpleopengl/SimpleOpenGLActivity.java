package com.advancedandroidbook.simpleopengl;

import android.content.Intent;

public class SimpleOpenGLActivity extends MenuActivity {

	@Override
	void prepareMenu() {
		addMenuItem("1a. GL Triangle (red)", BasicGLActivity.class);

		Intent colorful = new Intent(this, BasicGLActivity.class);
		colorful.putExtra(BasicGLActivity.COLOR_OPTION_EXTRA, true);
		addMenuItem("1b. GL Triangle (multicolor)", colorful);

		Intent wireframe = new Intent(this, ShowGLCubeActivity.class);
		wireframe.putExtra(ShowGLCubeActivity.WIREFRAME_OPTION_EXTRA, true);
		addMenuItem("2a. GL Cube (wireframe)", wireframe);

		addMenuItem("2b. GL Cube (filled)", ShowGLCubeActivity.class);

		addMenuItem("3. Lighting Example (Cube)", ShowGLLightingActivity.class);
		addMenuItem("4. FPS Example", ShowGLFramesPerSecActivity.class);

		addMenuItem("5a. Show GL Texture", ShowGLTextureActivity.class);

		Intent coloredCube = new Intent(this, ShowGLTextureActivity.class);
		coloredCube.putExtra(ShowGLTextureActivity.USE_COLOR_CUBE_EXTRA, true);
		addMenuItem("5b. Show GL Texture over color", coloredCube);

		addMenuItem("6. Android GL Example", ShowAndroidGLActivity.class);
	}
}