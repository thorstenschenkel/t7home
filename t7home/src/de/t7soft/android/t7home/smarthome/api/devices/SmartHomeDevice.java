package de.t7soft.android.t7home.smarthome.api.devices;

public class SmartHomeDevice {
	private String deviceId = "";
	private String deviceName = "";
	private String friendlyName = "";
	private String protocolId = "";
	private String addInProperties = "";
	private String protocolSpecificProperties = "";
	private String appId = "";
	private String timeOfDiscovery = "";
	private String timeOfAcceptance = "";

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName
	 *            the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the friendlyName
	 */
	public String getFriendlyName() {
		return friendlyName;
	}

	/**
	 * @param friendlyName
	 *            the friendlyName to set
	 */
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	/**
	 * @return the protocolId
	 */
	public String getProtocolId() {
		return protocolId;
	}

	/**
	 * @param protocolId
	 *            the protocolId to set
	 */
	public void setProtocolId(String protocolId) {
		this.protocolId = protocolId;
	}

	/**
	 * @return the addInProperties
	 */
	public String getAddInProperties() {
		return addInProperties;
	}

	/**
	 * @param addInProperties
	 *            the addInProperties to set
	 */
	public void setAddInProperties(String addInProperties) {
		this.addInProperties = addInProperties;
	}

	/**
	 * @return the protocolSpecificProperties
	 */
	public String getProtocolSpecificProperties() {
		return protocolSpecificProperties;
	}

	/**
	 * @param protocolSpecificProperties
	 *            the protocolSpecificProperties to set
	 */
	public void setProtocolSpecificProperties(String protocolSpecificProperties) {
		this.protocolSpecificProperties = protocolSpecificProperties;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the timeOfDiscovery
	 */
	public String getTimeOfDiscovery() {
		return timeOfDiscovery;
	}

	/**
	 * @param timeOfDiscovery
	 *            the timeOfDiscovery to set
	 */
	public void setTimeOfDiscovery(String timeOfDiscovery) {
		this.timeOfDiscovery = timeOfDiscovery;
	}

	/**
	 * @return the timeOfAcceptance
	 */
	public String getTimeOfAcceptance() {
		return timeOfAcceptance;
	}

	/**
	 * @param timeOfAcceptance
	 *            the timeOfAcceptance to set
	 */
	public void setTimeOfAcceptance(String timeOfAcceptance) {
		this.timeOfAcceptance = timeOfAcceptance;
	}

}
