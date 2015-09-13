package de.t7soft.android.t7home.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class HomeDatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

	private static final String DATABASE_NAME = "home.db";
	private static final int DATABASE_VERSION = 1;

	public static final String LOCATIONS_TABLE_NAME = "locatins";
	public static final String LOCATION_ID_COL_NAME = "locationId";
	public static final String LOCATION_NAME_COL_NAME = "name";
	public static final String LOCATION_POSITION_COL_NAME = "position";

	public HomeDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createLocationsTable(db);

	}

	private void createLocationsTable(SQLiteDatabase db) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(LOCATIONS_TABLE_NAME);
		sqlBuffer.append("(");
		sqlBuffer.append("_id INTEGER PRIMARY KEY AUTOINCREMENT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOCATION_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOCATION_NAME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOCATION_POSITION_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(");");
		db.execSQL(sqlBuffer.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion < DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
		}
		onCreate(db);

	}

}
