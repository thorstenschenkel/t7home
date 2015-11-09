package de.t7soft.android.t7home.roomactivity;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.database.HomeDatabaseAdapter;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public class RoomActuatorChangeListener implements ActuatorChangeListener {

	private static final String LOGTAG = RoomActuatorChangeListener.class.getSimpleName();

	private static final Format TEMPERATURE_FORMAT = new DecimalFormat("#.#");

	private final Context context;
	private final String sessionId;
	private final HomeDatabaseAdapter dbAdapter;

	public RoomActuatorChangeListener(final Context context, final String sessionId, final HomeDatabaseAdapter dbAdapter) {
		this.context = context;
		this.sessionId = sessionId;
		this.dbAdapter = dbAdapter;
	}

	@Override
	public void changed(final LogicalDevice device, final String newValue) {

		final String deviceType = device.getType();
		final String deviceId = device.getDeviceId();

		if (dbAdapter.canWrite()) {
			try {
				if (deviceType.equals(LogicalDevice.Type_RoomTemperatureActuatorState)) {
					Object parsedValue = TEMPERATURE_FORMAT.parseObject(newValue);
					if (parsedValue instanceof Double) {
						final double temperature = (Double) parsedValue;
						dbAdapter.updateRoomTemperatureActuator(device, temperature);
					} else {
						Log.w(LOGTAG, "Can't update value in data base.");
					}
				} else if (deviceType.equals(LogicalDevice.Type_RollerShutterActuator)) {
					final int level = Integer.parseInt(newValue);
					dbAdapter.updateRollerShutterActuator(device, level);
				} else {
					Log.w(LOGTAG, "Unkown device type: " + deviceType);
					return;
				}
			} catch (final ParseException ex) {
				Log.e(LOGTAG, "Can't update value in data base.", ex);
			} catch (final NumberFormatException ex) {
				Log.e(LOGTAG, "Can't update value in data base.", ex);
			}
		}

		final SmartHomeSession session = new SmartHomeSession(sessionId);
		try {
			if (deviceType.equals(LogicalDevice.Type_RoomTemperatureActuatorState)) {
				try {
					Object parsedValue = TEMPERATURE_FORMAT.parseObject(newValue);
					if (parsedValue instanceof Double) {
						session.roomTemperatureActuatorChangeState(deviceId, parsedValue.toString());
						// session.refreshLogicalDeviceState();
					} else {
						Log.w(LOGTAG, "Cannot change device state!");
					}
				} catch (final ParseException ex) {
					Log.e(LOGTAG, "Cannot change device state!", ex);
				}
			} else if (deviceType.equals(LogicalDevice.Type_RollerShutterActuator)) {
				session.switchRollerShutter(deviceId, newValue);
				// session.refreshLogicalDeviceState();
			} else {
				Log.w(LOGTAG, "Unkown device type: " + deviceType);
				return;
			}
		} catch (final SmartHomeSessionExpiredException e) {
			e.printStackTrace();
			final int errorMsgResId = getErrorMessage(deviceType);
			if (errorMsgResId >= 0) {
				Toast.makeText(context, errorMsgResId, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private static int getErrorMessage(final String deviceType) {
		if (deviceType.equals(LogicalDevice.Type_RoomTemperatureActuator)) {
			return R.string.temperature_set_error;
		} else if (deviceType.equals(LogicalDevice.Type_RollerShutterActuator)) {
			return R.string.shutter_set_error;
		}
		return -1;
	}

}
