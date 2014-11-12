package org.betarss.core;

public class FeedFilterException extends Exception {

	private static final long serialVersionUID = -5964598545149249700L;

	public FeedFilterException() {
		super();
	}

	public FeedFilterException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public FeedFilterException(String message) {
		super(message);
	}

	public FeedFilterException(Throwable throwable) {
		super(throwable);
	}

}
