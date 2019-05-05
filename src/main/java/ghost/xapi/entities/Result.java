package ghost.xapi.entities;

import java.util.*;

public class Result {

	private Object[] extension;

	/**
	 * @param extension
	 */
	public Result(Object[] extension) {
		this.extension = extension;
	}

	/**
	 * @param key
	 * @param extension
	 */
	public Result(String key, Object[] extension) {
		Map<String, Object> result = new HashMap<>();
		result.put(key, extension);

		this.extension = new Object[]{ result };
	}

	/**
	 * @return java.lang.Object[]
	 */
	public Object[] getExtension() {
		return extension;
	}

	/**
	 * @param newObject
	 *
	 * @return
	 */
	public void appendValue(Object newObject) {
		ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(this.extension));
		temp.add(newObject);
		this.extension = temp.toArray();
	}
}
