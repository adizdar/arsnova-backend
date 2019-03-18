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
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
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
		String activityId = "question#" + questionId;
		Question question = this.questionService.getQuestion(questionId);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("asked"),
				this.activityBuilder.createActivity(activityId, "lectureQuestion"),
				new Result(new Object[]{question})
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
				"lectureQuestions",
				sessionKey,
				activeSession.getCourseType(),
				Long.toString(activeSession.getCreationTime())
		});
		Result result = new Result(questions.toArray());
		Activity activity = this.activityBuilder.createActivity(activityId, "lectureQuestions");

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
	 * @throws IOException
	 */
	public Statement buildForAskedQuestion(HttpServletRequest request) throws IOException {
		User user = this.userService.getCurrentUser();
		String sessionKeyword = this.userService.getSessionForUser(user.getUsername());
		List<String> unansweredQuestions = this.questionService.getUnAnsweredLectureQuestionIds(sessionKeyword);
		String lastQuestionId = unansweredQuestions.get(unansweredQuestions.size() - 1);
		Question question = this.questionService.getQuestion(lastQuestionId);
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"question",
				question.get_id(),
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("asked"),
				this.activityBuilder.createActivity(activityId, "lectureQuestion"),
				new Result(new Object[]{question})
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

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"questions",
				String.valueOf(System.currentTimeMillis())
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("asked"),
				this.activityBuilder.createActivity(activityId, "multipleLectureQuestions"),
				new Result(new Object[]{questions})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForQuestionImage(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		boolean fcImage = Boolean.parseBoolean(request.getParameter("fcImage"));

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		String questionImage = "";
		if (fcImage) {
			questionImage = this.questionService.getQuestionFcImage(questionId);
		} else {
			questionImage = this.questionService.getQuestionImage(questionId);
		}

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity("question#" + questionId, "questionImage"),
				new Result(new Object[]{questionImage})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForNewPiRound(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		Statement statement = new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("started"),
				this.activityBuilder.createActivity("question#" + questionId, "newPiRoundForQuestion")
		);

		String timeForNewPiRound = request.getParameter("time");
		if (!timeForNewPiRound.isEmpty()) {
			statement.setResult(new Result(new Object[]{"Delayed time: " + timeForNewPiRound}));
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
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("canceled"),
				this.activityBuilder.createActivity("question#" + questionId, "piRoundForQuestion")
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForResetPiRound(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("reset"),
				this.activityBuilder.createActivity("question#" + questionId, "piRoundForQuestion")
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForDisableVote(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));
		boolean disableVote = Boolean.parseBoolean(request.getParameter("disableVote"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("disable"),
				this.activityBuilder.createActivity("question#" + questionId, "votingForQuestion"),
				new Result(new Object[]{"Disable voting: " + disableVote})
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

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("disable"),
				this.activityBuilder.createActivity("time#" + String.valueOf(System.currentTimeMillis()), "votingForAllQuestions"),
				new Result(new Object[]{questionTypesToDisable})
		);
	}

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForPublishQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));
		boolean publish = Boolean.parseBoolean(request.getParameter("publish"));

		Question question = this.questionService.getQuestion(questionId);
		Result result = new Result(new Object[]{"Publish: " + publish});
		result.appendValue(question);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("publish"),
				this.activityBuilder.createActivity("question#" + questionId, "questionViaID"),
				result
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

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"publishedOn",
				new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("publish"),
				this.activityBuilder.createActivity(activityId, "allQuestions"),
				new Result(new Object[]{questionTypesToPublish})
		);
	}

	/**
	 * TODO TEST
	 *
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForPublishStatistics(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));
		boolean showStatistics = Boolean.parseBoolean(request.getParameter("showStatistics"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("show"),
				this.activityBuilder.createActivity("question#" + questionId, "statisticsForQuestion"),
				new Result(new Object[]{"Show statistics: " + showStatistics})
		);
	}

	/**
	 * TODO TEST
	 *
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForCorrectAnswer(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));
		boolean showCorrectAnswer = Boolean.parseBoolean(request.getParameter("showCorrectAnswer"));

		Question question = this.questionService.getQuestion(questionId);
		String correctAnswer = question.getCorrectAnswer();

		Map<String, String> result = new HashMap<>();
		result.put("Show correct answer", String.valueOf(showCorrectAnswer));
		if (showCorrectAnswer) {
			result.put("Correct answer", correctAnswer);
		}

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("publish"),
				this.activityBuilder.createActivity("question#" + questionId, "correctAnswerForQuestion"),
				new Result(new Object[]{result})
		);
	}

	/**
	 * TODO TEST
	 *
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

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"forSession",
				sessionKey,
				"on",
				new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("delete"),
				this.activityBuilder.createActivity(activityId, "skillQuestions"),
				new Result(new Object[]{questionTypesToDelete})
		);
	}

	/**
	 * TODO TEST
	 *
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetAnswersForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		boolean allAnswers = Boolean.parseBoolean(request.getParameter("all"));

		List<Answer> answers;
		if (allAnswers || request.getParameter("piround") == null) {
			answers = this.questionService.getAllAnswers(questionId, -1, -1);
		} else {
			Integer piRound = Integer.parseInt(request.getParameter("piround"));
			answers = questionService.getAnswers(questionId, piRound, -1, -1);
		}

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity("question#" + questionId, "answersForQuestion"),
				new Result(new Object[]{answers})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForSaveAnswerForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));
		List<Answer> savedAnswer = this.questionService.getAnswers(questionId, 0, 1);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("save"),
				this.activityBuilder.createActivity("question#" + questionId, "answerForQuestion"),
				new Result(new Object[]{savedAnswer})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForUpdateAnswerForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));
		List<Answer> savedAnswer = this.questionService.getAnswers(questionId, 0, 1);

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("update"),
				this.activityBuilder.createActivity("question#" + questionId, "answerForQuestion"),
				new Result(new Object[]{savedAnswer})
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForDeleteAnswerForQuestion(HttpServletRequest request) {
		// TODO change to use this logic for question id
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String questionId = (String) pathVariables.get("questionId");

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				this.activityBuilder.createActivity("question#" + questionId, "answerForQuestion"),
				new Result(new Object[] { this.questionService.getQuestion(questionId) })
		);
	}

	/**
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForDeleteAllAnswersForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				this.activityBuilder.createActivity("question#" + questionId, "allAnswersForQuestion"),
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

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"forSession",
				sessionKey,
				"on",
				new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("deleted"),
				this.activityBuilder.createActivity(activityId, "allAnswersAndQuestionForSession"),
				new Result(new Object[]{questionTypesToDelete})
		);
	}

	/**
	 * TODO TEST ALSO CHANGE THE NAMES TO HAVE GET PUT POST DELETE
	 *
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetAllRoundAnswersCountForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity("question#" + questionId, "totalAnswersForQuestion"),
				new Result(new Object[]{
						Arrays.asList(
								this.questionService.getAnswerCount(questionId, 1),
								this.questionService.getAnswerCount(questionId, 2)
						)})
		);
	}

	/**
	 * TODO TEST ALSO CHANGE THE NAMES TO HAVE GET PUT POST DELETE
	 *
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetTotalAmountOfAnswersForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity("question#" + questionId, "totalAnswersForQuestion"),
				new Result(new Object[]{this.questionService.getTotalAnswerCountByQuestion(questionId)})
		);
	}

	/**
	 * TODO TEST ALSO CHANGE THE NAMES TO HAVE GET PUT POST DELETE
	 *
	 * @param request
	 *
	 * @return
	 */
	public Statement buildForGetTotalAmountOfAnswersAndAbstiantForQuestion(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity("question#" + questionId, "totalAnswersAndAbstentionAnswersForQuestion"),
				new Result(new Object[]{
						Arrays.asList(
								this.questionService.getAnswerCount(questionId),
								this.questionService.getAbstentionAnswerCount(questionId)
						)})
		);
	}

	/**
	 * TODO TEST ALSO CHANGE THE NAMES TO HAVE GET PUT POST DELETE
	 *
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement buildForGetFreeTextAnswers(HttpServletRequest request) {
		String pathWithQuestionId = new AntPathMatcher().extractPathWithinPattern(
				"/{questionId}/**",
				request.getRequestURI()
		);

		String questionId = pathWithQuestionId.substring(0, pathWithQuestionId.indexOf("/"));

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity("question#" + questionId, "freeTextAnswersForQuestion"),
				new Result(new Object[]{this.questionService.getFreetextAnswers(questionId, -1, -1)})
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
				"imageFor/question",
				questionId,
				"answer",
				answerId
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "image"),
				new Result(new Object[]{this.questionService.getImage(questionId, answerId)})
		);
	}
}
