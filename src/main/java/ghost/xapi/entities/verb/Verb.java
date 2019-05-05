package ghost.xapi.entities.verb;

import com.fasterxml.jackson.annotation.JsonProperty;
import ghost.xapi.entities.Translations;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class Verb {

	private String id;
	private Translations display;

	/**
	 * @param id
	 */
	public Verb(String id) {
		this.id = id;
		this.display = new Translations();
	}

	/**
	 * @param id
	 * @param display
	 */
	public Verb(String id, Translations display) {
		this.id = id;
		this.display = display;
	}

	/**
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return ghost.xapi.entities.Translations
	 */
	@JsonProperty("display")
	public Translations getDisplay() {
		return display;
	}

}
