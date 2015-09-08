package de.t7soft.android.t7home.smarthome.api.exceptions;

public class SHFunctionalException extends SHException {

	private static final long serialVersionUID = 6363956776637899440L;

	public SHFunctionalException() {
	}

	public SHFunctionalException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SHFunctionalException(String detailMessage) {
		super(detailMessage);
	}

	public SHFunctionalException(Throwable throwable) {
		super(throwable);
	}

}
