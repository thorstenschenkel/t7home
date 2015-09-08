package de.t7soft.android.t7home.smarthome.api;

import java.util.UUID;

import de.t7soft.android.t7home.smarthome.api.exceptions.LoginFailedException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SHTechnicalException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;

public class SmartHomeSession {

	// logon data
	private String userName;
	private String passWord;
	private String hostName;

	// IDs
	private final String sessionId = "";
	private String clientId;
	private final String requestId = "";

	public void logon(String userName, String passWord, String hostName)
			throws SHTechnicalException, LoginFailedException,
			SmartHomeSessionExpiredException {
		this.userName = userName;
		this.passWord = passWord;
		this.hostName = hostName;
		initialize();
	}

	private void initialize() throws SHTechnicalException,
			LoginFailedException, SmartHomeSessionExpiredException {
		clientId = UUID.randomUUID().toString();

	}
}
