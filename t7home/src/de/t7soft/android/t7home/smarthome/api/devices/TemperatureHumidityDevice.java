package de.t7soft.android.t7home.smarthome.api.devices;

import de.t7soft.android.t7home.smarthome.api.SmartHomeLocation;

public class TemperatureHumidityDevice {

	private SmartHomeLocation location = null;
	private RoomTemperatureSensor temperatureSensor = null;
	private RoomTemperatureActuator temperatureActuator = null;
	private RoomHumiditySensor roomHumidtySensor = null;

	public SmartHomeLocation getLocation() {
		return location;
	}

	public void setLocation(SmartHomeLocation location) {
		this.location = location;
	}

	public RoomTemperatureSensor getTemperatureSensor() {
		return temperatureSensor;
	}

	public void setTemperatureSensor(RoomTemperatureSensor temperatureSensor) {
		this.temperatureSensor = temperatureSensor;
	}

	public RoomTemperatureActuator getTemperatureActuator() {
		return temperatureActuator;
	}

	public void setTemperatureActuator(
			RoomTemperatureActuator temperatureActuator) {
		this.temperatureActuator = temperatureActuator;
	}

	public RoomHumiditySensor getRoomHumidtySensor() {
		return roomHumidtySensor;
	}

	public void setRoomHumidtySensor(RoomHumiditySensor roomHumidtySensor) {
		this.roomHumidtySensor = roomHumidtySensor;
	}

	public String getLocationId() {
		return location.getLocationId();
	}

	public void setLocationId(String locationId) {
		this.location.setLocationId(locationId);
	}

}
