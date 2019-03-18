package ghost.xapi.statements.authentication;

import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.Verb;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Request mapping /auth
 */
@Service
public class AuthenticationStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IUserService userService;

	/**
	 * @param request method POST
	 * path /auth/login, /doLogin
	 *
	 * @return Statement
	 */
	public Statement buildForLoginAction(HttpServletRequest request) {
		Actor actor = this.actorBuilderService.getActor();
		Verb verb = this.verbBuilder.createVerb("loggedin");
		Activity activity = this.activityBuilder.createActivity(actor.getObjectType(), "application");

		return new Statement(actor, verb, activity);
	}

	/**
	 * @param request method POST
	 * path /auth/logout, /logout
	 *
	 * @return Statement
	 */
	public Statement buildForLogoutAction(HttpServletRequest request) {
		Actor actor = this.actorBuilderService.getActor();
		Verb verb = this.verbBuilder.createVerb("loggout");
		// TODO fix id
		Activity activity = this.activityBuilder.createActivity(actor.getObjectType(), "application");

		return new Statement(actor, verb, activity);
	}

	/**
	 * @param request
	 * path /auth/dialog
	 *
	 * @return Statement
	 */
	public Statement buildForGetAuthDialog(HttpServletRequest request) {
		String type = request.getParameter("type");
		String successUrl = request.getParameter("successurl");
		String failureUrl = request.getParameter("failureurl");
		String uuid = this.generateUUID();

		Map<String, String> authDialogParameters = new HashMap<>();
		authDialogParameters.put("Account type", type);
		authDialogParameters.put("Success url redirection", successUrl);
		authDialogParameters.put("Failure url redirection", failureUrl);

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"/to/auth/dialog",
				type,
				uuid
		});

		return new Statement(
				new Actor("TEMP_USER#" + uuid, "NOT_LOGGED_IN"),
				this.verbBuilder.createVerb("redirect"),
				this.activityBuilder.createActivity(activityId ,"authenticationDialog"),
				new Result(new Object[] { authDialogParameters })
		);
	}

	/**
	 * @param request method GET
	 * path /auth/, /whoami
	 *
	 * @return Statement
	 */
	public Statement buildForGetUserInformation(HttpServletRequest request) {
		User currentUser = this.userService.getCurrentUser();;

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"whoami",
				currentUser.getUsername()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId ,"currentUserInformation"),
				new Result(new Object[] { currentUser })
		);
	}

}
