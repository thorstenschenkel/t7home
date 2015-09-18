package de.t7soft.android.t7home;

public class LogonData {

	private String username;
	private String password;
	private String ipAddress;

	public LogonData() {

	}

	public LogonData(String username, String password, String ipAddress) {
		super();
		this.username = username;
		this.password = password;
		this.ipAddress = ipAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}