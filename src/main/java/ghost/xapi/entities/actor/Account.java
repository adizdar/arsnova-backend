package ghost.xapi.entities.actor;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Account {

	private String name;
	private String type;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String homePage;

	/**
	 * @param name
	 * @param type
	 */
	public Account(String name, String type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * @param name
	 */
	public Account(String name) {
		this.name = name;
	}

	/**
	 * @param name
	 * @param type
	 * @param homePage
	 */
	public Account(String name, String type, String homePage) {
		this.name = name;
		this.type = type;
		this.homePage = homePage;
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

	/**
	 * @return java.lang.String
	 */
	public String getType() {
		return type;
	}
}
