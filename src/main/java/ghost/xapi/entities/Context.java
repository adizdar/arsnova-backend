package ghost.xapi.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.thm.arsnova.services.UserSessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Context {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object[] extension;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private HashMap<String, String> instructor;

	public Context() {
	}

	/**
	 * @param extension
	 */
	public Context(Object[] extension) {
		this.extension = extension;
	}

	/**
	 * @param extension
	 * @param instructor
	 */
	public Context(Object[] extension, HashMap<String, String> instructor) {
		this.extension = extension;
		this.instructor = instructor;
	}

	/**
	 * @return java.lang.Object[]
	 */
	public Object[] getExtension() {
		return extension;
	}

	/**
	 * @param extension
	 */
	public void setExtension(Object[] extension) {
		this.extension = extension;
	}

	/**
	 * @return java.util.HashMap<java.lang.String , java.lang.String>
	 */
	public HashMap<String, String> getInstructor() {
		return instructor;
	}

	/**
	 * @param instructorUsername
	 */
	public void setInstructor(String instructorUsername) {
		if (instructorUsername != null) {
			this.instructor = new HashMap<>();
			this.instructor.put("name", instructorUsername);
		}
	}

	/**
	 * @param role
	 */
	public void addRole(UserSessionService.Role role) {
		if (role != null) {
			ArrayList<Object> temp;
			if (this.extension != null) {
				temp = new ArrayList<Object>(Arrays.asList(this.extension));
			} else {
				temp = new ArrayList<Object>();
			}

			Map newObject = new HashMap<String, String>();
			newObject.put("Role", role.name());
			temp.add(newObject);

			this.extension = temp.toArray();
		}
	}
}
