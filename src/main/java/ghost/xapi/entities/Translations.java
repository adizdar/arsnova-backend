package ghost.xapi.entities;

import java.util.HashMap;

public class Translations {

	private HashMap<String, String> translation = new HashMap<String, String>();

	public HashMap<String, String> getMap() {
		return translation;
	}

	public void addTranslation(String language, String value) {
		this.translation.put(language, value);
	}

}
