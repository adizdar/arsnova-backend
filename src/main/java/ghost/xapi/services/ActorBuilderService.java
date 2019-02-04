package ghost.xapi.services;

import de.thm.arsnova.controller.SocketController;
import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.log.XAPILogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
//		Actor actor = this.getActorFromSession();

//		return actor != null ? actor : this.createActorViaUserService();
		return this.createActorViaUserService();
	}

	/**
	 * @return Actor
	 */
	private Actor getActorFromSession() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession();
		Actor actor = (Actor) session.getAttribute(Actor.class.getName());

		return actor;
	}

	/**
	 * @return Actor
	 */
	public Actor createActorViaUserService() {
		User currentUser = this.userService.getCurrentUser();
		if (currentUser == null) {
			XAPILogger.ERROR.error("Current user is null.");
			throw new NullPointerException("Current user is null.");
		}

		return new Actor(currentUser.getUsername(), currentUser.getType());
	}
}
