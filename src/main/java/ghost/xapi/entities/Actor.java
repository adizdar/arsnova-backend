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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

}
