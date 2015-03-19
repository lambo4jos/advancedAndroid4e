package com.advancedandroidbook.simplenetworking;

public class SimpleNetworkingActivity extends MenuActivity {

	@Override
	void prepareMenu() {
		addMenuItem("Network Status", NetworkStatusCheckActivity.class);
		addMenuItem("Example #1: Read Bytes", NetworkReadBytesActivity.class);
		addMenuItem("Example #2: Simple Flickr Feed (To File Sys)",
				FlickrActivity1.class);
		addMenuItem("Example #3: Flickr Feed (XML Parsing)",
				FlickrActivity2.class);
		addMenuItem("Example #4: Flickr Feed (Improved, Blocking)",
				FlickrActivity3.class);
		addMenuItem("Example #5: Flickr Feed (Improved, Async)",
				FlickrActivity4.class);
		addMenuItem("Example #6: Flickr Feed (Improved, Delay Async)",
				FlickrActivity5.class);

	}
}