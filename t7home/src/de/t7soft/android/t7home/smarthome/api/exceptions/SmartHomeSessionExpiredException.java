package de.t7soft.android.t7home.smarthome.api.exceptions;

public class SmartHomeSessionExpiredException extends SHFunctionalException {

	public static final String ERROR_CODE_UNAUTHORIZED = "unauthorized";
	public static final String ERROR_CODE_NO_SESSION_ID = "no session ID";
	public static final String ERROR_CODE_ILLEGAL_SESSION_ID = "illegal session ID";
	public static final String ERROR_CODE_NO_RESPONSE = "no response";
	public static final String ERROR_CODE_PARSE_ERROR = "parse error";

	private static final long serialVersionUID = -1239607831181318883L;

	public SmartHomeSessionExpiredException() {
	}

	public SmartHomeSessionExpiredException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SmartHomeSessionExpiredException(final String detailMessage) {
		super(detailMessage);
	}

	public SmartHomeSessionExpiredException(final Throwable throwable) {
		super(throwable);
	}

}
