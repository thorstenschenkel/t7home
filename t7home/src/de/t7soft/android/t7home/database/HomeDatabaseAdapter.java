package de.t7soft.android.t7home.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;

public class HomeDatabaseAdapter {

	private HomeDatabaseHelper dbHelper;
	private final Context context;
	private SQLiteDatabase database;

	public HomeDatabaseAdapter(final Context context) {
		this.context = context;
	}

	public void open() throws SQLException {
		dbHelper = new HomeDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertLocation(final SmartHomeLocation location) {
		return insertLocation(database, location);
	}

	static long insertLocation(SQLiteDatabase db,
			final SmartHomeLocation location) {
		final ContentValues initialValues = createContentValues(location);
		return db.insert(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null,
				initialValues);
	}

	public boolean deleteLocation(final SmartHomeLocation location) {
		final String id = location.getLocationId();
		final int ret = database.delete(
				HomeDatabaseHelper.LOCATIONS_TABLE_NAME,
				createLocationSelection(id), null);
		return (ret > 0);
	}

	public boolean deleteAllLocations() {
		final int ret = database.delete(
				HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public List<SmartHomeLocation> getAllLocations() {
		return getAllLocations(database);
	}

	static List<SmartHomeLocation> getAllLocations(SQLiteDatabase db) {
		final List<SmartHomeLocation> profiles = new ArrayList<SmartHomeLocation>();

		final Cursor cursor = db.query(HomeDatabaseHelper.LOCATIONS_TABLE_NAME,
				null, null, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				final SmartHomeLocation location = createLocation(cursor);
				profiles.add(location);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return profiles;
	}

	private static SmartHomeLocation createLocation(final Cursor cursor) {
		SmartHomeLocation location;
		location = new SmartHomeLocation();
		location.setLocationId(getString(cursor,
				HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		location.setName(getString(cursor,
				HomeDatabaseHelper.LOCATION_NAME_COL_NAME));
		location.setPosition(getString(cursor,
				HomeDatabaseHelper.LOCATION_POSITION_COL_NAME));
		return location;
	};

	private static ContentValues createContentValues(
			final SmartHomeLocation location) {
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME,
				location.getLocationId());
		values.put(HomeDatabaseHelper.LOCATION_NAME_COL_NAME,
				location.getName());
		values.put(HomeDatabaseHelper.LOCATION_POSITION_COL_NAME,
				location.getPosition());
		return values;
	}

	private String createLocationSelection(final String locationId) {
		return HomeDatabaseHelper.LOCATION_ID_COL_NAME + "=" + "\""
				+ locationId + "\"";
	}

	private static String getString(final Cursor cursor, String columnName) {
		return getString(cursor, cursor.getColumnIndex(columnName));
	}

	private static String getString(final Cursor cursor, int columnIndex) {
		String value = null;
		if (columnIndex < 0) {
			return value;
		}
		try {
			value = cursor.getString(columnIndex);
		} catch (Exception e) {
			value = null;
		}
		return value;
	}

}
