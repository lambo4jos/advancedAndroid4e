package com.advancedandroidbook.simplebackup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleBackupActivity extends Activity {
	private static final String DEBUG_TAG = "SimpleBackup Log";
	private static final String PREFERENCE_FILENAME = "AppPrefs";
	private static final String PREF_NAME = "App Data";
	private static final String APP_FILE_NAME = "appfile.txt";

	static final Object[] fileLock = new Object[0];

	BackupManager mBackupManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBackupManager = new BackupManager(this);
		setContentView(R.layout.main);
		showData();
	}

	public void loadData(View view) {
		showData();
	}

	public void saveData(View view) {
		final EditText data = (EditText) findViewById(R.id.EditText_AppData);
		String strAppData = data.getText().toString();

		// Store data in prefs
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME,
				0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putString(PREF_NAME, strAppData);
		prefEditor.commit();

		// Store data in a file
		if (Arrays.binarySearch(fileList(), APP_FILE_NAME) >= 0) {
			deleteFile(APP_FILE_NAME); // There can only be one
		}

		// Must be synchronized
		FileOutputStream fos;
		try {
			synchronized (fileLock) {
				fos = openFileOutput(APP_FILE_NAME, MODE_PRIVATE);
				fos.write(strAppData.getBytes());
				fos.close();
			}
		} catch (Exception e) {
			Log.i(DEBUG_TAG,
					"openFileOutput (new file) threw exception: "
							+ e.getMessage());
		}

		Log.i(DEBUG_TAG, "DATA SAVED BUT NOT BACKED UP");
		showData();
	}

	public void performBackup(View view) {
		// Hey backup manager, backup my data!
		mBackupManager.dataChanged();
		Log.i(DEBUG_TAG, "BACKUP REQUESTED...");
		showData();
	}

	public void performRestore(View view) {
		RestoreObserver obs = new RestoreObserver() {

			@Override
			public void onUpdate(int nowBeingRestored, String currentPackage) {
				Log.i(DEBUG_TAG, "RESTORING: " + currentPackage);
			}

			@Override
			public void restoreFinished(int error) {
				Log.i(DEBUG_TAG, "RESTORE FINISHED! (" + error + ")");
				showData();
			}

			@Override
			public void restoreStarting(int numPackages) {
				Log.i(DEBUG_TAG, "RESTORE STARTING...");
			}
		};

		try {
			// Hey backup manager, restore my data!
			mBackupManager.requestRestore(obs);
		} catch (Exception e) {
			Toast.makeText(this,
					"Failed to request restore. Try adb bmgr restore...",
					Toast.LENGTH_LONG).show();
		}
	}

	private void showData() {
		final TextView curAppData = (TextView) findViewById(R.id.curData);
		String strCurDataPref = "Pref value: ";
		String strCurDataFile = "File contents: ";

		// CURRENT APP DATA
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME,
				0);
		strCurDataPref += settings.getString(PREF_NAME, "");
		strCurDataFile += readAppFile();

		curAppData.setText(strCurDataPref + "\n" + strCurDataFile);
	}

	private String readAppFile() {
		FileInputStream fis;
		StringBuffer sBuffer = new StringBuffer();
		try {
			synchronized (fileLock) {
				fis = openFileInput(APP_FILE_NAME);
				int chunkSize = 70;
				byte[] bf = new byte[chunkSize];

				// read 50 bytes at a time
				while ((fis.read(bf, 0, chunkSize)) != -1) {
					String str = new String(bf);
					sBuffer.append(str + "\n");
					// zero out our buffer so the next line only contains the
					// remainder bytes
					if (fis.available() < 50) {
						Arrays.fill(bf, 0, chunkSize, (byte) ' ');
					}
				}
				fis.close();
			}
		} catch (Exception e) {
			Log.i(DEBUG_TAG, "openFileInput threw exception: " + e.getMessage());
		}
		return sBuffer.toString();
	}
}