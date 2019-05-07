package ghost.xapi.statements;

import de.thm.arsnova.dao.IDatabaseDao;
import de.thm.arsnova.entities.Session;
import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.ISessionService;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.builder.ActivityBuilder;
import ghost.xapi.builder.ActorBuilder;
import ghost.xapi.builder.VerbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AbstractStatementBuilderService {

	@Autowired
	protected ActorBuilder actorBuilder;

	@Autowired
	protected ActivityBuilder activityBuilder;

	@Autowired
	protected VerbBuilder verbBuilder;

	@Autowired
	private IDatabaseDao databaseDao;

	/**
	 * @param parameter
	 *
	 * @return boolean
	 */
	protected boolean parseParameterToBool(String parameter) {
		return Boolean.parseBoolean(parameter);
	}

	/**
	 * @return String
	 */
	protected String generateUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * @return String
	 */
	protected String getCurrentTimestamp() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * @param sessionService
	 * @param userService
	 * @return String
	 */
	protected String getActivityIdViaSessionOrUUUIDForCurrentUser(ISessionService sessionService, IUserService userService) {
		User user = userService.getCurrentUser();
		String sessionKey = userService.getSessionForUser(user.getUsername());
		Session session = sessionService.getSession(sessionKey);

		return (session == null)
				? this.activityBuilder.createActivityId(new String[] {
				this.generateUUID()
		})
				: this.activityBuilder.createActivityId(new String[] {
				"session",
				session.getName()
		});
	}

	/**
	 * @param sessionService
	 * @param userService
	 * @return String
	 */
	protected String getSessionNameForCurrentUser(ISessionService sessionService, IUserService userService) {
		User user = userService.getCurrentUser();
		String sessionKey = userService.getSessionForUser(user.getUsername());
		Session session = sessionService.getSession(sessionKey);

		return session != null ? session.getName() : null;
	}

	/**
	 * To overcome the anonymity of Arsnova for the creator.
	 *
	 * @param sessionKey
	 * @return
	 */
	protected String getCreatorFromSessionDao(String sessionKey) {
		Session session = this.databaseDao.getSessionFromKeyword(sessionKey);

		return (session != null) ? session.getCreator() : null;
	}

}
