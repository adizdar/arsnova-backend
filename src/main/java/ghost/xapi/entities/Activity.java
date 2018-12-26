package ghost.xapi.entities;

public class Activity {

	/**
	 * @param id Info how the id is composed: https://xapi.com/statements-101/
	 */
	private String id;
	private Definition definition;

	/**
	 * Minimal information for a Activity are id and type.
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

	public String getId() {
		return id;
	}

	public Definition getDefinition() {
		return definition;
	}

}
