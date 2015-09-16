package de.t7soft.android.t7home.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class HomeDatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

	private static final String DATABASE_NAME = "home.db";
	private static final int DATABASE_VERSION = 3;

	// common cols
	public static final String LOCATION_ID_COL_NAME = "locationId";
	public static final String LOGICAL_DEVICE_ID_COL_NAME = "logicalDeviceId";
	public static final String LOGICAL_DEVICE_TYPE_COL_NAME = "logicalDeviceType";
	public static final String LOGICAL_DEVICE_NAME_COL_NAME = "logicalDeviceName";

	// location
	public static final String LOCATIONS_TABLE_NAME = "locations";
	public static final String LOCATION_NAME_COL_NAME = "name";
	public static final String LOCATION_POSITION_COL_NAME = "position";

	// Temperature Humidity Device
	public static final String TEMPERATURE_HUMIDITY_DEVICE_TABLE_NAME = "temperatureHumidityDevices";
	// public static final String LOCATION_ID_COL_NAME = "locationId";
	public static final String TEMPERATURE_SENSOR_ID_COL_NAME = "temperatureSensor";
	public static final String TEMPERATURE_ACTUATOR_ID_COL_NAME = "temperatureActuator";
	public static final String ROOMHUMIDTY_SENSOR_ID_COL_NAME = "roomHumidtySensor";

	// Room Temperature Sensor
	public static final String ROOM_TEMPERATURE_SENSOR_TABLE_NAME = "roomTemperatureSensors";
	public static final String TEMPERATURE_COL_NAME = "temperature";

	// Room Humidity Sensor
	public static final String ROOM_HUMIDITY_SENSOR_TABLE_NAME = "roomhHumiditySensors";
	public static final String HUMIDITY_COL_NAME = "humidity";

	public HomeDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createLocationsTable(db);
		createTemperatureHumidityDeviceTable(db);
		createRoomTemperatureSensorTable(db);
		createRoomHumiditySensorTable(db);

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

	private void createLogicalDeviceTabDef(SQLiteDatabase db, String tableName, String additionalCols) {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(tableName);
		sqlBuffer.append("(");
		sqlBuffer.append("_id INTEGER PRIMARY KEY AUTOINCREMENT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOGICAL_DEVICE_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOGICAL_DEVICE_TYPE_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOGICAL_DEVICE_NAME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOCATION_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		if (additionalCols != null && !additionalCols.isEmpty()) {
			sqlBuffer.append(", ");
			sqlBuffer.append(additionalCols);
		}
		sqlBuffer.append(");");
		db.execSQL(sqlBuffer.toString());

	}

	private void createRoomTemperatureSensorTable(SQLiteDatabase db) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(TEMPERATURE_COL_NAME);
		sqlBuffer.append(" DOUBLE");
		createLogicalDeviceTabDef(db, ROOM_TEMPERATURE_SENSOR_TABLE_NAME, sqlBuffer.toString());
	}

	private void createRoomHumiditySensorTable(SQLiteDatabase db) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(HUMIDITY_COL_NAME);
		sqlBuffer.append(" DOUBLE");
		createLogicalDeviceTabDef(db, ROOM_HUMIDITY_SENSOR_TABLE_NAME, sqlBuffer.toString());
	}

	private void createTemperatureHumidityDeviceTable(SQLiteDatabase db) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(TEMPERATURE_HUMIDITY_DEVICE_TABLE_NAME);
		sqlBuffer.append("(");
		sqlBuffer.append("_id INTEGER PRIMARY KEY AUTOINCREMENT");
		sqlBuffer.append(", ");
		sqlBuffer.append(LOCATION_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(TEMPERATURE_SENSOR_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(TEMPERATURE_ACTUATOR_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(ROOMHUMIDTY_SENSOR_ID_COL_NAME);
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