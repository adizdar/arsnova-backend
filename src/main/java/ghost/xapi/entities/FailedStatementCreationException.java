package ghost.xapi.entities;

public class FailedStatementCreationException extends Exception {

	private static final String ERROR_PREFIX = "Error occurred in Statement creation process: ";

	public FailedStatementCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getMessage() {
		return ERROR_PREFIX + super.getMessage();
	}

	public FailedStatementCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
