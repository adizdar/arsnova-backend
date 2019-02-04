package ghost.xapi.entities.activity;

public class Activity {

	private String id;
	private Definition definition;
	private String objectType = "Activity";

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

	/**
	 * @return java.lang.String
	 */
	public String getObjectType() {
		return objectType;
	}
}
