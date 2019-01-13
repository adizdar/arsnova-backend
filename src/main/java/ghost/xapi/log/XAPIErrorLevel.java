package ghost.xapi.log;

import org.apache.log4j.Level;

public class XAPIErrorLevel extends Level {

	/**
	 * Value of XAPIErrorLog4jLevel level. This value is higher than INFO_INT.
	 */
	public static final int XAPIError_INT = Level.INFO_INT + 13;

	/**
	 * Level representing the log level.
	 */
	public static final Level XAPIError = new XAPIErrorLevel(XAPIError_INT, "XAPIError", 7);

	/**
	 * @param level
	 * @param levelStr
	 * @param syslogEquivalent
	 */
	public XAPIErrorLevel(int level, String levelStr, int syslogEquivalent) {
		super(level, levelStr, syslogEquivalent);
	}

	/**
	 * Checks whether logArgument is "XAPIError" level. If yes then returns
	 * XAPIError}, else calls XAPIErrorLog4jLevel#toLevel(String, Level) passing
	 * it Level#DEBUG as the defaultLevel.
	 *
	 * @param logArgument
	 * @return Level
	 */
	public static Level toLevel(String logArgument) {
		if (logArgument != null && logArgument.toUpperCase().equals("XAPIError")) {
			return XAPIError;
		}
		return (Level) toLevel(logArgument, Level.DEBUG);
	}

	/**
	 * Checks whether val is XAPIErrorLog4jLevel#XAPIError_INT. If yes then
	 * returns XAPIErrorLog4jLevel#XAPIError, else calls
	 * XAPIErrorLog4jLevel#toLevel(int, Level) passing it Level#DEBUG as the
	 * defaultLevel
	 *
	 * @param val
	 * @return Level
	 */
	public static Level toLevel(int val) {
		if (val == XAPIError_INT) {
			return XAPIError;
		}
		return (Level) toLevel(val, Level.DEBUG);
	}

	/**
	 * Checks whether val is XAPIErrorLog4jLevel#XAPIError_INT. If yes
	 * then returns XAPIErrorLog4jLevel#XAPIError, else calls Level#toLevel(int, org.apache.log4j.Level)
	 *
	 * @param val
	 * @param defaultLevel
	 * @return Level
	 */
	public static Level toLevel(int val, Level defaultLevel) {
		if (val == XAPIError_INT) {
			return XAPIError;
		}

		return Level.toLevel(val, defaultLevel);
	}

	/**
	 * Checks whether logArgument is "XAPIError" level. If yes then returns
	 * XAPIErrorLog4jLevel#XAPIError, else calls
	 * Level#toLevel(java.lang.String, org.apache.log4j.Level)
	 *
	 * @param logArgument
	 * @param defaultLevel
	 * @return Level
	 */
	public static Level toLevel(String logArgument, Level defaultLevel) {
		if (logArgument != null && logArgument.toUpperCase().equals("XAPIError")) {
			return XAPIError;
		}

		return Level.toLevel(logArgument, defaultLevel);
	}

}
