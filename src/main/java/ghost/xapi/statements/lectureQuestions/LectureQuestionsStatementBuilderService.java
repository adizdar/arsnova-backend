package ghost.xapi.statements.lectureQuestions;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.arsnova.entities.Answer;
import de.thm.arsnova.entities.Question;
import de.thm.arsnova.entities.Session;
import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IQuestionService;
import de.thm.arsnova.services.ISessionService;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LectureQuestionsStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IQuestionService questionService;

	@Autowired
	private ISessionService sessionService;

	@Autowired
	private IUserService userService;


	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForGetQuestionViaId(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		String questionId = (String) pathVariables.get("questionId");
		Question question = this.questionService.getQuestion(questionId);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "lectureQuestion");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Retrieve lecturer question"
		);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("get"),
				activity,
				new Result("question", new Object[]{question})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForGetQuestions(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");
		boolean lectureQuestionsOnly = Boolean.parseBoolean(request.getParameter("lecturequestionsonly"));
		boolean flashcardsOnly = Boolean.parseBoolean(request.getParameter("flashcardsonly"));
		boolean preparationQuestionsOnly = Boolean.parseBoolean(request.getParameter("preparationquestionsonly"));
		boolean requestImageData = Boolean.parseBoolean(request.getParameter("requestImageData"));

		// Retrieve all question for session
		List<Question> questions;
		if (lectureQuestionsOnly) {
			questions = this.questionService.getLectureQuestions(sessionKey);
		} else if (flashcardsOnly) {
			questions = this.questionService.getFlashcards(sessionKey);
		} else if (preparationQuestionsOnly) {
			questions = this.questionService.getPreparationQuestions(sessionKey);
		} else {
			questions = this.questionService.getSkillQuestions(sessionKey);
		}

		if ((questions != null || !questions.isEmpty()) && requestImageData) {
			questions = questionService.replaceImageData(questions);
		}

		Session activeSession = this.sessionService.getSession(sessionKey);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestions/session",
				activeSession.getName()
		});
		Result result = new Result("questions", questions.toArray());
		Activity activity = this.activityBuilder.createActivity(activityId, "lectureQuestions");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Retrieve all questions for session " + activeSession.getName()
		);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				activity,
				result
		);
	}


	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForAskedQuestion(HttpServletRequest request) {
		User user = this.userService.getCurrentUser();
		String sessionKeyword = this.userService.getSessionForUser(user.getUsername());

		List<String> unansweredQuestions = this.questionService.getUnAnsweredLectureQuestionIds(sessionKeyword);
		String lastQuestionId = unansweredQuestions.get(unansweredQuestions.size() - 1);
		Question question = this.questionService.getQuestion(lastQuestionId);
		if (question == null) {
			throw new NullArgumentException("Asked lecturer question was not retrieved.");
		}

		Activity activity = this.activityBuilder.createActivity(question.get_id(), "lectureQuestion");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"User " + user.getUsername() + " asked a question."
		);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				question.get_id()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("asked"),
				this.activityBuilder.createActivity(activityId, "lectureQuestion"),
				new Result("question", new Object[]{question})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 * @throws IOException
	 */
	public Statement buildForBulkAskedQuestions(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<Question> questions = mapper.readValue(request.getInputStream(), List.class);

		String sessionName = this.getSessionNameForCurrentUser(this.sessionService, this.userService);
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"bulk/lectureQuestions" + (sessionName != null ? "session" : ""),
				(sessionName != null ? sessionName : this.generateUUID())
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("asked"),
				this.activityBuilder.createActivity(activityId, "multipleLectureQuestions"),
				new Result("questions", new Object[]{questions})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForQuestionImage(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		boolean fcImage = Boolean.parseBoolean(request.getParameter("fcImage"));

		String questionImage = "";
		if (fcImage) {
			questionImage = this.questionService.getQuestionFcImage(questionId);
		} else {
			questionImage = this.questionService.getQuestionImage(questionId);
		}

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"lectureQuestion/image",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "questionImage"),
				new Result("questionImage", new Object[]{questionImage})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForNewPiRound(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"piRound/lectureQuestion",
				questionId
		});

		Statement statement = new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("started"),
				this.activityBuilder.createActivity(activityId, "newPiRoundForQuestion")
		);

		String timeForNewPiRound = request.getParameter("time");
		if (!timeForNewPiRound.isEmpty()) {
			statement.setResult(new Result("timeForNewPiRound", new Object[]{timeForNewPiRound}));
		}

		return statement;
	}

	/**
	 * The admin accounts in the config needs to match the login user or it needs to have permissions.
	 *
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForCancelPiRound(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"piRound/lectureQuestion",
				questionId
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "piRoundForQuestion");
		String activityDescription = "Pi round canceled for question";

		Question question = this.questionService.getQuestion(questionId);
		if (question != null) {
			activityDescription += " subject " + question.getSubject();
		}

		activity.getDefinition().getDescription().addNoLanguageTranslation(activityDescription);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("canceled"),
				activity
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForResetPiRound(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"piRound/lectureQuestion",
				questionId
		});

		Activity activity = this.activityBuilder.createActivity(activityId, "piRoundForQuestion");
		String activityDescription = "Pi round reset for question";

		Question question = this.questionService.getQuestion(questionId);
		if (question != null) {
			activityDescription += " subject " + question.getSubject();
		}

		activity.getDefinition().getDescription().addNoLanguageTranslation(activityDescription);

		Statement statement = new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("reset"),
				activity
		);

		if (question != null) {
			statement.setResult(new Result("newPiRound", new Object[] { question.getPiRound() }));
		}

		return statement;
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForDisableVote(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		boolean disableVote = Boolean.parseBoolean(request.getParameter("disableVote"));

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"voting/lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("disable"),
				this.activityBuilder.createActivity(activityId, "votingForQuestion"),
				new Result("isVotingDisabled", new Object[]{disableVote})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForDisableVoteForAllQuestions(HttpServletRequest request) {
		boolean disableVote = Boolean.parseBoolean(request.getParameter("disableVote"));
		boolean lectureQuestionsOnly = Boolean.parseBoolean(request.getParameter("lecturequestionsonly"));
		boolean preparationQuestionsOnly = Boolean.parseBoolean(request.getParameter("preparationquestionsonly"));

		Map<String, Boolean> questionTypesToDisable = new HashMap<>();
		questionTypesToDisable.put("Disable voting for lecture questions", lectureQuestionsOnly);
		questionTypesToDisable.put("Disable voting for preparation questions", preparationQuestionsOnly);
		if (!lectureQuestionsOnly && !preparationQuestionsOnly) {
			questionTypesToDisable.put("Disable all", disableVote);
		}

		String activityId = this.getActivityIdViaSessionOrUUUIDForCurrentUser(this.sessionService, this.userService);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("disable"),
				this.activityBuilder.createActivity(activityId, "votingForAllQuestions"),
				new Result(new Object[]{questionTypesToDisable})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForPublishQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");
		Question question = this.questionService.getQuestion(questionId);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("publish"),
				this.activityBuilder.createActivity(activityId, "questionViaID"),
				new Result("question", new Object[] {question})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForPublishAllQuestions(HttpServletRequest request) {
		boolean publish = Boolean.parseBoolean(request.getParameter("publish"));
		boolean lectureQuestionsOnly = Boolean.parseBoolean(request.getParameter("lecturequestionsonly"));
		boolean preparationQuestionsOnly = Boolean.parseBoolean(request.getParameter("preparationquestionsonly"));

		Map<String, Boolean> questionTypesToPublish = new HashMap<>();
		questionTypesToPublish.put("Publish for lecture questions", lectureQuestionsOnly);
		questionTypesToPublish.put("Publish for for preparation questions", preparationQuestionsOnly);
		if (!lectureQuestionsOnly && !preparationQuestionsOnly) {
			questionTypesToPublish.put("Publish all", publish);
		}

		String activityId = this.getActivityIdViaSessionOrUUUIDForCurrentUser(this.sessionService, this.userService);

		Activity activity = this.activityBuilder.createActivity(activityId, "allQuestions");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Lecturer questions published on " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
		);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("publish"),
				activity,
				new Result("questionTypesToPublish", new Object[]{questionTypesToPublish})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForPublishStatistics(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		boolean showStatistics = Boolean.parseBoolean(request.getParameter("showStatistics"));

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"statistics/lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("show"),
				this.activityBuilder.createActivity(activityId, "statisticsForQuestion"),
				new Result("showStatistics", new Object[]{showStatistics})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForCorrectAnswer(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		boolean showCorrectAnswer = Boolean.parseBoolean(request.getParameter("showCorrectAnswer"));

		Question question = this.questionService.getQuestion(questionId);
		String correctAnswer = question.getCorrectAnswer();

		Map<String, String> result = new HashMap<>();
		result.put("Show correct answer", String.valueOf(showCorrectAnswer));
		result.put("Correct answer", correctAnswer);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"correctAnswer/lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("publish"),
				this.activityBuilder.createActivity(activityId, "correctAnswerForQuestion"),
				new Result("answer", new Object[]{result})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForDeleteSkillQuestions(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");
		boolean lectureQuestionsOnly = Boolean.parseBoolean(request.getParameter("lecturequestionsonly"));
		boolean flashcardsOnly = Boolean.parseBoolean(request.getParameter("flashcardsonly"));
		boolean preparationQuestionsOnly = Boolean.parseBoolean(request.getParameter("preparationquestionsonly"));

		Map<String, Boolean> questionTypesToDelete = new HashMap<>();
		questionTypesToDelete.put("Delete lecture questions", lectureQuestionsOnly);
		questionTypesToDelete.put("Delete preparation questions", preparationQuestionsOnly);
		questionTypesToDelete.put("Delete flash cards", flashcardsOnly);

		if (!lectureQuestionsOnly && !flashcardsOnly && !preparationQuestionsOnly) {
			questionTypesToDelete.put("Delete all", true);
		}

		Session activeSession = this.sessionService.getSession(sessionKey);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestionTypes/session",
				activeSession.getName()
		});
		Activity activity = this.activityBuilder.createActivity(activityId, "skillQuestions");
		activity.getDefinition().getDescription().addNoLanguageTranslation(
				"Skill questions deleted on " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
		);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("delete"),
				this.activityBuilder.createActivity(activityId, "skillQuestions"),
				new Result("questionTypesToDelete", new Object[]{questionTypesToDelete})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetAnswersForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		boolean allAnswers = Boolean.parseBoolean(request.getParameter("all"));

		List<Answer> answers;
		if (allAnswers || request.getParameter("piround") == null) {
			answers = this.questionService.getAllAnswers(questionId, -1, -1);
		} else {
			Integer piRound = Integer.parseInt(request.getParameter("piround"));
			answers = questionService.getAnswers(questionId, piRound, -1, -1);
		}

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"answers/lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "answersForQuestion"),
				new Result("answers", new Object[]{answers})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForSaveAnswerForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");
		List<Answer> savedAnswer = this.questionService.getAnswers(questionId, 0, 1);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("save"),
				this.activityBuilder.createActivity(activityId, "answerForQuestion"),
				new Result("answer", new Object[]{savedAnswer})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForUpdateAnswerForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");
		List<Answer> savedAnswer = this.questionService.getAnswers(questionId, 0, 1);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("updated"),
				this.activityBuilder.createActivity(activityId, "answerForQuestion"),
				new Result("updatedAnswer", new Object[]{savedAnswer})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForDeleteAnswerForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				this.activityBuilder.createActivity(activityId, "answerForQuestion"),
				new Result(new Object[] { this.questionService.getQuestion(questionId) })
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForDeleteAllAnswersForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				this.activityBuilder.createActivity(activityId, "allAnswersForQuestion"),
				new Result(new Object[]{this.questionService.getQuestion(questionId)})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForDeleteQuestionsAllAnswersForSession(HttpServletRequest request) {
		String sessionKey = request.getParameter("sessionkey");
		boolean lectureQuestionsOnly = Boolean.parseBoolean(request.getParameter("lecturequestionsonly"));
		boolean preparationQuestionsOnly = Boolean.parseBoolean(request.getParameter("preparationquestionsonly"));

		Map<String, Boolean> questionTypesToDelete = new HashMap<>();
		questionTypesToDelete.put("Delete only lecture questions and answers", lectureQuestionsOnly);
		questionTypesToDelete.put("Delete only preparation questions and answers", preparationQuestionsOnly);

		if (!lectureQuestionsOnly && !preparationQuestionsOnly) {
			questionTypesToDelete.put("Delete all questions and answers from session", true);
		}

		Session activeSession = this.sessionService.getSession(sessionKey);

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"deleteAllQuestionsAndAnswers/session",
				activeSession.getName()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				this.activityBuilder.createActivity(activityId, "allAnswersAndQuestionForSession"),
				new Result("questionTypesToDelete", new Object[]{questionTypesToDelete})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetAllRoundAnswersCountForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "totalAnswersForQuestion"),
				new Result(new Object[]{
						Arrays.asList(
								this.questionService.getAnswerCount(questionId, 1),
								this.questionService.getAnswerCount(questionId, 2)
						)})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetTotalAmountOfAnswersForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "totalNumberOfAnswersForQuestion"),
				new Result("total", new Object[]{this.questionService.getTotalAnswerCountByQuestion(questionId)})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetTotalAmountOfAnswersAndAbstiantForQuestion(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "totalNumberOfAnswersAndAbstentionAnswersForQuestion"),
				new Result("total", new Object[]{
						Arrays.asList(
								this.questionService.getAnswerCount(questionId),
								this.questionService.getAbstentionAnswerCount(questionId)
						)})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForGetFreeTextAnswers(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"lectureQuestion",
				questionId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "freeTextAnswersForQuestion"),
				new Result("answers", new Object[]{this.questionService.getFreetextAnswers(questionId, -1, -1)})
		);
	}

	/**
	 * @param request method GET
	 *
	 * path /{questionId}/answer/{answerId}/image
	 *
	 * @return Statement
	 */
	public Statement buildForGetImage(HttpServletRequest request) {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");
		String answerId = (String) pathVariables.get("answerId");

		String activityId = this.activityBuilder.createActivityId(new String[] {
				"imageFor/lectureQuestion",
				questionId,
				"answer",
				answerId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "image"),
				new Result("image", new Object[]{this.questionService.getImage(questionId, answerId)})
		);
	}
}
