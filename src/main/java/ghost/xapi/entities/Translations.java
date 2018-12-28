package ghost.xapi.entities;

import java.util.HashMap;

public class Translations {

	private HashMap<String, String> translation = new HashMap<String, String>();

	/**
	 * @return java.util.HashMap<java.lang.String , java.lang.String>
	 */
	public HashMap<String, String> getTranslation() {
		return translation;
	}

	/**
	 * @param language
	 * @param value
	 */
	public void addTranslation(String language, String value) {
		this.translation.put(language, value);
	}

}
