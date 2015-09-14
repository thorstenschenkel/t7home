package de.t7soft.android.t7home.smarthome.api.devices;

public class RoomTemperatureSensor extends LogicalDevice {

	private Double temperature = Double.valueOf(0.0);

	public RoomTemperatureSensor() {
		this.setType(LogicalDevice.Type_RoomTemperatureSensorState);
	}

	/**
	 * @return the pointTemperature
	 */
	public Double getTemperature() {
		return temperature;
	}

	/**
	 * @param pointTemperature
	 *            the pointTemperature to set
	 */
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

}
