package ghost.xapi.entities.activity;

import ghost.xapi.entities.Translations;
import org.springframework.beans.factory.annotation.Value;

public class Activity {

	@Value(value = "${root-url}") private String rootUrl;

	/**
	 * @copyright https://watershedlrs.zendesk.com/hc/en-us/articles/214880383-Get-the-Activity-ID-Right
	 * Activity ID Rules:
	 *
	 * - The Activity ID should be a web address, starting with http:// or https://.
	 *
	 * - That web address should be within a website controlled by the organization creating or commissioning the content,
	 *  whether that’s an organization or a content vendor.
	 *
	 * - You don’t have to host anything at this address, it doesn't need to be the address where the content is hosted;
	 * the point is to make sure the ID is universally unique.
	 *
	 * - The Activity ID should be something meaningful when you read it, making it easier for you to identify
	 *  the activity from the ID.
	 */
	private final static String ACTIVIRY_URL = rootUrl + "/xapi/activities/";

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
		this.id = ACTIVIRY_URL + id;
		this.definition = new Definition(type, name, description);
	}

	/**
	 * @param id
	 * @param definition
	 */
	public Activity(String id, Definition definition) {
		this.id = ACTIVIRY_URL + id;
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
