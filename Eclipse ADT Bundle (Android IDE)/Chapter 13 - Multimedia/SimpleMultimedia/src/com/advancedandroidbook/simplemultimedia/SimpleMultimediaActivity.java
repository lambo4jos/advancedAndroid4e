package com.advancedandroidbook.simplemultimedia;

public class SimpleMultimediaActivity extends MenuActivity {
	@Override
	void prepareMenu() {
		addMenuItem("1. Record & Play Audio", AudioActivity.class);
		addMenuItem("2. Capture Still Image", StillImageActivity.class);
		addMenuItem("3. Play Video", VideoPlayActivity.class);
		addMenuItem("4. Record Video", VideoRecordActivity.class);
		addMenuItem("5. Search Media", MediaSearchActivity.class);
	}
}