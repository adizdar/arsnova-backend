package ghost.xapi.entities;

public class Definition {
	private String type;
	private Translations name;
	private Translations description;

	/**
	 * @param type
	 */
	public Definition(String type) {
		this.type = type;
		this.name = new Translations();
		this.description = new Translations();
	}

	/**
	 * @param type
	 * @param name
	 * @param description
	 */
	public Definition(String type, Translations name, Translations description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public Translations getName() {
		return name;
	}

	public Translations getDescription() {
		return description;
	}

	/**
	 * The name is a optional parameter for a Activity it has a setter so it can be set latter if needed.
	 *
	 * @param name
	 */
	public void setName(Translations name) {
		this.name = name;
	}

	/**
	 * The description is a optional parameter for a Activity it has a setter so it can be set latter if needed.
	 *
	 * @param description
	 */
	public void setDescription(Translations description) {
		this.description = description;
	}
}
