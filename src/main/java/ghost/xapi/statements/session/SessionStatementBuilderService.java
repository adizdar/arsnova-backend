package ghost.xapi.statements.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.thm.arsnova.entities.Session;
import de.thm.arsnova.entities.SessionInfo;
import de.thm.arsnova.entities.transport.ImportExportSession;
import de.thm.arsnova.services.ISessionService;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Mapping path: "/session"
 */
@Service
public class SessionStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private ISessionService sessionService;

	@Autowired
	private IUserService userService;

	/**
	 * @param request method POST
	 * path /
	 *
	 * @return
	 */
	public Statement buildForPostNewSession(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"on",
				this.getCurrentTimestamp()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("create"),
				this.activityBuilder.createActivity(activityId, "newSession")
		);
	}

	/**
	 * @param request method GET
	 * path /
	 *
	 * @return
	 */
	public Statement buildForGetSessions(HttpServletRequest request) {
		boolean ownedOnly = this.parseParameterToBool(request.getParameter("ownedOnly"));
		boolean visitedOnly = this.parseParameterToBool(request.getParameter("visitedOnly"));
		String sortBy = request.getParameter("sortby");
		String username = request.getParameter("username");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"on",
				this.getCurrentTimestamp()
		});

		List<Session> sessions = null;
		if (!username.isEmpty()) {
			if (ownedOnly && !visitedOnly) {
				sessions = this.sessionService.getUserSessions(username);
			} else if (visitedOnly && !ownedOnly) {
				sessions = this.sessionService.getUserVisitedSessions(username);
			}
		} else {
			if (ownedOnly && !visitedOnly) {
				sessions = this.sessionService.getMySessions(-1, -1);
			} else if (visitedOnly && !ownedOnly) {
				sessions = this.sessionService.getMyVisitedSessions(-1, -1);
			}
		}

		Map<String, String> sessionParameters = new HashMap<>();
		sessionParameters.put("ownedOnly", Boolean.toString(ownedOnly));
		sessionParameters.put("visitedOnly", Boolean.toString(visitedOnly));
		sessionParameters.put("sortBy", sortBy);
		sessionParameters.put("username", username);

		Map<String, Object> result = new HashMap<>();
		result.put("sessionParameters", sessionParameters);
		result.put("sessions", sessions != null ? sessions.toArray() : "No result for search criteria.");

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "sessions"),
				new Result(new Object[]{ result })
		);
	}

	/**
	 * @param request method GET
	 * path /
	 *
	 * @return
	 */
	public Statement buildForGetMySession(HttpServletRequest request) {
		boolean visitedOnly = this.parseParameterToBool(request.getParameter("visitedOnly"));
		String sortBy = request.getParameter("sortby");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"on",
				this.getCurrentTimestamp()
		});

		List<SessionInfo> sessions;
		if (!visitedOnly) {
			sessions = this.sessionService.getMySessionsInfo(-1, -1);
		} else {
			sessions = this.sessionService.getMyVisitedSessionsInfo(-1, -1);
		}

		Map<String, String> sessionParameters = new HashMap<>();
		sessionParameters.put("visitedOnly", Boolean.toString(visitedOnly));
		sessionParameters.put("sortBy", sortBy);

		Map<String, Object> result = new HashMap<>();
		result.put("sessionParameters", sessionParameters);
		result.put("sessions", sessions != null ? sessions.toArray() : "No result for search criteria.");

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "sessions"),
				new Result(new Object[]{ result })
		);
	}

	/**
	 * @param request
	 * @param pathVariables method DELETE
	 * path /{sessionkey}
	 *
	 * @return
	 */
	public Statement buildForDeleteSession(HttpServletRequest request, Map pathVariables) {
		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"delete/session",
				sessionKey
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("delete"),
				this.activityBuilder.createActivity(activityId, "session")
		);
	}

	/**
	 * @param request method PUT
	 * path /{sessionkey}
	 *
	 * @return
	 */
	public Statement buildForUpdateSession(HttpServletRequest request, Map pathVariables) {
		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"update/session",
				sessionKey
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("updated"),
				this.activityBuilder.createActivity(activityId, "session"),
				new Result("session", new Object[]{this.sessionService.getSession(sessionKey)})
		);
	}

	/**
	 * TODO Test
	 *
	 * @param request method GET
	 * path /publicpool
	 *
	 * @return
	 */
	public Statement buildForGetMyPublicPoolSessions(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"publicpool/created",
				this.getCurrentTimestamp()
		});
		// TODO BETTER IDEA FOR ID's
		List<SessionInfo> sessions = this.sessionService.getMyPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "sessions"),
				new Result(new Object[]{
						"sessions",
						sessions
				})
		);
	}

	/**
	 * @param request method POST
	 * path /import
	 *
	 * @return
	 */
	public Statement buildForImportSession(HttpServletRequest request) throws IOException {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"import/created",
				this.getCurrentTimestamp()
		});

		ObjectMapper mapper = new ObjectMapper();
		Map importSession = mapper.readValue(request.getInputStream(), Map.class);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("import"),
				this.activityBuilder.createActivity(activityId, "session"),
				new Result("importedSession", new Object[] {importSession})
		);
	}

	/**
	 * @param request method GET
	 * path /export
	 *
	 * @return
	 */
	public Statement buildForExportSession(HttpServletRequest request) {
		boolean withAnswerStatistics = this.parseParameterToBool(request.getParameter("withAnswerStatistics"));
		boolean withFeedbackQuestions = this.parseParameterToBool(request.getParameter("withFeedbackQuestions"));

		String sessionKeysAsString = request.getParameter("sessionkey");
		List<String> sessionKeys = Lists.newArrayList(Splitter.on(",").split(sessionKeysAsString));

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"export/sessions",
				sessionKeysAsString
		});

		List<ImportExportSession> sessions = new ArrayList<>();
		ImportExportSession temp;
		for (String key : sessionKeys) {
			temp = this.sessionService.exportSession(key, withAnswerStatistics, withFeedbackQuestions);
			if (temp != null) {
				sessions.add(temp);
			}
		}

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("export"),
				this.activityBuilder.createActivity(activityId, "sessions"),
				new Result("exportedSessions", new Object[] { sessions })
		);
	}

	/**
	 * @param request method PUT
	 * path /{sessionkey}/changecreator
	 *
	 * @return
	 */
	public Statement buildForUpdateSessionCreator(HttpServletRequest request, Map pathVariables) {
		String sessionKey = (String) pathVariables.get("sessionkey");
		Session session = this.sessionService.getSession(sessionKey);
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"changecreator/session",
				sessionKey,
				"newCreator",
				session.getCreator()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("updated"),
				this.activityBuilder.createActivity(activityId, "sessionCreator"),
				new Result("newSessionCreator", new Object[]{session.getCreator()})
		);
	}

	/**
	 * @param request
	 * @param pathVariables method POST
	 * path /{sessionkey}/copytopublicpool
	 *
	 * @return
	 */
	public Statement buildForCopyToPublicPool(HttpServletRequest request, Map pathVariables) {
		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"copytopublicpool/session",
				sessionKey
		});

		this.sessionService.getPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("copy"),
				this.activityBuilder.createActivity(activityId, "sessionToPublicPool"),
				new Result("session", new Object[] {this.sessionService.getPublicPoolSessionsInfo()})
		);
	}

	/**
	 * @param request
	 * @param pathVariables method POST
	 * path /{sessionkey}/lock
	 *
	 * @return
	 */
	public Statement buildForLockSession(HttpServletRequest request, Map pathVariables) {
		boolean isLocked = this.parseParameterToBool(request.getParameter("lock"));

		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lock/session",
				sessionKey
		});

		this.sessionService.getPublicPoolSessionsInfo();

		Map<String, Object> result = new HashMap<>();
		result.put("isSessionLocked", Boolean.toString(isLocked));
		result.put("session", this.sessionService.getSession(sessionKey));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("lock"),
				this.activityBuilder.createActivity(activityId, "session"),
				new Result(new Object[] {result})
		);
	}

	/**
	 * @param request
	 * @param pathVariables method GET
	 * path /{sessionkey}/learningprogress
	 *
	 * @return
	 */
	public Statement buildGetLearningProgress(HttpServletRequest request, Map pathVariables) {
		String sessionKey = (String) pathVariables.get("sessionkey");
		String progressType = request.getParameter("type");
		String questionVariant = request.getParameter("questionVariant");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"learningprogress/session",
				sessionKey
		});

		this.sessionService.getPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "learningProgress"),
				new Result("learningProgress", new Object[] {
		 			this.sessionService.getLearningProgress(sessionKey, progressType, questionVariant)
				})
		);
	}

	/**
	 * @param request
	 * @param pathVariables method GET
	 * path /{sessionkey}/features
	 *
	 * @return
	 */
	public Statement buildForGetSessionFeatures(HttpServletRequest request, Map pathVariables) {
		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"features/session",
				sessionKey
		});

		this.sessionService.getPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "sessionFeatures"),
				new Result("sessionFeatures", new Object[] {this.sessionService.getSessionFeatures(sessionKey)})
		);
	}

	/**
	 * @param request
	 * @param pathVariables method POST
	 * path /{sessionkey}/lockfeedbackinput
	 *
	 * @return
	 */
	public Statement buildForLockFeedbackInput(HttpServletRequest request, Map pathVariables) {
		boolean isLocked = this.parseParameterToBool(request.getParameter("lock"));

		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lockfeedbackinput/session",
				sessionKey
		});

//		this.sessionService.getPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("lock"),
				this.activityBuilder.createActivity(activityId, "feedbackInput"),
				new Result("isFeedbackInputLocked", new Object[] {Boolean.toString(isLocked)})
		);
	}

	/**
	 * @param request
	 * @param pathVariables method POST
	 * path /{sessionkey}/flipflashcards
	 *
	 * @return
	 */
	public Statement buildForFlipFlashcards(HttpServletRequest request, Map pathVariables) {
		boolean shouldFlip = this.parseParameterToBool(request.getParameter("flip"));

		String sessionKey = (String) pathVariables.get("sessionkey");
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"flipflashcards/session",
				sessionKey
		});

//		this.sessionService.getPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("flip"),
				this.activityBuilder.createActivity(activityId, "flashCards"),
				new Result("shouldReturnFlipFlashCards", new Object[] {
						this.sessionService.flipFlashcards(sessionKey, shouldFlip)
				})
		);
	}
}
