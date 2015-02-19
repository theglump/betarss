package org.betarss.exception;

public class BetarssException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BetarssException() {
		super();
	}

	public BetarssException(String message, Throwable cause) {
		super(message, cause);
	}

	public BetarssException(String message) {
		super(message);
	}

	public BetarssException(Throwable cause) {
		super(cause);
	}

}
