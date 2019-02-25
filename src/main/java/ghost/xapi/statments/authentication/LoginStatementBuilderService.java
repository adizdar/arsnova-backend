package ghost.xapi.statments.authentication;

import ghost.xapi.builder.ActivityBuilder;
import ghost.xapi.builder.VerbBuilder;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.Verb;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.services.ActorBuilderService;
import ghost.xapi.statments.AbstractStatmentBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class LoginStatementBuilderService extends AbstractStatmentBuilderService {

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
