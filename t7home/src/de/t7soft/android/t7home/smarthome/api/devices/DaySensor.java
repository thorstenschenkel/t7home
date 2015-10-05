package de.t7soft.android.t7home.smarthome.api.devices;

import java.util.Date;

public class DaySensor extends LogicalDevice {

	private String latitude;
	private String longitude;
	private Date nextSunset;
	private Date nextSunrise;
	private Date nextTimeEvent;

	public DaySensor() {
		super();
		this.setType(LogicalDevice.Type_DaySensor);
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(final String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(final String longitude) {
		this.longitude = longitude;
	}

	public Date getNextSunset() {
		return nextSunset;
	}

	public void setNextSunset(final Date nextSunset) {
		this.nextSunset = nextSunset;
	}

	public Date getNextSunrise() {
		return nextSunrise;
	}

	public void setNextSunrise(final Date nextSunrise) {
		this.nextSunrise = nextSunrise;
	}

	public Date getNextTimeEvent() {
		return nextTimeEvent;
	}

	public void setNextTimeEvent(final Date nextTimeEvent) {
		this.nextTimeEvent = nextTimeEvent;
	}

}
