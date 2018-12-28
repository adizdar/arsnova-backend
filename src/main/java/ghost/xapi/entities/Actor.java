package ghost.xapi.entities;

public class Actor {

	private String email;
	private String name;

	/**
	 * @param email
	 */
	public Actor(String email) {
		this.email = email;
	}

	/**
	 * Becasue name is a optional parameter for the xAPI statement.
	 *
	 * @param email
	 * @param name
	 */
	public Actor(String email, String name) {
		this.email = email;
		this.name = name;
	}

	/**
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return java.lang.String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
