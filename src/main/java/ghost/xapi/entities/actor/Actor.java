package ghost.xapi.entities.actor;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Actor {

	static final String ACTOR_PREFIX = "mailto:";

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;
	private String name;
	private String objectType = "Agent";
	private Account account;

	/**
	 * @param userName
	 */
	public Actor(String userName) {
		this.setNameAndEmailViaUsername(userName);
		this.account = new Account(this.name);
	}

	/**
	 * @param userName
	 * @param type
	 */
	public Actor(String userName, String type) {
		this.setNameAndEmailViaUsername(userName);
		this.account = new Account(this.name, type);
	}

	/**
	 * Maps name and email field via userName. The userName can be a email or just a plain name.
	 * @param userName
	 */
	private void setNameAndEmailViaUsername(String userName) {
		try {
			InternetAddress internetAddress = new InternetAddress(userName);
			internetAddress.validate();

			// It is a valid email address.
			this.email = ACTOR_PREFIX + userName;
			this.name = this.getNameFromEmail(userName);
		} catch (AddressException e) {
			// No email address so it is a username.
			this.name = userName;
		}
	}

	/**
	 * @param email
	 * @return java.lang.String
	 */
	private String getNameFromEmail(String email) {
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

	/**
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
