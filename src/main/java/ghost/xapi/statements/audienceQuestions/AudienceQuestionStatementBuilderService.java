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

		Session activeSession = this.sessionService.getSession(request.getParameter("sessionkey"));

		Activity activity = this.activityBuilder.createActivity(question.getType(), "interposedQuestion");
		activity.getDefinition().getName().addTranslation("de", question.getSubject());
		activity.getDefinition().getDescription().addTranslation("de", question.getText());

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("asked"),
				activity
		);
	}

	/**
	 * @param sessionKey
	 * @return InterposedQuestion
	 */
	private InterposedQuestion getInterposedQuestionOfUserViaSessionKey(String sessionKey) {
		// Retrieve the last question from the user aka the one that is posted.
		// The reason for this is, we can't read the input stream anymore.
		// TODO if it is not working than use https://www.baeldung.com/spring-http-logging for prehanlde
		// TODO maybe a service to retrieve the last question
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
	 * @param request
	 * @return Statement
	 */
	public Statement buildForGetInterposedQuestion(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");

		// Retrieve all question for session.
		List<InterposedQuestion> interposedQuestions = this.questionService.getInterposedQuestions(
				sessionKey,
				0,
				0
		);

		Session activeSession = this.sessionService.getSession(sessionKey);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"interposedQuestions",
				sessionKey,
				activeSession.getCourseType(),
				Long.toString(activeSession.getCreationTime())
		});
		Result result = new Result(interposedQuestions.toArray());
		Activity activity = this.activityBuilder.createActivity(activityId, "interposedQuestions");

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
				result
		);
	}

	/**
	 * @param pathVariables The question id stored in the path variables.
	 * @return
	 */
	public Statement buildForGetOneInterposedQuestion(Map pathVariables) {
		String questionId = (String) pathVariables.get("questionId");
		InterposedQuestion question = this.questionService.readInterposedQuestion(questionId);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("seen"),
				this.activityBuilder.createActivity("question#"+questionId, "interposedQuestion"),
				new Result(new InterposedQuestion[]{question})
		);
	}

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForDeleteInterposedQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		String questionId = (String) pathVariables.get("questionId");
		String question = "question#" + questionId;

		Result result = new Result(new String[]{question});
		Activity activity = this.activityBuilder.createActivity(questionId, "interposedQuestion");

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				activity,
				result
		);
	}

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement buildForUnreadInterposedQuestionsCount(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");
		String username = request.getParameter("user");

		Session activeSession = this.sessionService.getSession(sessionKey);

		InterposedReadingCount unredQuestionsCount = this.questionService.getInterposedReadingCount(sessionKey, username);
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"interposedQuestionsCount",
				sessionKey,
				username,
				Long.toString(activeSession.getCreationTime())
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("unread"),
				this.activityBuilder.createActivity(activityId, "unreadCount"),
				new Result(new InterposedReadingCount[] {unredQuestionsCount})
		);

	}

}
