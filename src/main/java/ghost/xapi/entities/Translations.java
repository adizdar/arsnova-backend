package ghost.xapi.entities;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;

public class Translations {

	public static String NO_LANGUAGE_KEY = "unknownLanguage";

	protected HashMap<String, String> translation = new HashMap<String, String>();

	/**
	 * @return java.util.HashMap<java.lang.String , java.lang.String>
	 */
	@JsonValue
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

	/**
	 * @param value
	 */
	public void addNoLanguageTranslation(String value) {
		this.translation.put(NO_LANGUAGE_KEY, value);
	}

	/**
	 * @param value
	 */
	public void addNoLanguageTranslationFromCamelCase(String value) {
		this.addNoLanguageTranslation(this.convertCamelCaseToSpace(value));
	}

	/**
	 * @param camelCaseString
	 * @return String
	 */
	public String convertCamelCaseToSpace(String camelCaseString) {
		String result = camelCaseString.replaceAll(
				String.format("%s|%s|%s",
						"(?<=[A-Z])(?=[A-Z][a-z])",
						"(?<=[^A-Z])(?=[A-Z])",
						"(?<=[A-Za-z])(?=[^A-Za-z])"
				),
				" "
		);

		return result.trim();
	}

}
