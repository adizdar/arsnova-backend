package ghost.xapi.statements.motd;

import ghost.xapi.entities.Statement;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Mapping path: "/motd"
 */
@Service
public class SocketStatementBuilderService extends AbstractStatementBuilderService {

	/**
	 * @param request method POST
	 * path /assign
	 *
	 * @return
	 */
	public Statement buildForAssignSocket(HttpServletRequest request) {
		String username = request.getParameter("username");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"register/user",
				username
		});

		return new Statement(
				new Actor(username),
				this.verbBuilder.createVerb("register"),
				this.activityBuilder.createActivity(activityId, "newUser")
		);
	}

	/**
	 * @param request method POST || GET
	 * path /{username}/activate
	 *
	 * @return
	 */
	public Statement buildForActivateUser(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String username = (String) pathVariables.get("username");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"activate/user",
				username
		});

		return new Statement(
				new Actor(username),
				this.verbBuilder.createVerb("activate"),
				this.activityBuilder.createActivity(activityId, "user")
		);
	}

	/**
	 * @param request method DELETE
	 * path /{username}/
	 *
	 * @return
	 */
	public Statement buildForDeleteUser(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String username = (String) pathVariables.get("username");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"delete/user",
				username
		});

		return new Statement(
				new Actor(username),
				this.verbBuilder.createVerb("delete"),
				this.activityBuilder.createActivity(activityId, "user")
		);
	}

	/**
	 * @param request method POST
	 * path /{username}/resetpassword
	 *
	 * @return
	 */
	public Statement buildForResetPassword(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String username = (String) pathVariables.get("username");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"resetPassword/user",
				username
		});

		return new Statement(
				new Actor(username),
				this.verbBuilder.createVerb("reset"),
				this.activityBuilder.createActivity(activityId, "password")
		);
	}

}
