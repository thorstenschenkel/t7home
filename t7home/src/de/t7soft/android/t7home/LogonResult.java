package de.t7soft.android.t7home;

import de.t7soft.android.t7home.smarthome.api.SmartHomeSession;

public class LogonResult {

	public static final int LOGON_OK = 0;
	public static final int LOGON_LOGIN_FAILED = 1;
	public static final int LOGON_SESSION_EXPIRED = 2;
	public static final int LOGON_TECHNICAL_EXCEPTION = 3;

	private int resultCode;
	private SmartHomeSession session;

	public LogonResult(int resultCode, SmartHomeSession session) {
		super();
		this.resultCode = resultCode;
		this.session = session;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public SmartHomeSession getSession() {
		return session;
	}

	public void setSession(SmartHomeSession session) {
		this.session = session;
	}

}