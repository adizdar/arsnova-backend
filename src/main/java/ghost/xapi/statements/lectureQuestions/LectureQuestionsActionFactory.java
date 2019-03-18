package ghost.xapi.statements.lectureQuestions;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class LectureQuestionsActionFactory {

	private AntPathMatcher patchMatcher = new AntPathMatcher();

	@Autowired
	private LectureQuestionsStatementBuilderService lectureQuestionsStatementBuilderService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		String requestUri = request.getRequestURI().toLowerCase();
		if (requestUri.contains("bulk")) {
			return this.lectureQuestionsStatementBuilderService.buildForBulkAskedQuestions(request);
		} else if (requestUri.contains("questionimage")) {
			return this.lectureQuestionsStatementBuilderService.buildForQuestionImage(request);
		} else if (requestUri.contains("startnewpiround")) {
			return this.lectureQuestionsStatementBuilderService.buildForNewPiRound(request);
		} else if (requestUri.contains("canceldelayedpiround")) {
			return this.lectureQuestionsStatementBuilderService.buildForCancelPiRound(request);
		} else if (requestUri.contains("resetpiroundstate")) {
			return this.lectureQuestionsStatementBuilderService.buildForResetPiRound(request);
		} else if (requestUri.contains("disablevote")) {
			if (request.getParameterMap().containsKey("lecturequestionsonly") || request.getParameterMap().containsKey("preparationquestionsonly")) {
				return this.lectureQuestionsStatementBuilderService.buildForDisableVoteForAllQuestions(request);
			}

			return this.lectureQuestionsStatementBuilderService.buildForDisableVote(request);
		} else if (requestUri.contains("publish")) {
			if (request.getParameterMap().containsKey("lecturequestionsonly") || request.getParameterMap().containsKey("preparationquestionsonly")) {
				return this.lectureQuestionsStatementBuilderService.buildForPublishAllQuestions(request);
			}

			return this.lectureQuestionsStatementBuilderService.buildForPublishQuestion(request);
		} else if (this.doesUriMatchWithPattern(request, "/lecturerquestion/{questionId}/answer/{answerId}/image") ||
				this.doesUriMatchWithPattern(request, "/lecturerquestion/{questionId}/answer/{answerId}/image/")) {
			return this.lectureQuestionsStatementBuilderService.buildForGetImage(request);
		} else if (requestUri.contains("publishstatistics")) {
			return this.lectureQuestionsStatementBuilderService.buildForPublishStatistics(request);
		} else if (requestUri.contains("publishcorrectanswer")) {
			return this.lectureQuestionsStatementBuilderService.buildForCorrectAnswer(request);
		}  else if (requestUri.contains("answer") && !requestUri.contains("answercount")) {
			String methodName = request.getMethod().toLowerCase();
			switch (methodName) {
				case "post":
					return this.lectureQuestionsStatementBuilderService.buildForSaveAnswerForQuestion(request);
				case "get":
					return this.lectureQuestionsStatementBuilderService.buildForGetAnswersForQuestion(request);
				case "put":
					return this.lectureQuestionsStatementBuilderService.buildForUpdateAnswerForQuestion(request);
				case "delete":
					Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
					if (pathVariables.isEmpty()) {
						return this.lectureQuestionsStatementBuilderService.buildForDeleteQuestionsAllAnswersForSession(request);
					} else if (pathVariables.containsKey("answerId")) {
						return this.lectureQuestionsStatementBuilderService.buildForDeleteAnswerForQuestion(request);
					}

					return this.lectureQuestionsStatementBuilderService.buildForDeleteAllAnswersForQuestion(request);
			}
		} else if (requestUri.contains("allroundanswercount")) {
			return this.lectureQuestionsStatementBuilderService.buildForGetAllRoundAnswersCountForQuestion(request);
		} else if (requestUri.contains("totalanswercount")) {
			return this.lectureQuestionsStatementBuilderService.buildForGetTotalAmountOfAnswersForQuestion(request);
		} else if (requestUri.contains("answerandabstentioncount")) {
			return this.lectureQuestionsStatementBuilderService.buildForGetTotalAmountOfAnswersAndAbstiantForQuestion(request);
		} else if (requestUri.contains("getFreetextAnswers")) {
			return this.lectureQuestionsStatementBuilderService.buildForGetFreeTextAnswers(request);
		} else if (requestUri.contains("lecturerquestion")) {
			String methodName = request.getMethod().toLowerCase();
			switch (methodName) {
				case "post":
					return this.lectureQuestionsStatementBuilderService.buildForAskedQuestion(request);
				case "get":
					Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
					if (pathVariables.isEmpty()) {
						return this.lectureQuestionsStatementBuilderService.buildForGetQuestions(request);
					}

					return this.lectureQuestionsStatementBuilderService.buildForGetQuestionViaId(request);
				case "put":
					return this.lectureQuestionsStatementBuilderService.buildForGetQuestionViaId(request);
				case "delete":
					return this.lectureQuestionsStatementBuilderService.buildForDeleteSkillQuestions(request);

			}
		}

		// This case should only happen if ARSNOVA registers a new action
		// TODO custom exception
		throw new NullPointerException();
	}

	/**
	 * TODO move
	 * @param request
	 * @param uriToMatch
	 * @return
	 */
	protected boolean doesUriMatchWithPattern(HttpServletRequest request, String uriToMatch) {
		String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		return bestMatchPattern.equals(uriToMatch);
	}
}
