package de.t7soft.android.t7home.smarthome.api.exceptions;

public class SmartHomeSessionExpiredException extends SHFunctionalException {

	private static final long serialVersionUID = -1239607831181318883L;

	public SmartHomeSessionExpiredException() {
	}

	public SmartHomeSessionExpiredException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SmartHomeSessionExpiredException(String detailMessage) {
		super(detailMessage);
	}

	public SmartHomeSessionExpiredException(Throwable throwable) {
		super(throwable);
	}

}
