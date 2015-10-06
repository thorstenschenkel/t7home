package de.t7soft.android.t7home.smarthome.api.devices;

public class RollerShutterActuator extends LogicalDevice {

	private int OnLvl;
	private int OffLvl;
	private String ShDT;
	private String SCBh;
	private String TmFU;
	private String TmFD;
	private boolean calibrating;
	private int shutterLevel;

	public RollerShutterActuator() {
		setType(LogicalDevice.Type_RollerShutterActuator);
	}

	public int getOnLvl() {
		return OnLvl;
	}

	public void setOnLvl(final int onLvl) {
		OnLvl = onLvl;
	}

	public int getOffLvl() {
		return OffLvl;
	}

	public void setOffLvl(final int offLvl) {
		OffLvl = offLvl;
	}

	public String getShDT() {
		return ShDT;
	}

	public void setShDT(final String shDT) {
		ShDT = shDT;
	}

	public String getSCBh() {
		return SCBh;
	}

	public void setSCBh(final String sCBh) {
		SCBh = sCBh;
	}

	public String getTmFU() {
		return TmFU;
	}

	public void setTmFU(final String tmFU) {
		TmFU = tmFU;
	}

	public String getTmFD() {
		return TmFD;
	}

	public void setTmFD(final String tmFD) {
		TmFD = tmFD;
	}

	public boolean isCalibrating() {
		return calibrating;
	}

	public void setCalibrating(final boolean calibrating) {
		this.calibrating = calibrating;
	}

	public int getShutterLevel() {
		return shutterLevel;
	}

	public void setShutterLevel(final int shutterLevel) {
		this.shutterLevel = shutterLevel;
	}

}
