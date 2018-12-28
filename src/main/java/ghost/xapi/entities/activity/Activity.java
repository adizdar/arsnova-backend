package ghost.xapi.entities.activity;

import ghost.xapi.entities.Translations;

public class Activity {

	/**
	 * @param id Info how the id is composed: https://xapi.com/statements-101/
	 */
	private String id;
	private Definition definition;

	/**
	 * Minimal information for a activity are id and type.
	 *
	 * @param id
	 * @param type
	 */
	public Activity(String id, String type) {
		this.id = id;
		this.definition = new Definition(type);
	}

	/**
	 * @param id
	 * @param type
	 * @param name
	 * @param description
	 */
	public Activity(String id, String type, Translations name, Translations description) {
		this.id = id;
		this.definition = new Definition(type, name, description);
	}

	/**
	 * @param id
	 * @param definition
	 */
	public Activity(String id, Definition definition) {
		this.id = id;
		this.definition = definition;
	}

	/**
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return ghost.xapi.entities.activity.Definition
	 */
	public Definition getDefinition() {
		return definition;
	}
}
