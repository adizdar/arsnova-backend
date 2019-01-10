package ghost.xapi.services;

import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.actor.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Service
public class ActorBuilderService {

	@Autowired
	private IUserService userService;

	/**
	 * @return Actor
	 */
	public Actor getActor() {
		Actor actor = this.getActorFromSession();
		if (actor != null) {
			return actor;
		}

		return this.createActorViaUserService();
	}

	/**
	 * @return Actor
	 */
	private Actor getActorFromSession() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession();
		Actor actor = (Actor) session.getAttribute(Actor.class.toString());

		return actor != null ? actor : null;
	}

	/**
	 * @return Actor
	 */
	public Actor createActorViaUserService() {
		User currentUser = this.userService.getCurrentUser();

		return new Actor(currentUser.getUsername(), currentUser.getType());
	}
}
