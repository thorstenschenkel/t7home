package de.t7soft.android.t7home.smarthome.api.devices;

import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;

public class LogicalDevice extends SmartHomeDevice {
	private String type = "Generic";
	public static final String Type_Generic = "Generic";
	public static final String Type_SwitchActuatorState = "SwitchActuatorState";
	public static final String Type_RoomTemperatureActuatorState = "RoomTemperatureActuatorState";
	public static final String Type_RoomHumiditySensorState = "RoomHumiditySensorState";
	public static final String Type_GenericSensor = "GenericSensor";
	public static final String Type_DaySensor = "DaySensor";
	public static final String Type_LuminanceSensor = "LuminanceSensor";
	public static final String Type_MotionDetectionSensor = "MotionDetectionSensor";
	public static final String Type_RoomTemperatureSensorState = "RoomTemperatureSensorState";
	public static final String Type_RollerShutterActuator = "RollerShutterActuator";
	public static final String Type_Router = "Router";
	public static final String Type_SwitchActuator = "SwitchActuator";
	public static final String Type_PushButtonSensor = "PushButtonSensor";
	public static final String Type_DimmerActuator = "DimmerActuator";
	public static final String Type_DimmerActuatorState = "DimmerActuatorState";
	public static final String Type_RoomTemperatureActuator = "RoomTemperatureActuator";
	public static final String Type_RoomHumiditySensor = "RoomHumiditySensor";
	public static final String Type_RoomTemperatureSensor = "RoomTemperatureSensor";
	public static final String Type_GenericActuator = "GenericActuator";
	public static final String Type_SmsActuator = "SMSActuator";
	public static final String Type_EmailActuator = "EMailActuator";
	public static final String Type_GenericActuatorState = "GenericDeviceState";
	public static final String Type_WindowDoorSensor = "WindowDoorSensor";
	public static final String Type_SmokeDetectorSensor = "SmokeDetectorSensor";
	public static final String Type_SmokeDetectionSensorState = "SmokeDetectionSensorState";
	public static final String Type_AlarmActuator = "AlarmActuator";
	public static final String Type_AlarmActuatorState = "AlarmActuatorState";
	public static final String Type_WindowDoorSensorState = "WindowDoorSensorState";

	private String logicalDeviceId = "";
	private String logicalDeviceType = "";
	private String logicalDeviceName = "";
	private String locationId = "";
	private SmartHomeLocation location = new SmartHomeLocation();

	public SmartHomeLocation getLocation() {
		return location;
	}

	public void setLocation(SmartHomeLocation location) {
		this.location = location;
	}

	private Boolean temporaryChange = Boolean.FALSE;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public String getLogicalDeviceName() {
		return logicalDeviceName;
	}

	public void setLogicalDeviceName(String logicalDeviceName) {
		this.logicalDeviceName = logicalDeviceName;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the logicalDeviceId
	 */
	public String getLogicalDeviceId() {
		return logicalDeviceId;
	}

	/**
	 * @param logicalDeviceId
	 *            the logicalDeviceId to set
	 */
	public void setLogicalDeviceId(String logicalDeviceId) {
		this.logicalDeviceId = logicalDeviceId;
		this.setDeviceId(logicalDeviceId);
	}

	/**
	 * @return the logicalDeviceType
	 */
	public String getLogicalDeviceType() {
		return logicalDeviceType;
	}

	/**
	 * @param logicalDeviceType
	 *            the logicalDeviceType to set
	 */
	public void setLogicalDeviceType(String logicalDeviceType) {
		this.logicalDeviceType = logicalDeviceType;
	}

	/**
	 * @return the temporaryChange
	 */
	public Boolean getTemporaryChange() {
		return temporaryChange;
	}

	/**
	 * @param temporaryChange
	 *            the temporaryChange to set
	 */
	public void setTemporaryChange(Boolean temporaryChange) {
		this.temporaryChange = temporaryChange;
	}

	@Override
	public String toString() {
		return type + ": " + logicalDeviceId;
	}
}
