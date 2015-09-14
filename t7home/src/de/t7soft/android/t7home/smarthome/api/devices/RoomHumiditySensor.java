package de.t7soft.android.t7home.smarthome.api.devices;

public class RoomHumiditySensor extends LogicalDevice {

	private Double humidity = Double.valueOf(0.0);

	public RoomHumiditySensor() {
		this.setType(LogicalDevice.Type_RoomHumiditySensorState);
	}

	/**
	 * @return the humidity
	 */
	public Double getHumidity() {
		return humidity;
	}

	/**
	 * @param humidity
	 *            the humidity to set
	 */
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}

}
