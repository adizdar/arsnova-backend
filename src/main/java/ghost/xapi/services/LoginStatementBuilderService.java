package ghost.xapi.services;

import ghost.xapi.builder.ActivityBuilder;
import ghost.xapi.builder.VerbBuilder;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.Verb;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class LoginStatementBuilderService {

	@Autowired
	private ActivityBuilder activityBuilder;

	@Autowired
	private VerbBuilder verbBuilder;

	@Autowired
	private ActorBuilderService actorBuilderService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForLoginAction(HttpServletRequest request) {
		try {
			Actor actor = this.actorBuilderService.getActor();
			Verb verb = this.verbBuilder.createVerb("loggedin");
			Activity activity = this.activityBuilder.createActivity(actor.getObjectType(), "application");

			return new Statement(actor, verb, activity);
		} catch (Exception e) {
			// TODO Log to file and add error to statment request for loggin at TLA, don't break user functionality.
			return new Statement(null, null, null);
		}
	}

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForLogoutAction(HttpServletRequest request) {
		try {
			Actor actor = this.actorBuilderService.getActor();
			Verb verb = this.verbBuilder.createVerb("loggout");
			Activity activity = this.activityBuilder.createActivity(actor.getObjectType(), "application");

			return new Statement(actor, verb, activity);

		} catch (Exception e) {
			// TODO move to parent call ...
			// TODO Log to file and add error to statment request for loggin at TLA, don't break user functionality.
			return new Statement(null, null, null);
		}

	}

}
