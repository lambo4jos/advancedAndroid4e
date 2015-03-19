package com.advancedandroidbook.simplesearchprovider;

import java.util.HashMap;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SimpleFieldnotesContentProvider extends ContentProvider {
	// columns
	public static final String _ID = "_id";
	public static final String FIELDNOTES_TITLE = "fieldnotes_title";
	public static final String FIELDNOTES_BODY = "fieldnotes_body";
	
	// data
	public static final String AUTHORITY = "com.advancedandroidbook.simplesearchprovider.SimpleFieldnotesContentProvider";
	
	// content mime types
	public static final String BASE_DATA_NAME = "fieldnotes_provider";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.advancedandroidbook.search." + BASE_DATA_NAME;
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.advancedandroidbook.search." + BASE_DATA_NAME;
	
	// common URIs
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_DATA_NAME);
	public static final Uri SEARCH_SUGGEST_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + BASE_DATA_NAME + "/"
			+ SearchManager.SUGGEST_URI_PATH_QUERY);
	
	// matcher
	private static final int FIELDNOTES = 0x1000;
	private static final int FIELDNOTE_ITEM = 0x1001;
	private static final int FIELDNOTES_SEARCH_SUGGEST = 0x1200;
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME, FIELDNOTES);
		sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME + "/#", FIELDNOTE_ITEM);
		sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME + "/"
				+ SearchManager.SUGGEST_URI_PATH_QUERY,
				FIELDNOTES_SEARCH_SUGGEST);
		sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME + "/"
				+ SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
				FIELDNOTES_SEARCH_SUGGEST);
	}
	// custom search suggest column mapping
	private static final HashMap<String, String> FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP;
	static {
		FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
		FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP.put(_ID, _ID);
		FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP.put(
				SearchManager.SUGGEST_COLUMN_TEXT_1, FIELDNOTES_TITLE + " AS "
						+ SearchManager.SUGGEST_COLUMN_TEXT_1);
		// this one is only necessary if a full search on the story is needed
		// during suggest time
		// might be too slow to be worthwhile
		// FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP.put(FIELDNOTES_BODY,
		// FIELDNOTES_BODY);
		FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP.put(
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, _ID + " AS "
						+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	}
	// the database
	private SimpleFieldnotesDatabase database;

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase sql = database.getWritableDatabase();
		int rowsAffected = 0;
		switch (match) {
		case FIELDNOTES:
			rowsAffected = sql.delete(
					SimpleFieldnotesDatabase.FIELDNOTES_TABLE, whereClause,
					whereArgs);
			break;
		case FIELDNOTE_ITEM:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(whereClause)) {
				rowsAffected = sql.delete(
						SimpleFieldnotesDatabase.FIELDNOTES_TABLE, _ID + "="
								+ id, null);
			} else {
				rowsAffected = sql.delete(
						SimpleFieldnotesDatabase.FIELDNOTES_TABLE, whereClause
								+ " and " + _ID + "=" + id, whereArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	@Override
	public String getType(Uri uri) {
		int matchType = sURIMatcher.match(uri);
		switch (matchType) {
		case FIELDNOTES:
			return CONTENT_TYPE;
		case FIELDNOTE_ITEM:
			return CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri newUri = null;
		int match = sURIMatcher.match(uri);
		if (match == FIELDNOTES) {
			SQLiteDatabase sql = database.getWritableDatabase();
			long newId = sql.insert(SimpleFieldnotesDatabase.FIELDNOTES_TABLE,
					null, values);
			if (newId > 0) {
				newUri = ContentUris.withAppendedId(uri, newId);
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}
		return newUri;
	}

	@Override
	public boolean onCreate() {
		database = new SimpleFieldnotesDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(SimpleFieldnotesDatabase.FIELDNOTES_TABLE);
		int match = sURIMatcher.match(uri);
		switch (match) {
		case FIELDNOTES_SEARCH_SUGGEST:
			// selectionArgs has a single item; the query
			// add wildcards around it
			selectionArgs = new String[] { "%" + selectionArgs[0] + "%" };
			queryBuilder
					.setProjectionMap(FIELDNOTES_SEARCH_SUGGEST_PROJECTION_MAP);
			break;
		case FIELDNOTES:
			break;
		case FIELDNOTE_ITEM:
			String id = uri.getLastPathSegment();
			queryBuilder.appendWhere(_ID + "=" + id);
			break;
		default:
			throw new IllegalArgumentException("Invalid URI: " + uri);
		}
		SQLiteDatabase sql = database.getReadableDatabase();
		Cursor cursor = queryBuilder.query(sql, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// no updates allowed :)
		return 0;
	}

	private class SimpleFieldnotesDatabase extends SQLiteOpenHelper {
		private static final String FIELDNOTES_TABLE = "fieldnotes_provider";
		private static final String FIELDNOTES_DB_NAME = "fieldnotes_provider_db";
		private static final int SCHEMA_VERSION = 1;
		private static final String FIELDNOTES_SCHEMA = "CREATE TABLE "
				+ FIELDNOTES_TABLE + "(" + _ID
				+ " integer primary key autoincrement, " + FIELDNOTES_TITLE
				+ " text NOT NULL, " + FIELDNOTES_BODY + " text NOT NULL"
				+ ");";
		private static final String UPGRADE_DB_SCHEMA = "DROP TABLE IF EXISTS "
				+ FIELDNOTES_TABLE;

		public SimpleFieldnotesDatabase(Context context) {
			super(context, FIELDNOTES_DB_NAME, null, SCHEMA_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(FIELDNOTES_SCHEMA);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(UPGRADE_DB_SCHEMA);
			onCreate(db);
		}
	}
}
