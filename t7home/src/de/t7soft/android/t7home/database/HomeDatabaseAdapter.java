package de.t7soft.android.t7home.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;
import de.t7soft.android.t7home.smarthome.api.devices.DaySensor;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RollerShutterActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureSensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;

public class HomeDatabaseAdapter {

	private static final String LOGTAG = HomeDatabaseAdapter.class.getSimpleName();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private final Context context;
	private final List<IDatabaseUpdateListener> updateListeners;
	private HomeDatabaseHelper dbHelper;
	private SQLiteDatabase database;

	public HomeDatabaseAdapter(final Context context) {
		this.context = context;
		updateListeners = new LinkedList<IDatabaseUpdateListener>();
	}

	public void open() throws SQLException {
		dbHelper = new HomeDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean canWrite() {
		if (database != null) {
			return database.isOpen() && !database.isReadOnly() && !database.isDbLockedByCurrentThread();
		}
		return false;
	}

	public long insertLocation(final SmartHomeLocation location) {
		return insertLocation(database, location);
	}

	private static long insertLocation(final SQLiteDatabase db, final SmartHomeLocation location) {
		final ContentValues initialValues = createContentValues(location);
		return db.insert(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, initialValues);
	}

	public boolean deleteLocation(final SmartHomeLocation location) {
		final String id = location.getLocationId();
		final String where = createLocationSelection(id);
		final int ret = database.delete(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, where, null);
		return (ret > 0);
	}

	public List<SmartHomeLocation> getAllLocations() {
		return getAllLocations(database);
	}

	private static List<SmartHomeLocation> getAllLocations(final SQLiteDatabase db) {
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

	private static int getLocationsCount(final SQLiteDatabase db) {

		final Cursor cursor = db.query(HomeDatabaseHelper.LOCATIONS_TABLE_NAME, null, null, null, null, null, null);

		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	private static SmartHomeLocation createLocation(final Cursor cursor) {
		final SmartHomeLocation location = new SmartHomeLocation();
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

	private static String getString(final Cursor cursor, final String columnName) {
		return getString(cursor, cursor.getColumnIndex(columnName));
	}

	private static String getString(final Cursor cursor, final int columnIndex) {
		String value = null;
		if (columnIndex < 0) {
			return value;
		}
		try {
			value = cursor.getString(columnIndex);
		} catch (final Exception e) {
			value = null;
		}
		return value;
	}

	private static int getInt(final Cursor cursor, final String columnName) {
		return getInt(cursor, cursor.getColumnIndex(columnName));
	}

	private static double getDouble(final Cursor cursor, final String columnName) {
		return getDouble(cursor, cursor.getColumnIndex(columnName));
	}

	private static boolean getBoolean(final Cursor cursor, final String columnName) {
		return getBoolean(cursor, cursor.getColumnIndex(columnName));
	}

	private static boolean getBoolean(final Cursor cursor, final int columnIndex) {
		boolean value = false;
		if (columnIndex < 0) {
			return false;
		}
		try {
			final int intValue = cursor.getInt(columnIndex);
			value = (intValue != 0);
		} catch (final Exception e) {
			value = false;
		}
		return value;
	}

	private static double getDouble(final Cursor cursor, final int columnIndex) {
		double value = Double.MIN_VALUE;
		if (columnIndex < 0) {
			return value;
		}
		try {
			value = cursor.getDouble(columnIndex);
		} catch (final Exception e) {
			value = Double.MIN_VALUE;
		}
		return value;
	}

	private static int getInt(final Cursor cursor, final int columnIndex) {
		int value = Integer.MIN_VALUE;
		if (columnIndex < 0) {
			return value;
		}
		try {
			value = cursor.getInt(columnIndex);
		} catch (final Exception e) {
			value = Integer.MIN_VALUE;
		}
		return value;
	}

	public long insertDaySensor(final DaySensor sensor) {
		return insertDaySensor(database, sensor);
	}

	private static long insertDaySensor(final SQLiteDatabase db, final DaySensor sensor) {
		final ContentValues initialValues = createContentValues(sensor);
		return db.insert(HomeDatabaseHelper.DAY_SENSOR_TABLE_NAME, null, initialValues);
	}

	public long insertWindowDoorSensor(final WindowDoorSensor sensor) {
		return insertWindowDoorSensor(database, sensor);
	}

	private static long insertWindowDoorSensor(final SQLiteDatabase db, final WindowDoorSensor sensor) {
		final ContentValues initialValues = createContentValues(sensor);
		return db.insert(HomeDatabaseHelper.WINDOW_DOOR_SENSOR_TABLE_NAME, null, initialValues);
	}

	public long insertRollerShutterActuator(final RollerShutterActuator actuator) {
		return insertRollerShutterActuator(database, actuator);
	}

	private static long insertRollerShutterActuator(final SQLiteDatabase db, final RollerShutterActuator actuator) {
		final ContentValues initialValues = createContentValues(actuator);
		return db.insert(HomeDatabaseHelper.ROLLER_SHUTTER_TABLE_NAME, null, initialValues);
	}

	public long updateRollerShutterActuator(final LogicalDevice device, final int rollerShutterLevel) {
		return updateRollerShutterActuator(database, device, rollerShutterLevel);
	}

	private long updateRollerShutterActuator(final SQLiteDatabase db, final LogicalDevice device,
			final int rollerShutterLevel) {
		final String selection = createLogicalDeviceSelection(device);
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.SHUTTER_LEVEL_COL_NAME, rollerShutterLevel);
		final long row = db.update(HomeDatabaseHelper.ROLLER_SHUTTER_TABLE_NAME, values, selection, null);
		fireUpdate(device);
		return row;
	}

	public long updateRoomTemperatureActuator(final LogicalDevice device, final double temperature) {
		return updateRoomTemperatureActuator(database, device, temperature);
	}

	private long updateRoomTemperatureActuator(final SQLiteDatabase db, final LogicalDevice device,
			final double temperature) {
		final String selection = createLogicalDeviceSelection(device);
		final ContentValues values = new ContentValues();
		values.put(HomeDatabaseHelper.POINT_TEMPERATURE, temperature);
		final long row = db.update(HomeDatabaseHelper.ROOM_TEMPERATURE_ACTUATOR_TABLE_NAME, values, selection, null);
		fireUpdate(device);
		return row;
	}

	public long insertTemperatureHumidityDevice(final TemperatureHumidityDevice device) {
		return insertTemperatureHumidityDevice(database, device);
	}

	private static long insertTemperatureHumidityDevice(final SQLiteDatabase db, final TemperatureHumidityDevice device) {
		ContentValues initialValues = createContentValues(device.getRoomHumiditySensor());
		db.insert(HomeDatabaseHelper.ROOM_HUMIDITY_SENSOR_TABLE_NAME, null, initialValues);
		initialValues = createContentValues(device.getTemperatureSensor());
		db.insert(HomeDatabaseHelper.ROOM_TEMPERATURE_SENSOR_TABLE_NAME, null, initialValues);
		initialValues = createContentValues(device.getTemperatureActuator());
		db.insert(HomeDatabaseHelper.ROOM_TEMPERATURE_ACTUATOR_TABLE_NAME, null, initialValues);
		initialValues = createContentValues(device);
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

	private static ContentValues createContentValues(final DaySensor sensor) {
		final ContentValues values = new ContentValues();

		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, sensor.getLocationId());
		values.put(HomeDatabaseHelper.DEVICE_NAME_COL_NAME, sensor.getDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME, sensor.getLogicalDeviceId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME, sensor.getLogicalDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME, sensor.getLogicalDeviceType());

		if (sensor.getNextSunrise() != null) {
			values.put(HomeDatabaseHelper.NEXT_SUNRISE_COL_NAME, DATE_FORMAT.format(sensor.getNextSunrise()));
		} else {
			values.put(HomeDatabaseHelper.NEXT_SUNRISE_COL_NAME, "");
		}
		if (sensor.getNextSunset() != null) {
			values.put(HomeDatabaseHelper.NEXT_SUNSET_COL_NAME, DATE_FORMAT.format(sensor.getNextSunset()));
		} else {
			values.put(HomeDatabaseHelper.NEXT_SUNSET_COL_NAME, "");
		}
		if (sensor.getNextTimeEvent() != null) {
			values.put(HomeDatabaseHelper.NEXT_TIME_EVENT_COL_NAME, DATE_FORMAT.format(sensor.getNextTimeEvent()));
		} else {
			values.put(HomeDatabaseHelper.NEXT_TIME_EVENT_COL_NAME, "");
		}
		return values;

	}

	private static ContentValues createContentValues(final WindowDoorSensor sensor) {
		final ContentValues values = new ContentValues();

		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, sensor.getLocationId());
		values.put(HomeDatabaseHelper.DEVICE_NAME_COL_NAME, sensor.getDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME, sensor.getLogicalDeviceId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME, sensor.getLogicalDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME, sensor.getLogicalDeviceType());

		values.put(HomeDatabaseHelper.IS_OPEN_COL_NAME, sensor.isOpen());
		return values;
	}

	private static ContentValues createContentValues(final RollerShutterActuator sensor) {
		final ContentValues values = new ContentValues();

		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, sensor.getLocationId());
		values.put(HomeDatabaseHelper.DEVICE_NAME_COL_NAME, sensor.getDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME, sensor.getLogicalDeviceId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME, sensor.getLogicalDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME, sensor.getLogicalDeviceType());

		values.put(HomeDatabaseHelper.IS_CALIBRATING_COL_NAME, sensor.isCalibrating());
		values.put(HomeDatabaseHelper.OFF_LVL_COL_NAME, sensor.getOffLvl());
		values.put(HomeDatabaseHelper.ON_LVL_COL_NAME, sensor.getOnLvl());
		values.put(HomeDatabaseHelper.SCBH_COL_NAME, sensor.getSCBh());
		values.put(HomeDatabaseHelper.SH_DT_COL_NAME, sensor.getShDT());
		values.put(HomeDatabaseHelper.SHUTTER_LEVEL_COL_NAME, sensor.getShutterLevel());
		values.put(HomeDatabaseHelper.TM_FD_COL_NAME, sensor.getTmFD());
		values.put(HomeDatabaseHelper.TM_FU_COL_NAME, sensor.getTmFU());

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

	private static ContentValues createContentValues(final RoomTemperatureActuator actuator) {
		final ContentValues values = new ContentValues();

		values.put(HomeDatabaseHelper.LOCATION_ID_COL_NAME, actuator.getLocationId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME, actuator.getLogicalDeviceId());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME, actuator.getLogicalDeviceName());
		values.put(HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME, actuator.getLogicalDeviceType());

		values.put(HomeDatabaseHelper.POINT_TEMPERATURE, actuator.getPointTemperature());
		values.put(HomeDatabaseHelper.OPERATION_MODE_COL_NAME, actuator.getOperationMode());
		values.put(HomeDatabaseHelper.MAX_TEMPERATURE_COL_NAME, actuator.getMaxTemperature());
		values.put(HomeDatabaseHelper.MIN_TEMPERATURE_COL_NAME, actuator.getMinTemperature());
		values.put(HomeDatabaseHelper.IS_LOCKED_COL_NAME, actuator.getIsLocked());
		return values;
	}

	public List<Object> getAllDevices(final SmartHomeLocation location) {
		final List<Object> devices = new ArrayList<Object>();
		devices.addAll(getTemperatureHumidityDevices(database, location));
		devices.addAll(getWindowDoorSensors(database, location));
		devices.addAll(getDaySensors(database, location));
		devices.addAll(getRollerShutterActuator(database, location));
		return devices;
	}

	public List<TemperatureHumidityDevice> getTemperatureHumidityDevices(final SmartHomeLocation location) {
		return getTemperatureHumidityDevices(database, location);
	}

	public List<WindowDoorSensor> getWindowDoorSensors(final SmartHomeLocation location) {
		return getWindowDoorSensors(database, location);
	}

	public List<RollerShutterActuator> getRollerShutterActuator(final SmartHomeLocation location) {
		return getRollerShutterActuator(database, location);
	}

	public List<DaySensor> getDaySensors(final SmartHomeLocation location) {
		return getDaySensors(database, location);
	}

	private List<TemperatureHumidityDevice> getTemperatureHumidityDevices(final SQLiteDatabase db,
			final SmartHomeLocation location) {

		final List<TemperatureHumidityDevice> devices = new ArrayList<TemperatureHumidityDevice>();

		final String selection = createLocationSelection(location);
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

	private TemperatureHumidityDevice createTemperatureHumidityDevice(final Cursor cursor) {

		final String locationId = getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME);
		final String roomTemperatureSensorId = getString(cursor, HomeDatabaseHelper.TEMPERATURE_SENSOR_ID_COL_NAME);
		final String roomHumiditySensorId = getString(cursor, HomeDatabaseHelper.ROOMHUMIDTY_SENSOR_ID_COL_NAME);
		final String temperatureActuatorId = getString(cursor, HomeDatabaseHelper.TEMPERATURE_ACTUATOR_ID_COL_NAME);

		final TemperatureHumidityDevice device = new TemperatureHumidityDevice();
		device.setLocation(getLocation(locationId));
		device.setTemperatureSensor(getRoomTemperatureSensor(database, roomTemperatureSensorId));
		device.setRoomHumiditySensor(getRoomHumiditySensor(database, roomHumiditySensorId));
		device.setTemperatureActuator(getRoomTemperatureActuator(database, temperatureActuatorId));
		device.getTemperatureActuator().setRoomTemperatureSensor(device.getTemperatureSensor());
		device.getTemperatureActuator().setRoomHumiditySensor(device.getRoomHumiditySensor());

		return device;

	}

	private static String createLocationSelection(final SmartHomeLocation location) {
		return createLocationSelection(location.getLocationId());
	}

	private static String createLocationSelection(final String id) {
		return HomeDatabaseHelper.LOCATION_ID_COL_NAME + "=" + "\"" + id + "\"";
	}

	private static String createLogicalDeviceSelection(final LogicalDevice device) {
		return createLogicalDeviceSelection(device.getDeviceId());
	}

	private static String createLogicalDeviceSelection(final String id) {
		return HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME + "=" + "\"" + id + "\"";
	}

	public SmartHomeLocation getLocation(final String id) {
		return getLocation(database, id);
	}

	private static SmartHomeLocation getLocation(final SQLiteDatabase db, final String id) {

		SmartHomeLocation location = null;

		final String selection = createLocationSelection(id);
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

	private RoomHumiditySensor getRoomHumiditySensor(final SQLiteDatabase db, final String id) {

		RoomHumiditySensor sensor = null;

		final String selection = createLogicalDeviceSelection(id);
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

	private List<WindowDoorSensor> getWindowDoorSensors(final SQLiteDatabase db, final SmartHomeLocation location) {

		final List<WindowDoorSensor> sensors = new ArrayList<WindowDoorSensor>();

		final String selection = createLocationSelection(location);
		final Cursor cursor = db.query(HomeDatabaseHelper.WINDOW_DOOR_SENSOR_TABLE_NAME, null, selection, null, null,
				null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				final WindowDoorSensor sensor = createWindowDoorSensor(cursor);
				sensors.add(sensor);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return sensors;

	}

	private List<RollerShutterActuator> getRollerShutterActuator(final SQLiteDatabase db,
			final SmartHomeLocation location) {

		final List<RollerShutterActuator> actuators = new ArrayList<RollerShutterActuator>();

		final String selection = createLocationSelection(location);
		final Cursor cursor = db.query(HomeDatabaseHelper.ROLLER_SHUTTER_TABLE_NAME, null, selection, null, null, null,
				null);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				final RollerShutterActuator actuator = createRollerShutterActuator(cursor);
				actuators.add(actuator);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return actuators;

	}

	private List<DaySensor> getDaySensors(final SQLiteDatabase db, final SmartHomeLocation location) {

		final List<DaySensor> sensors = new ArrayList<DaySensor>();

		final String selection = createLocationSelection(location);
		final Cursor cursor = db.query(HomeDatabaseHelper.DAY_SENSOR_TABLE_NAME, null, selection, null, null, null,
				null);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				final DaySensor sensor = createDaySensor(cursor);
				sensors.add(sensor);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return sensors;

	}

	private RoomTemperatureSensor getRoomTemperatureSensor(final SQLiteDatabase db, final String id) {

		RoomTemperatureSensor sensor = null;

		final String selection = createLogicalDeviceSelection(id);
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

	private RoomTemperatureActuator getRoomTemperatureActuator(final SQLiteDatabase db, final String id) {

		RoomTemperatureActuator sensor = null;

		final String selection = createLogicalDeviceSelection(id);
		final Cursor cursor = db.query(HomeDatabaseHelper.ROOM_TEMPERATURE_ACTUATOR_TABLE_NAME, null, selection, null,
				null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				sensor = createRoomTemperatureActuator(cursor);
			}
			cursor.close();
		}

		return sensor;

	}

	private WindowDoorSensor createWindowDoorSensor(final Cursor cursor) {

		final WindowDoorSensor sensor = new WindowDoorSensor();

		sensor.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		sensor.setDeviceName(getString(cursor, HomeDatabaseHelper.DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		sensor.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));

		sensor.setOpen(getBoolean(cursor, HomeDatabaseHelper.IS_OPEN_COL_NAME));
		sensor.setLocation(getLocation(sensor.getLocationId()));
		return sensor;

	};

	private RollerShutterActuator createRollerShutterActuator(final Cursor cursor) {

		final RollerShutterActuator actuator = new RollerShutterActuator();

		actuator.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		actuator.setDeviceName(getString(cursor, HomeDatabaseHelper.DEVICE_NAME_COL_NAME));
		actuator.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		actuator.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		actuator.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));

		actuator.setCalibrating(getBoolean(cursor, HomeDatabaseHelper.IS_CALIBRATING_COL_NAME));
		actuator.setOffLvl(getInt(cursor, HomeDatabaseHelper.OFF_LVL_COL_NAME));
		actuator.setOnLvl(getInt(cursor, HomeDatabaseHelper.ON_LVL_COL_NAME));
		actuator.setSCBh(getString(cursor, HomeDatabaseHelper.SCBH_COL_NAME));
		actuator.setShDT(getString(cursor, HomeDatabaseHelper.SH_DT_COL_NAME));
		actuator.setShutterLevel(getInt(cursor, HomeDatabaseHelper.SHUTTER_LEVEL_COL_NAME));
		actuator.setTmFD(getString(cursor, HomeDatabaseHelper.TM_FD_COL_NAME));
		actuator.setTmFU(getString(cursor, HomeDatabaseHelper.TM_FU_COL_NAME));

		actuator.setLocation(getLocation(actuator.getLocationId()));
		return actuator;

	};

	private DaySensor createDaySensor(final Cursor cursor) {

		final DaySensor sensor = new DaySensor();

		sensor.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		sensor.setDeviceName(getString(cursor, HomeDatabaseHelper.DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		sensor.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));

		try {
			sensor.setNextSunrise(DATE_FORMAT.parse(getString(cursor, HomeDatabaseHelper.NEXT_SUNRISE_COL_NAME)));
		} catch (final ParseException e) {
			Log.e(LOGTAG, "Can't parse date of next sunrise", e);
		}
		try {
			sensor.setNextSunset(DATE_FORMAT.parse(getString(cursor, HomeDatabaseHelper.NEXT_SUNSET_COL_NAME)));
		} catch (final ParseException e) {
			Log.e(LOGTAG, "Can't parse date of next sunset", e);
		}
		try {
			sensor.setNextTimeEvent(DATE_FORMAT.parse(getString(cursor, HomeDatabaseHelper.NEXT_TIME_EVENT_COL_NAME)));
		} catch (final ParseException e) {
			Log.e(LOGTAG, "Can't parse date of next time event", e);
		}
		sensor.setLocation(getLocation(sensor.getLocationId()));
		return sensor;

	};

	private RoomTemperatureSensor createRoomTemperatureSensor(final Cursor cursor) {

		final RoomTemperatureSensor sensor = new RoomTemperatureSensor();

		sensor.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		sensor.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		sensor.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));

		sensor.setTemperature(getDouble(cursor, HomeDatabaseHelper.TEMPERATURE_COL_NAME));
		sensor.setLocation(getLocation(sensor.getLocationId()));
		return sensor;

	};

