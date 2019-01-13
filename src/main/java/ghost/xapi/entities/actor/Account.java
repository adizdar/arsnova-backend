package ghost.xapi.entities.actor;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Account {

	private String name;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String homePage;

	/**
	 * @param name
	 * @param homePage
	 */
	public Account(String name, String homePage) {
		// TODO add role
		this.name = name;
		this.homePage = homePage;
	}

	/**
	 * @param name
	 */
	public Account(String name) {
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
	public String getHomePage() {
		return homePage;
	}

	/**
	 * @param homePage
	 */
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
}
