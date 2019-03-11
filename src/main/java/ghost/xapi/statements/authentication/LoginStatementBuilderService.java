package ghost.xapi.statements.authentication;

import ghost.xapi.entities.Statement;
import ghost.xapi.entities.Verb;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class LoginStatementBuilderService extends AbstractStatementBuilderService {

	/**
	 * @param request
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
	 * @param request
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

}
