package de.t7soft.android.t7home.smarthome.api.devices;

/**
 * TODO GetEntitiesResponse -> <Installation>Window</Installation>
 * 
 */
public class WindowDoorSensor extends LogicalDevice {

	private boolean isOpen = false;
	private String state = "";

	public WindowDoorSensor() {
		super();
		this.setType(LogicalDevice.Type_WindowDoorSensor);
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(final boolean isOpen) {
		this.isOpen = isOpen;
		if (isOpen)
			state = "open";
		else
			state = "closed";
	}

	public String getState() {
		return state;
	}

}
