package de.t7soft.android.t7home.roomactivity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import de.t7soft.android.t7home.R;
import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public class RoomActuatorChangeListener implements ActuatorChangeListener {

	private static final String LOGTAG = RoomActuatorChangeListener.class.getSimpleName();

	private final Context context;
	private final String sessionId;

	public RoomActuatorChangeListener(final Context context, final String sessionId) {
		this.context = context;
		this.sessionId = sessionId;
	}

	@Override
	public void changed(final String deviceId, final String deviceType, final String newValue) {

		// TODO Progressbar?

		final SmartHomeSession session = new SmartHomeSession(sessionId);
		try {
			if (deviceType.equals(LogicalDevice.Type_RoomTemperatureActuatorState)) {
				session.roomTemperatureActuatorChangeState(deviceId, newValue);
				session.refreshLogicalDeviceState();
				// TODO update in database (only temperatrue !?! )
			} else if (deviceType.equals(LogicalDevice.Type_RollerShutterActuator)) {
				session.switchRollerShutter(deviceId, newValue);
				session.refreshLogicalDeviceState();
				// TODO update in database (only level !?! )
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
