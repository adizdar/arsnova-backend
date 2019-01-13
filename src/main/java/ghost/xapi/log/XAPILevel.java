package ghost.xapi.log;

import org.apache.log4j.Level;

public class XAPILevel extends Level {

	/**
	 * Value of XAPILog4jLevel level. This value is higher than INFO_INT.
	 */
	public static final int XAPI_INT = Level.INFO_INT + 17;

	/**
	 * Level representing the log level.
	 */
	public static final Level XAPI = new XAPILevel(XAPI_INT, "XAPI", 10);

	/**
	 * @param level
	 * @param levelStr
	 * @param syslogEquivalent
	 */
	public XAPILevel(int level, String levelStr, int syslogEquivalent) {
		super(level, levelStr, syslogEquivalent);
	}

	/**
	 * Checks whether logArgument is "XAPI" level. If yes then returns
	 * XAPI}, else calls XAPILog4jLevel#toLevel(String, Level) passing
	 * it Level#DEBUG as the defaultLevel.
	 *
	 * @param logArgument
	 * @return Level
	 */
	public static Level toLevel(String logArgument) {
		if (logArgument != null && logArgument.toUpperCase().equals("XAPI")) {
			return XAPI;
		}
		return (Level) toLevel(logArgument, Level.DEBUG);
	}

	/**
	 * Checks whether val is XAPILog4jLevel#XAPI_INT. If yes then
	 * returns XAPILog4jLevel#XAPI, else calls
	 * XAPILog4jLevel#toLevel(int, Level) passing it Level#DEBUG as the
	 * defaultLevel
	 *
	 * @param val
	 * @return Level
	 */
	public static Level toLevel(int val) {
		if (val == XAPI_INT) {
			return XAPI;
		}
		return (Level) toLevel(val, Level.DEBUG);
	}

	/**
	 * Checks whether val is XAPILog4jLevel#XAPI_INT. If yes
	 * then returns XAPILog4jLevel#XAPI, else calls Level#toLevel(int, org.apache.log4j.Level)
	 *
	 * @param val
	 * @param defaultLevel
	 * @return Level
	 */
	public static Level toLevel(int val, Level defaultLevel) {
		if (val == XAPI_INT) {
			return XAPI;
		}

		return Level.toLevel(val, defaultLevel);
	}

	/**
	 * Checks whether logArgument is "XAPI" level. If yes then returns
	 * XAPILog4jLevel#XAPI, else calls
	 * Level#toLevel(java.lang.String, org.apache.log4j.Level)
	 *
	 * @param logArgument
	 * @param defaultLevel
	 * @return Level
	 */
	public static Level toLevel(String logArgument, Level defaultLevel) {
		if (logArgument != null && logArgument.toUpperCase().equals("XAPI")) {
			return XAPI;
		}

		return Level.toLevel(logArgument, defaultLevel);
	}
}
