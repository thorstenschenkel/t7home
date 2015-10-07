package de.t7soft.android.t7home.database;

import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;

public interface DatabaseUpdateListener {

	void updated(final LogicalDevice device, final String newValue);

}
