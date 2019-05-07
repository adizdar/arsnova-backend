package ghost.xapi.builder;

import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.log.XAPILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActorBuilder {

	@Autowired
	private IUserService userService;

	@Value(value = "${root-url}")
	private String rootUrl;

	/**
	 * @return Actor
	 */
	public Actor getActor() {
		User currentUser = this.userService.getCurrentUser();
		if (currentUser == null) {
			XAPILogger.ERROR.error("Current user is null in ActorBuilder.");

			throw new NullPointerException("Current user is null.");
		}

		return new Actor(
				currentUser.getUsername(),
				this.rootUrl
		);
	}
}

