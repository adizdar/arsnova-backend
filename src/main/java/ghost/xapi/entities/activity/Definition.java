package ghost.xapi.entities.activity;

import ghost.xapi.entities.Translations;

/**
 * The type Definition.
 */
public class Definition {

	// TODO create arsnova config so it can be changed
	private final static String BASE_URL_ACTIVITIES = "http://adlnet.gov/expapi/activities/";

	private String type;
	private Translations name;
	private Translations description;

	/**
	 * @param type
	 */
	public Definition(String type) {
		this.type = BASE_URL_ACTIVITIES + type;
		this.name = new Translations();
		this.description = new Translations();
	}

	/**
	 * @param type
	 * @param name
	 * @param description
	 */
	public Definition(String type, Translations name, Translations description) {
		this.type = BASE_URL_ACTIVITIES + type;
		this.name = name;
		this.description = description;
	}

	/**
	 * @return java.lang.String
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return ghost.xapi.entities.Translations
	 */
	public Translations getName() {
		return name;
	}

	/**
	 * @return ghost.xapi.entities.Translations
	 */
	public Translations getDescription() {
		return description;
	}

	/**
	 * The name is a optional parameter for a activity it has a setter so it can be set latter if needed.
	 *
	 * @param name
	 */
	public void setName(Translations name) {
		this.name = name;
	}

	/**
	 * The description is a optional parameter for a activity it has a setter so it can be set latter if needed.
	 *
	 * @param description
	 */
	public void setDescription(Translations description) {
		this.description = description;
	}

}
