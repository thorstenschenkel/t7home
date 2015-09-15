package de.t7soft.android.t7home.smarthome.api.devices;

import java.util.ArrayList;
import java.util.List;

public class RoomTemperatureActuator extends LogicalDevice {

	public final static String OPERATION_MODE_AUTO = "Auto";
	public final static String OPERATION_MODE_MANU = "Manu";

	private Double pointTemperature = Double.valueOf(0.0);
	private String operationMode = "";
	private String windowReductionActive = "";
	private List<String> underlyingDevicesIds = new ArrayList<String>(2);
	private Double maxTemperature = Double.valueOf(0.0);
	private Double minTemperature = Double.valueOf(0.0);
	private Double preheatFactor = Double.valueOf(0.0);
	private Boolean isLocked = false;
	private Boolean isFreezeProtectionActivated = false;
	private Double freezeProtection = Double.valueOf(0.0);
	private Boolean isMoldProtectionActivated = false;
	private Double humidityMoldProtection = Double.valueOf(0.0);
	private Double windowsOpenTemperature = Double.valueOf(0.0);
	private RoomHumiditySensor roomHumiditySensor = null;
	private RoomTemperatureSensor roomTemperatureSensor = null;

	public Double getPointTemperature() {
		return pointTemperature;
	}

	public void setPointTemperature(Double pointTemperature) {
		this.pointTemperature = pointTemperature;
	}

	public String getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(String operationMode) {
		this.operationMode = operationMode;
	}

	public String getWindowReductionActive() {
		return windowReductionActive;
	}

	public void setWindowReductionActive(String windowReductionActive) {
		this.windowReductionActive = windowReductionActive;
	}

	public List<String> getUnderlyingDevicesIds() {
		return underlyingDevicesIds;
	}

	public void setUnderlyingDevicesIds(List<String> underlyingDevicesIds) {
		this.underlyingDevicesIds = underlyingDevicesIds;
	}

	public Double getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(Double maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public Double getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(Double minTemperature) {
		this.minTemperature = minTemperature;
	}

	public Double getPreheatFactor() {
		return preheatFactor;
	}

	public void setPreheatFactor(Double preheatFactor) {
		this.preheatFactor = preheatFactor;
	}

	public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public Boolean getIsFreezeProtectionActivated() {
		return isFreezeProtectionActivated;
	}

	public void setIsFreezeProtectionActivated(Boolean isFreezeProtectionActivated) {
		this.isFreezeProtectionActivated = isFreezeProtectionActivated;
	}

	public Double getFreezeProtection() {
		return freezeProtection;
	}

	public void setFreezeProtection(Double freezeProtection) {
		this.freezeProtection = freezeProtection;
	}

	public Boolean getIsMoldProtectionActivated() {
		return isMoldProtectionActivated;
	}

	public void setIsMoldProtectionActivated(Boolean isMoldProtectionActivated) {
		this.isMoldProtectionActivated = isMoldProtectionActivated;
	}

	public Double getHumidityMoldProtection() {
		return humidityMoldProtection;
	}

	public void setHumidityMoldProtection(Double humidityMoldProtection) {
		this.humidityMoldProtection = humidityMoldProtection;
	}

	public Double getWindowsOpenTemperature() {
		return windowsOpenTemperature;
	}

	public void setWindowsOpenTemperature(Double windowsOpenTemperature) {
		this.windowsOpenTemperature = windowsOpenTemperature;
	}

	public RoomHumiditySensor getRoomHumiditySensor() {
		return roomHumiditySensor;
	}

	public void setRoomHumiditySensor(RoomHumiditySensor roomHumiditySensor) {
		this.roomHumiditySensor = roomHumiditySensor;
	}

	public RoomTemperatureSensor getRoomTemperatureSensor() {
		return roomTemperatureSensor;
	}

	public void setRoomTemperatureSensor(RoomTemperatureSensor roomTemperatureSensor) {
		this.roomTemperatureSensor = roomTemperatureSensor;
	}

}
