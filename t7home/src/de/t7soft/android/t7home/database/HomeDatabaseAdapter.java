package de.t7soft.android.t7home.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureSensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;

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

	private static long insertLocation(SQLiteDatabase db, final SmartHomeLocation location) {
		final ContentValues initialValues = createContentValues(location);
		return db.insert(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, initialValues);
	}

	public boolean deleteLocation(final SmartHomeLocation location) {
		final String id = location.getLocationId();
		String where = createLocationSelection(id);
		final int ret = database.delete(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, where, null);
		return (ret > 0);
	}

	public List<SmartHomeLocation> getAllLocations() {
		return getAllLocations(database);
	}

	private static List<SmartHomeLocation> getAllLocations(SQLiteDatabase db) {
		final List<SmartHomeLocation> profiles = new ArrayList<SmartHomeLocation>();

		final Cursor cursor = db.query(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, null, null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					final SmartHomeLocation location = createLocation(cursor);
					profiles.add(location);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return profiles;
	}

	public int getLocationsCount() {
		return getLocationsCount(database);
	}

	private static int getLocationsCount(SQLiteDatabase db) {

		final Cursor cursor = db.query(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, null, null, null, null, null);

		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	private static SmartHomeLocation createLocation(final Cursor cursor) {
		SmartHomeLocation location = new SmartHomeLocation();
		location.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		location.setName(getString(cursor, HomeDatabaseHelper.LOCATION_NAME_COL_NAME));
		location.setPosition(getString(cursor, HomeDatabaseHelper.LOCATION_POSITION_COL_NAME));
		return location;
	};

	private static ContentValues createContentValues(final SmartHomeLocation location) {
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, location.getLocationId());
		values.put(HomeDatabaseHelper.LOCATION_NAME_COL_NAME, location.getName());
		values.put(HomeDatabaseHelper.LOCATION_POSITION_COL_NAME, location.getPosition());
		return values;
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

	private static double getDouble(final Cursor cursor, String columnName) {
		return getDouble(cursor, cursor.getColumnIndex(columnName));
	}

	private static double getDouble(final Cursor cursor, int columnIndex) {
		double value = Double.MIN_VALUE;
		if (columnIndex < 0) {
			return value;
		}
		try {
			value = cursor.getDouble(columnIndex);
		} catch (Exception e) {
			value = Double.MIN_VALUE;
		}
		return value;
	}

	public long insertTemperatureHumidityDevice(final TemperatureHumidityDevice device) {
		return insertTemperatureHumidityDevice(database, device);
	}

	private static long insertTemperatureHumidityDevice(SQLiteDatabase db, final TemperatureHumidityDevice device) {
		ContentValues initialValues = createContentValues(device.getRoomHumiditySensor());
		db.insert(HomeDatabaseHelper.ROOM_HUMIDITY_SENSOR_TABLE_NAME, null, initialValues);
		initialValues = createContentValues(device.getTemperatureSensor());
		db.insert(HomeDatabaseHelper.ROOM_TEMPERATURE_SENSOR_TABLE_NAME, null, initialValues);
		initialValues = createContentValues(device);
		// TODO device.getTemperatureActuator();
		return db.insert(HomeDatabaseHelper.TEMPERATURE_HUMIDITY_DEVICE_TABLE_NAME, null, initialValues);
	}

	private static ContentValues createContentValues(final TemperatureHumidityDevice device) {
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, device.getLocationId());
		values.put(HomeDatabaseHelper.TEMPERATURE_SENSOR_ID_COL_NAME, device.getTemperatureSensor().getDeviceId());
		values.put(HomeDatabaseHelper.TEMPERATURE_ACTUATOR_ID_COL_NAME, device.getTemperatureActuator().getDeviceId());
		values.put(HomeDatabaseHelper.ROOMHUMIDTY_SENSOR_ID_COL_NAME, device.getRoomHumiditySensor().getDeviceId());
		return values;
	}

	private static ContentValues createContentValues(final RoomHumiditySensor sensor) {
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, sensor.getLocationId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME, sensor.getLogicalDeviceId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME, sensor.getLogicalDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME, sensor.getLogicalDeviceType());
		values.put(HomeDatabaseHelper.HUMIDITY_COL_NAME, sensor.getHumidity());
		return values;
	}

	private static ContentValues createContentValues(final RoomTemperatureSensor sensor) {
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, sensor.getLocationId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME, sensor.getLogicalDeviceId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME, sensor.getLogicalDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME, sensor.getLogicalDeviceType());
		values.put(HomeDatabaseHelper.TEMPERATURE_COL_NAME, sensor.getTemperature());
		return values;
	}

	public List<Object> getAllDevices(SmartHomeLocation location) {
		List<Object> devices = new ArrayList<Object>();
		devices.addAll(getTemperatureHumidityDevices(database, location));
		return devices;
	}

	public List<TemperatureHumidityDevice> getTemperatureHumidityDevices(SmartHomeLocation location) {
		return getTemperatureHumidityDevices(database, location);
	}

	private List<TemperatureHumidityDevice> getTemperatureHumidityDevices(SQLiteDatabase db, SmartHomeLocation location) {

		final List<TemperatureHumidityDevice> devices = new ArrayList<TemperatureHumidityDevice>();

		String selection = createLocationSelection(location);
		final Cursor cursor = db.query(HomeDatabaseHelper.TEMPERATURE_HUMIDITY_DEVICE_TABLE_NAME, null, selection,
				null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				final TemperatureHumidityDevice device = createTemperatureHumidityDevice(cursor);
				devices.add(device);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return devices;

	}

	private TemperatureHumidityDevice createTemperatureHumidityDevice(Cursor cursor) {

		String locationId = getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME);
		String roomTemperatureSensorId = getString(cursor, HomeDatabaseHelper.TEMPERATURE_SENSOR_ID_COL_NAME);
		String roomHumiditySensorId = getString(cursor, HomeDatabaseHelper.ROOMHUMIDTY_SENSOR_ID_COL_NAME);

		TemperatureHumidityDevice device = new TemperatureHumidityDevice();
		device.setLocation(getLocation(locationId));
		device.setTemperatureSensor(getRoomTemperatureSensor(database, roomTemperatureSensorId));
		device.setRoomHumiditySensor(getRoomHumiditySensor(database, roomHumiditySensorId));
		// TODO device.setTemperatureActuator

		return device;

	}

	private static String createLocationSelection(final SmartHomeLocation location) {
		return createLocationSelection(location.getLocationId());
	}

	private static String createLocationSelection(final String id) {
		return HomeDatabaseHelper.LOCATION_ID_COL_NAME + "=" + "\"" + id + "\"";
	}

	private static String createRoomTemperatureSensorSelection(final String id) {
		return HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME + "=" + "\"" + id + "\"";
	}

	private static String createRoomHumiditySensorSelection(final String id) {
		return HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME + "=" + "\"" + id + "\"";
	}

	public SmartHomeLocation getLocation(String id) {
		return getLocation(database, id);
	}

	private static SmartHomeLocation getLocation(SQLiteDatabase db, String id) {

		SmartHomeLocation location = null;

		String selection = createLocationSelection(id);
		final Cursor cursor = db
				.query(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, selection, null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				location = createLocation(cursor);
			}
			cursor.close();
		}

		return location;

	}

	private RoomHumiditySensor getRoomHumiditySensor(SQLiteDatabase db, String id) {

		RoomHumiditySensor sensor = null;

		String selection = createRoomHumiditySensorSelection(id);
		final Cursor cursor = db.query(HomeDatabaseHelper.ROOM_HUMIDITY_SENSOR_TABLE_NAME, null, selection, null, null,
				null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				sensor = createRoomHumiditySensor(cursor);
			}
			cursor.close();
		}

		return sensor;

	}

	private RoomTemperatureSensor getRoomTemperatureSensor(SQLiteDatabase db, String id) {

		RoomTemperatureSensor sensor = null;

		String selection = createRoomTemperatureSensorSelection(id);
		final Cursor cursor = db.query(HomeDatabaseHelper.ROOM_TEMPERATURE_SENSOR_TABLE_NAME, null, selection, null,
				null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				sensor = createRoomTemperatureSensor(cursor);
			}
			cursor.close();
		}

		return sensor;

	}

	private RoomTemperatureSensor createRoomTemperatureSensor(final Cursor cursor) {

		RoomTemperatureSensor sensor = new RoomTemperatureSensor();

		sensor.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		sensor.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		sensor.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));
		sensor.setTemperature(getDouble(cursor, HomeDatabaseHelper.TEMPERATURE_COL_NAME));
		sensor.setLocation(getLocation(sensor.getLocationId()));
		return sensor;

	};

	private RoomHumiditySensor createRoomHumiditySensor(final Cursor cursor) {

		RoomHumiditySensor sensor = new RoomHumiditySensor();

		sensor.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		sensor.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		sensor.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));
		sensor.setHumidity(getDouble(cursor, HomeDatabaseHelper.HUMIDITY_COL_NAME));
		sensor.setLocation(getLocation(sensor.getLocationId()));
		return sensor;

	};

	public boolean deleteAllLocations() {
		final int ret = database.delete(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public boolean deleteAllRoomHumidtySensors() {
		final int ret = database.delete(HomeDatabaseHelper.ROOM_HUMIDITY_SENSOR_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public boolean deleteAllTemperatureSensors() {
		final int ret = database.delete(HomeDatabaseHelper.ROOM_TEMPERATURE_SENSOR_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public boolean deleteAllTemperatureHumidityDevices() {
		final int ret = database.delete(HomeDatabaseHelper.TEMPERATURE_HUMIDITY_DEVICE_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public void deleteAll() {
		deleteAllLocations();
		deleteAllRoomHumidtySensors();
		deleteAllTemperatureSensors();
		deleteAllTemperatureHumidityDevices();
	}

}
