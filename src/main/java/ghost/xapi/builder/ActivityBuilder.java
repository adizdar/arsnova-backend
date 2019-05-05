package ghost.xapi.builder;

import ghost.xapi.entities.Translations;
import ghost.xapi.entities.activity.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActivityBuilder {

	@Value(value = "${root-url}")
	private String rootUrl;
	@Value(value = "${xapi.activity.base-url}")
	private String activityIdBaseUrl;

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
	private final static String ACTIVITY_URI = "/xapi/activity/";

	/**
	 * @param id
	 * @param type
	 * @return Activity
	 */
	public Activity createActivity(String id, String type) {
		this.activityIdBaseUrl = (this.activityIdBaseUrl != null && !this.activityIdBaseUrl.isEmpty()) ? this.activityIdBaseUrl : this.rootUrl;

		Activity activity = new Activity(
				this.activityIdBaseUrl + ACTIVITY_URI + id,
				this.definitionTypeBaseUrl + type
		);
		activity.getDefinition().getName().addDefaultLanguageKey(Translations.convertCamelCaseToSpace(type));

		return activity;
	}

	/**
	 * @param elements
	 * @return String
	 */
	public String createActivityId(String[] elements) {
		return String.join("#", elements);
	}

}
