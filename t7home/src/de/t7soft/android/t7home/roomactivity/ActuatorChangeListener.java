package de.t7soft.android.t7home.roomactivity;

public interface ActuatorChangeListener {

	void changed(final String deviceId, final String deviceType, final String newValue);

}
