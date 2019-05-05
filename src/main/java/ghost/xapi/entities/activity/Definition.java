package ghost.xapi.entities.activity;

import com.fasterxml.jackson.annotation.JsonInclude;
import ghost.xapi.entities.Translations;

/**
 * The type Definition.
 */
public class Definition {

	private String type;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Translations name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
