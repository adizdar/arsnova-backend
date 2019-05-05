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
import ghost.xapi.entities.activity.Activity;
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
	public Statement buildForPostNewSession(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Session session = mapper.readValue(request.getInputStream(), Session.class);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"session/name",
				session.getShortName().replaceAll(" ", "_")
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "session");
		activity.getDefinition().getName().addNoLanguageTranslation("New session");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"New session created"
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("create"),
				activity,
				new Result("session", new Object[] {session})
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
				"session/list",
				this.generateUUID()
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

		Activity activity = this.activityBuilder.createActivity(activityId, "sessions");
		activity.getDefinition().getName().addNoLanguageTranslation("Session list");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Get session list"
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
				new Result("sessions", new Object[]{ result })
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
				"sessions/list/user",
				this.userService.getCurrentUser().getUsername()
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

		Activity activity = this.activityBuilder.createActivity(activityId, "sessions");
		activity.getDefinition().getName().addNoLanguageTranslation("Session list");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"User session list"
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
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
				"session",
				sessionKey
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "session");
		activity.getDefinition().getName().addNoLanguageTranslation("Delete session");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("delete"),
				activity
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
				"session",
				sessionKey
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "session");
		activity.getDefinition().getName().addNoLanguageTranslation("Update session");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("updated"),
				activity,
				new Result("session", new Object[]{this.sessionService.getSession(sessionKey)})
		);
	}

	/**
	 * @param request method GET
	 * path /publicpool
	 *
	 * @return
	 */
	public Statement buildForGetMyPublicPoolSessions(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"publicPool/user",
				this.userService.getCurrentUser().getUsername()
		});

		List<SessionInfo> sessions = this.sessionService.getMyPublicPoolSessionsInfo();

		Activity activity = this.activityBuilder.createActivity(activityId, "sessions");
		activity.getDefinition().getName().addNoLanguageTranslation("Public session pool");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Retrieve session pool"
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
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
		ObjectMapper mapper = new ObjectMapper();
		Map importSession = mapper.readValue(request.getInputStream(), Map.class);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"import/session",
				!((String) importSession.get("keyword")).isEmpty() ? (String) importSession.get("keyword") : (String) importSession.get("name")
		});

		return new Statement(
				this.actorBuilder.getActor(),
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
				sessionKeysAsString.length() > 16 ? sessionKeysAsString.substring(0, 16) : sessionKeysAsString
		});

		List<ImportExportSession> sessions = new ArrayList<>();
		ImportExportSession temp;
		for (String key : sessionKeys) {
			temp = this.sessionService.exportSession(key, withAnswerStatistics, withFeedbackQuestions);
			if (temp != null) {
				sessions.add(temp);
			}
		}

		Activity activity = this.activityBuilder.createActivity(activityId, "sessions");
		activity.getDefinition().getName().addNoLanguageTranslation("Export sessions");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("export"),
				activity,
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

		Activity activity = this.activityBuilder.createActivity(activityId, "sessionCreator");
		activity.getDefinition().getName().addNoLanguageTranslation("Session");
		activity.getDefinition().getDescription().addNoLanguageTranslation("Update session creator");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("updated"),
				activity,
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
				"publicPool/session",
				sessionKey
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "sessionToPublicPool");
		activity.getDefinition().getDescription().addNoLanguageTranslation("Copy session to public pool");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("copy"),
				activity,
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
				this.actorBuilder.getActor(),
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

		Activity activity = this.activityBuilder.createActivity(activityId, "learningProgress");
		activity.getDefinition().getDescription().addNoLanguageTranslation("Learning progress retrieved");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
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
				"session",
				sessionKey
		});

		this.sessionService.getPublicPoolSessionsInfo();

		return new Statement(
				this.actorBuilder.getActor(),
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

		return new Statement(
				this.actorBuilder.getActor(),
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

		Activity activity = this.activityBuilder.createActivity(activityId, "flashCards");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Flash cards retrieved for session " + this.sessionService.getSession(sessionKey).getName()
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("flip"),
				activity,
				new Result("shouldReturnFlipFlashCards", new Object[] {
						this.sessionService.flipFlashcards(sessionKey, shouldFlip)
				})
		);
	}
}
