package ghost.xapi.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Result {

	private boolean success = true;
	private boolean completion = true;
	private Object[] response;

	/**
	 * @param response
	 */
	public Result(Object[] response) {
		this.response = response;
	}

	/**
	 * @return boolean
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return boolean
	 */
	public boolean isCompletion() {
		return completion;
	}

	/**
	 * @return java.lang.Object[]
	 */
	public Object[] getResponse() {
		return response;
	}

	/**
	 * @param success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @param completion
	 */
	public void setCompletion(boolean completion) {
		this.completion = completion;
	}

	/**
	 * @param newObject
	 *
	 * @return
	 */
	public void appendValue(Object newObject) {
		ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(this.response));
		temp.add(newObject);
		this.response = temp.toArray();
	}
}
