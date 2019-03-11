package ghost.xapi.statements.motd;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.arsnova.entities.Motd;
import de.thm.arsnova.entities.MotdList;
import de.thm.arsnova.entities.transport.ImportExportSession;
import de.thm.arsnova.services.IMotdService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.actor.Actor;
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

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/session",
				sessionKey
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retreive"),
				this.activityBuilder.createActivity(activityId, "motd"),
				new Result(new Object[] { motds })
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

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"motd/create",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("create"),
				this.activityBuilder.createActivity(activityId, "motd"),
				new Result(new Object[] { motd })
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
				"motd/update",
				motdKey
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("update"),
				this.activityBuilder.createActivity(activityId, "motd"),
				new Result(new Object[] { this.motdService.getMotd(motdKey) })
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
				"motd/delete",
				motdKey
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("delete"),
				this.activityBuilder.createActivity(activityId, "motd")
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
				"userlist",
				username,
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "userList"),
				new Result(new Object[] { this.motdService.getMotdListForUser(username) })
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
				"create/userlist",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("create"),
				this.activityBuilder.createActivity(activityId, "userList"),
				new Result(new Object[] { motdList })
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
				"update/userlist",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("update"),
				this.activityBuilder.createActivity(activityId, "userList"),
				new Result(new Object[] { motdList })
		);
	}
}
