package ghost.xapi.statements.audienceQuestions;

import de.thm.arsnova.entities.InterposedQuestion;
import de.thm.arsnova.entities.InterposedReadingCount;
import de.thm.arsnova.entities.Session;
import de.thm.arsnova.services.IQuestionService;
import de.thm.arsnova.services.ISessionService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class AudienceQuestionStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IQuestionService questionService;

	@Autowired
	private ISessionService sessionService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForAskedInterposedQuestion(HttpServletRequest request) {
		InterposedQuestion question = this.getInterposedQuestionOfUserViaSessionKey(request.getParameter("sessionkey"));
		Session session = this.sessionService.getSession(request.getParameter("sessionkey"));

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"audienceQuestion",
				question.get_id()
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "interposedQuestion");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"An audience question asked during the session " + session.getName()
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("asked"),
				activity,
				new Result("question", new Object[] {question})
		);
	}

	/**
	 * @param sessionKey
	 * @return InterposedQuestion
	 */
	private InterposedQuestion getInterposedQuestionOfUserViaSessionKey(String sessionKey) {
		// Retrieve the last question from the user aka the one that is posted, so we don;t need to cache the
		// input stream to read him twice.
		List<InterposedQuestion> interposedQuestions = this.questionService.getInterposedQuestions(
				sessionKey,
				0,
				1
		);

		if (interposedQuestions.isEmpty()) {
			throw new NullArgumentException("No question is found for the current user.");
		}

		return interposedQuestions.get(0);
	}

	/**
	 * @param sessionKey
	 * @return Statement
	 */
	private List<InterposedQuestion> getAllInterposedQuestionOfUserViaSessionKey(String sessionKey) {
		// Retrieve all question for session.
		List<InterposedQuestion> interposedQuestions = this.questionService.getInterposedQuestions(
				sessionKey,
				0,
				0
		);

		if (interposedQuestions.isEmpty()) {
			throw new NullArgumentException("No questions are found for the current session.");
		}

		return interposedQuestions;
	}

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForGetInterposedQuestion(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");

		// Retrieve all question for session.
		List<InterposedQuestion> interposedQuestions = this.getAllInterposedQuestionOfUserViaSessionKey(sessionKey);

		Session activeSession = this.sessionService.getSession(sessionKey);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"audienceQuestions/session",
				activeSession.getName()
		});
		Session session = this.sessionService.getSession(request.getParameter("sessionkey"));

		Result result = new Result("interposedQuestions", interposedQuestions.toArray());
		Activity activity = this.activityBuilder.createActivity(activityId, "interposedQuestions");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"All questions asked in the session " + session.getName()
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
				result
		);
	}

	/**
	 * @param pathVariables The question id stored in the path variables.
	 * @return Statement
	 */
	public Statement buildForGetOneInterposedQuestion(Map pathVariables) {
		String questionId = (String) pathVariables.get("questionId");
		InterposedQuestion question = this.questionService.readInterposedQuestion(questionId);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"audienceQuestion",
				questionId
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "interposedQuestion");
		activity.getDefinition().getDescription().addNoLanguageTranslation("Retrieve one question.");

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "interposedQuestion"),
				new Result("question", new InterposedQuestion[]{question})
		);
	}

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForDeleteInterposedQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"audienceQuestion",
				questionId
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "interposedQuestion");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Question was deleted from session"
		);

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("deleted"),
				activity
		);
	}

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForUnreadInterposedQuestionsCount(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");
		String username = request.getParameter("user");

		Session session = this.sessionService.getSession(request.getParameter("sessionkey"));

		InterposedReadingCount unredQuestionsCount = this.questionService.getInterposedReadingCount(sessionKey, username);
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"audienceQuestionsCount/session",
				session.getName(),
				"forUser",
				username
		});

		return new Statement(
				this.actorBuilder.getActor(),
				this.verbBuilder.createVerb("unread"),
				this.activityBuilder.createActivity(activityId, "unreadCount"),
				new Result("unreadCount", new InterposedReadingCount[] {unredQuestionsCount})
		);

	}

}