	private RoomHumiditySensor createRoomHumiditySensor(final Cursor cursor) {

		final RoomHumiditySensor sensor = new RoomHumiditySensor();

		sensor.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		sensor.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		sensor.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		sensor.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));

		sensor.setHumidity(getDouble(cursor, HomeDatabaseHelper.HUMIDITY_COL_NAME));
		sensor.setLocation(getLocation(sensor.getLocationId()));
		return sensor;

	};

	private RoomTemperatureActuator createRoomTemperatureActuator(final Cursor cursor) {

		final RoomTemperatureActuator actuator = new RoomTemperatureActuator();

		actuator.setLocationId(getString(cursor, HomeDatabaseHelper.LOCATION_ID_COL_NAME));
		actuator.setLogicalDeviceId(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_ID_COL_NAME));
		actuator.setLogicalDeviceName(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_NAME_COL_NAME));
		actuator.setLogicalDeviceType(getString(cursor, HomeDatabaseHelper.LOGICAL_DEVICE_TYPE_COL_NAME));

		actuator.setPointTemperature(getDouble(cursor, HomeDatabaseHelper.POINT_TEMPERATURE));
		actuator.setOperationMode(getString(cursor, HomeDatabaseHelper.OPERATION_MODE_COL_NAME));
		actuator.setMaxTemperature(getDouble(cursor, HomeDatabaseHelper.MAX_TEMPERATURE_COL_NAME));
		actuator.setMinTemperature(getDouble(cursor, HomeDatabaseHelper.MIN_TEMPERATURE_COL_NAME));
		actuator.setIsLocked(getBoolean(cursor, HomeDatabaseHelper.IS_LOCKED_COL_NAME));

		actuator.setLocation(getLocation(actuator.getLocationId()));
		return actuator;

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

	public boolean deleteAllTemperatureActuators() {
		final int ret = database.delete(HomeDatabaseHelper.ROOM_TEMPERATURE_ACTUATOR_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public boolean deleteAllWindowDoorSensors() {
		final int ret = database.delete(HomeDatabaseHelper.WINDOW_DOOR_SENSOR_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public boolean deleteAllRollerShutterActuator() {
		final int ret = database.delete(HomeDatabaseHelper.ROLLER_SHUTTER_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public boolean deleteAllDaySensors() {
		final int ret = database.delete(HomeDatabaseHelper.DAY_SENSOR_TABLE_NAME, null, null);
		return (ret > 0);
	}

	public void deleteAll() {
		deleteAllLocations();
		deleteAllRoomHumidtySensors();
		deleteAllTemperatureSensors();
		deleteAllTemperatureActuators();
		deleteAllTemperatureHumidityDevices();
		deleteAllWindowDoorSensors();
		deleteAllDaySensors();
		deleteAllRollerShutterActuator();
	}

	public boolean addUpdateListener(final IDatabaseUpdateListener listner) {
		removeUpdateListener(listner);
		return updateListeners.add(listner);
	}

	public boolean removeUpdateListener(final IDatabaseUpdateListener listner) {
		return updateListeners.remove(listner);
	}

	private void fireUpdate(final LogicalDevice device) {
		for (final IDatabaseUpdateListener listener : updateListeners) {
			listener.updated(device);
		}
	}

}
