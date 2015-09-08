package de.t7soft.android.t7home.smarthome.api.exceptions;

public class LoginFailedException extends SHFunctionalException {

	private static final long serialVersionUID = 8906345144679812935L;

	public LoginFailedException() {
	}

	public LoginFailedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public LoginFailedException(String detailMessage) {
		super(detailMessage);
	}

	public LoginFailedException(Throwable throwable) {
		super(throwable);
	}

}
