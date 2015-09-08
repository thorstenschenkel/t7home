package de.t7soft.android.t7home.smarthome.api.exceptions;

public class SHTechnicalException extends SHException {

	private static final long serialVersionUID = -1258148678822125774L;

	public SHTechnicalException() {
	}

	public SHTechnicalException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SHTechnicalException(String detailMessage) {
		super(detailMessage);
	}

	public SHTechnicalException(Throwable throwable) {
		super(throwable);
	}

}
