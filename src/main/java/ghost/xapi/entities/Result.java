package ghost.xapi.entities;

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
}
