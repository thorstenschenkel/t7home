package de.t7soft.android.t7home.smarthome.api.devices;

import java.util.ArrayList;
import java.util.List;

public class RoomTemperatureActuator extends LogicalDevice {

	private final Double pointTemperature = new Double(0.0);
	private final String operationMode = "";
	private final String windowReductionActive = "";
	private final List<String> underlyingDevicesIds = new ArrayList<String>(2);
	private final Double maxTemperature = new Double(0.0);
	private final Double minTemperature = new Double(0.0);
	private final Double preheatFactor = new Double(0.0);
	private final Boolean isLocked = false;
	private final Boolean isFreezeProtectionActivated = false;
	private final Double freezeProtection = new Double(0.0);
	private final Boolean isMoldProtectionActivated = false;
	private final Double humidityMoldProtection = new Double(0.0);
	private final Double windowsOpenTemperature = new Double(0.0);
	private final RoomHumiditySensor roomHumiditySensor = null;
	private final RoomTemperatureSensor roomTemperatureSensor = null;

}
