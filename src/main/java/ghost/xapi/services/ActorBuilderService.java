package ghost.xapi.services;

import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.log.XAPILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			XAPILogger.ERROR.error("Current user is null in ActorBuilderService.");

			throw new NullPointerException("Current user is null.");
		}

		return new Actor(currentUser.getUsername(), currentUser.getType(), currentUser.getRole().name());
	}
}

