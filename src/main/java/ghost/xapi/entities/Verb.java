package ghost.xapi.entities;

import ghost.xapi.entities.Translations;

public class Verb {

	// TODO add config field
	private static String baseUrlForVerbsDefinition = "http://adlnet.gov/expapi/verbs/";

	private String id;
	private Translations display;

	/**
	 * @param id
	 */
	public Verb(String id) {
		this.id = baseUrlForVerbsDefinition + id;
		this.display = new Translations();
	}

	/**
	 * @param id
	 * @param display
	 */
	public Verb(String id, Translations display) {
		this.id = baseUrlForVerbsDefinition + id;
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
	public Translations getDisplay() {
		return display;
	}

	/**
	 * @param language The display language that is supported like: en-US, de, es...
	 * @param verb The defined action between the actor and activity in the supported language.
	 */
	public void addTranslation(String language, String verb) {
		this.display.addTranslation(language, verb);
	}

}
