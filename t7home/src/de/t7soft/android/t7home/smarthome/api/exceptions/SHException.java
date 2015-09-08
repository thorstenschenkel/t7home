package de.t7soft.android.t7home.smarthome.api.exceptions;

public class SHException extends Exception {

	public SHException() {
		super();
	}

	public SHException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SHException(String detailMessage) {
		super(detailMessage);
	}

	public SHException(Throwable throwable) {
		super(throwable);
	}

}
