package ghost.xapi.services;

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
	private ActorBuilderService actorBuilderService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForLoginAction(HttpServletRequest request) {
		Actor actor = this.actorBuilderService.getActor();
		Verb verb = new Verb("loggedin");
		Activity activity = new Activity(actor.getObjectType(), "application");

		return new Statement(actor, verb, activity);
	}

}
