package de.t7soft.android.t7home.roomactivity;

import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;

public interface ActuatorChangeListener {

	void changed(final LogicalDevice device, final String newValue);

}
