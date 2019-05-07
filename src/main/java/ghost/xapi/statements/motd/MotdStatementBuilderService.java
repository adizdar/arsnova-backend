package ghost.xapi.statements.motd;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.arsnova.entities.Motd;
import de.thm.arsnova.entities.Session;
import de.thm.arsnova.services.IMotdService;
import de.thm.arsnova.services.ISessionService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Mapping path: "/motd"
 * Massage of the day
 */
@Service
public class MotdStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IMotdService motdService;

	@Autowired
	private ISessionService sessionService;

	/**
	 * @param request method GET
	 * path /
	 *
	 * @return
	 */
	public Statement buildForGetMotd(HttpServletRequest request) {
		String clientDate = request.getParameter("clientdate");
		String audience = request.getParameter("audience");
		String sessionKey = request.getParameter("sessionkey") != null ? request.getParameter("sessionkey") : "";
		boolean adminView = this.parseParameterToBool(request.getParameter("adminview"));

		List<Motd> motds;
		Date client = new Date(System.currentTimeMillis());
		if (clientDate != null && !clientDate.isEmpty()) {
			client.setTime(Long.parseLong(clientDate));
		}
		if (adminView) {
			if ("null".equals(sessionKey)) {
				motds = this.motdService.getAdminMotds();
			} else {
				motds = this.motdService.getAllSessionMotds(sessionKey);
			}
		} else {
			motds = this.motdService.getCurrentMotds(client, audience, sessionKey);
		}

		Session session = this.sessionService.getSession(sessionKey);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/session",
				sessionKey
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "motd");
		activity.getDefinition().getDescription().addDefaultLanguageKey(
				"Messages of the day for session " + session.getName()
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retreive"),
				activity,
				new Result("motd", new Object[] { motds })
		);
	}

	/**
	 * @param request method POST
	 * path /
	 *
	 * @return
	 */
	public Statement buildForPostNewtMotd(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map motd = mapper.readValue(request.getInputStream(), Map.class);

		Session session = this.sessionService.getSession((String) motd.get("sessionkey"));

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/session",
				(String) motd.get("sessionkey")
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "createMotd");
		activity.getDefinition().getDescription().addDefaultLanguageKey(
				"Created message of the day for session " + session.getName()
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("create"),
				activity,
				new Result("motd", new Object[] { motd })
		);
	}

	/**
	 * @param request method PUT
	 * path /{motdkey}
	 *
	 * @return
	 */
	public Statement buildForUpdateMotd(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String motdKey = (String) pathVariables.get("motdkey");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motdKey",
				motdKey
		});

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("update"),
				this.activityBuilder.createActivity(activityId, "updateMotd"),
				new Result("motd", new Object[] { this.motdService.getMotd(motdKey)})
		);
	}

	/**
	 * @param request method DELETE
	 * path /{motdkey}
	 *
	 * @return
	 */
	public Statement buildForDeleteMotd(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String motdKey = (String) pathVariables.get("motdkey");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motdKey",
				motdKey
		});

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("delete"),
				this.activityBuilder.createActivity(activityId, "deleteMotd")
		);
	}

	/**
	 * @param request method GET
	 * path /userlist
	 *
	 * @return
	 */
	public Statement buildForGetUserMotdList(HttpServletRequest request) {
		String username = request.getParameter("username");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/list/user",
				username
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "motd");
		activity.getDefinition().getDescription().addDefaultLanguageKey(
				"All messages of the day for user " + username
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
				new Result("motds", new Object[] { this.motdService.getMotdListForUser(username) })
		);
	}

	/**
	 * @param request method POST
	 * path /userlist
	 *
	 * @return
	 */
	public Statement buildForPostUserMotdList(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map motdList = mapper.readValue(request.getInputStream(), Map.class);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/list/user",
				(String) motdList.get("username")
		});

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("create"),
				this.activityBuilder.createActivity(activityId, "newMotdList"),
				new Result("motdList", new Object[] { motdList })
		);
	}

	/**
	 * @param request method PUT
	 * path /userlist
	 *
	 * @return
	 */
	public Statement buildForUpdateUserMotdList(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map motdList = mapper.readValue(request.getInputStream(), Map.class);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/list/user",
				(String) motdList.get("username")
		});

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("update"),
				this.activityBuilder.createActivity(activityId, "updateMotdList"),
				new Result("motdList", new Object[] { motdList })
		);
	}
}
