package de.t7soft.android.t7home.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class HomeDatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

	private static final String DATABASE_NAME = "home.db";
	private static final int DATABASE_VERSION = 6;

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

	// RoomTemperatureActuator
	public static final String ROOM_TEMPERATURE_ACTUATOR_TABLE_NAME = "roomTemperatureActuators";
	public static final String POINT_TEMPERATURE = "pointTemperature";
	public static final String OPERATION_MODE_COL_NAME = "operationMode";
	public static final String MAX_TEMPERATURE_COL_NAME = "maxTemperature";
	public static final String MIN_TEMPERATURE_COL_NAME = "minTemperature";
	public static final String IS_LOCKED_COL_NAME = "isLocked";
	public static final String ROOM_HUMIDITYSENSOR_ID_COL_NAME = "roomHumiditySensorId";
	public static final String ROOM_TEMPERATURESENSOR_ID_COL_NAME = "roomTemperatureSensorId";

	// WindowDoorSensor
	public static final String WINDOW_DOOR_SENSOR_TABLE_NAME = "WindowDoorSensors";
	public static final String IS_OPEN_COL_NAME = "isOpen";

	// DaySensor
	public static final String DAY_SENSOR_TABLE_NAME = "DaySensors";
	public static final String NEXT_SUNSET_COL_NAME = "nextSunset";
	public static final String NEXT_SUNRISE_COL_NAME = "nextSunrise";
	public static final String NEXT_TIME_EVENT_COL_NAME = "NextTimeEvent";

	public HomeDatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {

		createLocationsTable(db);
		createTemperatureHumidityDeviceTable(db);
		createRoomTemperatureActuatorTable(db);
		createRoomTemperatureSensorTable(db);
		createRoomHumiditySensorTable(db);
		createWindowDoorSensorTable(db);
		createDaySensorTable(db);

	}

	private void createLocationsTable(final SQLiteDatabase db) {
		final StringBuffer sqlBuffer = new StringBuffer();
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

	private void createLogicalDeviceTabDef(final SQLiteDatabase db, final String tableName, final String additionalCols) {

		final StringBuffer sqlBuffer = new StringBuffer();
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
		if ((additionalCols != null) && !additionalCols.isEmpty()) {
			sqlBuffer.append(", ");
			sqlBuffer.append(additionalCols);
		}
		sqlBuffer.append(");");
		db.execSQL(sqlBuffer.toString());

	}

	private void createRoomTemperatureSensorTable(final SQLiteDatabase db) {
		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(TEMPERATURE_COL_NAME);
		sqlBuffer.append(" DOUBLE");
		createLogicalDeviceTabDef(db, ROOM_TEMPERATURE_SENSOR_TABLE_NAME, sqlBuffer.toString());
	}

	private void createRoomHumiditySensorTable(final SQLiteDatabase db) {
		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(HUMIDITY_COL_NAME);
		sqlBuffer.append(" DOUBLE");
		createLogicalDeviceTabDef(db, ROOM_HUMIDITY_SENSOR_TABLE_NAME, sqlBuffer.toString());
	}

	private void createRoomTemperatureActuatorTable(final SQLiteDatabase db) {

		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(POINT_TEMPERATURE);
		sqlBuffer.append(" DOUBLE");
		sqlBuffer.append(", ");
		sqlBuffer.append(OPERATION_MODE_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(MAX_TEMPERATURE_COL_NAME);
		sqlBuffer.append(" DOUBLE");
		sqlBuffer.append(", ");
		sqlBuffer.append(MIN_TEMPERATURE_COL_NAME);
		sqlBuffer.append(" DOUBLE");
		sqlBuffer.append(", ");
		sqlBuffer.append(IS_LOCKED_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(", ");
		sqlBuffer.append(ROOM_HUMIDITYSENSOR_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(ROOM_TEMPERATURESENSOR_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		createLogicalDeviceTabDef(db, ROOM_TEMPERATURE_ACTUATOR_TABLE_NAME, sqlBuffer.toString());

	};

	private void createWindowDoorSensorTable(final SQLiteDatabase db) {

		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(IS_OPEN_COL_NAME);
		sqlBuffer.append(" INTEGER");
		createLogicalDeviceTabDef(db, WINDOW_DOOR_SENSOR_TABLE_NAME, sqlBuffer.toString());

	};

	private void createDaySensorTable(final SQLiteDatabase db) {

		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(NEXT_SUNSET_COL_NAME);
		sqlBuffer.append(" STRING");
		sqlBuffer.append(", ");
		sqlBuffer.append(NEXT_SUNRISE_COL_NAME);
		sqlBuffer.append(" STRING");
		sqlBuffer.append(", ");
		sqlBuffer.append(NEXT_TIME_EVENT_COL_NAME);
		sqlBuffer.append(" STRING");
		createLogicalDeviceTabDef(db, DAY_SENSOR_TABLE_NAME, sqlBuffer.toString());

	};

	private void createTemperatureHumidityDeviceTable(final SQLiteDatabase db) {
		final StringBuffer sqlBuffer = new StringBuffer();
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
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

		if (oldVersion < DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
		}
		onCreate(db);

	}

}
