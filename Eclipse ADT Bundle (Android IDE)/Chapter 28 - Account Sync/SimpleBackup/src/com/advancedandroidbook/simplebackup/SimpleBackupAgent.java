package com.advancedandroidbook.simplebackup;

import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class SimpleBackupAgent extends BackupAgentHelper {
	private static final String DEBUG_TAG = "SimpleBackup Log";

	// Same as Activity class
	private static final String PREFERENCE_FILENAME = "AppPrefs";
	private static final String APP_FILE_NAME = "appfile.txt";

	// Keys to uniquely identify backup data
	static final String BACKUP_PREFERENCE_KEY = "BackupAppPrefs";
	static final String BACKUP_FILE_KEY = "BackupFile";

	@Override
	public void onCreate() {
		SharedPreferencesBackupHelper prefshelper = new SharedPreferencesBackupHelper(
				this, PREFERENCE_FILENAME);
		addHelper(BACKUP_PREFERENCE_KEY, prefshelper);
		FileBackupHelper filehelper = new FileBackupHelper(this, APP_FILE_NAME);
		addHelper(BACKUP_FILE_KEY, filehelper);
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {
		// Lock file for backups
		synchronized (SimpleBackupActivity.fileLock) {
			super.onBackup(oldState, data, newState);
		}
		Log.i(DEBUG_TAG, "IN onBackup()");
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {
		// Lock file for restores
		synchronized (SimpleBackupActivity.fileLock) {
			super.onRestore(data, appVersionCode, newState);
		}
		Log.i(DEBUG_TAG, "IN onRestore()");
	}
}