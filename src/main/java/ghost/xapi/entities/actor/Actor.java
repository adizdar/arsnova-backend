package ghost.xapi.entities.actor;

public class Actor {

	static final String ACTOR_PREFIX = "mailto:";

	private String email;
	private String objectType;
	private Account account;

	/**
	 * @param email
	 */
	public Actor(String email) {
		this.email = ACTOR_PREFIX + email;
		this.account = new Account(this.getNameFromEmail(email));
	}

	/**
	 * Becasue name is a optional parameter for the xAPI statement.
	 *
	 * @param email
	 * @param type
	 */
	public Actor(String email, String type) {
		this.email = ACTOR_PREFIX + email;
		this.objectType = type;
		this.account = new Account(this.getNameFromEmail(email));
	}

	/**
	 * @param email
	 * @return java.lang.String
	 */
	private String getNameFromEmail(String email) {
		// TODO add email validation
		return email.substring(0, email.indexOf("@"));
	}

	/**
	 * @return java.lang.String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return java.lang.String
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return ghost.xapi.entities.actor.Account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

}
