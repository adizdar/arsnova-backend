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
		User currentUser = this.userService.getCurrentUser();
		if (currentUser == null) {
			XAPILogger.ERROR.error("Current user is null.");
			throw new NullPointerException("Current user is null.");
		}

		return new Actor(currentUser.getUsername(), currentUser.getType());
	}
}
