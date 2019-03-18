package ghost.xapi.statements.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.arsnova.entities.Statistics;
import de.thm.arsnova.services.IStatisticsService;
import de.thm.arsnova.services.IUserService;
import de.thm.arsnova.services.UserSessionService;
import de.thm.arsnova.socket.ARSnovaSocket;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
	 * Mapping path: "/socket" TODO throw out
 */
@Service
public class SocketStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IUserService userService;

	@Autowired
	private UserSessionService userSessionService;

	@Autowired
	private ARSnovaSocket server;

	/**
	 * @param request method POST
	 * path /assign
	 *
	 * @return
	 */
	public Statement buildForAuthorizeSocketAssign(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map sessionMap = mapper.readValue(request.getInputStream(), Map.class);

		String socketId = (String) sessionMap.get("session");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"/socket/assign",
				socketId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("assign"),
				this.activityBuilder.createActivity(activityId, "websocket"),
				new Result(new Object[] { sessionMap })
		);
	}

	/**
	 * @param request method GET
	 * path /url
	 *
	 * @return
	 */
	public Statement buildForGetSocketUrl(HttpServletRequest request) {
		String socketUrl = request.getServerName() + ":" + this.server.getPortNumber();
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"socket/url",
				request.getServerName() + ":" + this.server.getPortNumber()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("get"),
				this.activityBuilder.createActivity(activityId, "socketUrl"),
				new Result(new Object[] {
						(server.isUseSSL() ? "https://" : "http://") + socketUrl
				})
		);
	}
}
