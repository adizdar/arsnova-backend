package ghost.xapi.statements.lectureQuestions;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import ghost.xapi.statements.UriMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class LectureQuestionsActionFactory {

	@Autowired
	private LectureQuestionsStatementBuilderService lectureQuestionsStatementBuilderService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			// Only placeholder.
			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException {
			if (requestUri.contains("bulk")) {
				return lectureQuestionsStatementBuilderService.buildForBulkAskedQuestions(request);
			} else if (requestUri.contains("questionimage")) {
				return lectureQuestionsStatementBuilderService.buildForQuestionImage(request);
			} else if (requestUri.contains("startnewpiround")) {
				return lectureQuestionsStatementBuilderService.buildForNewPiRound(request);
			} else if (requestUri.contains("canceldelayedpiround")) {
				return lectureQuestionsStatementBuilderService.buildForCancelPiRound(request);
			} else if (requestUri.contains("resetpiroundstate")) {
				return lectureQuestionsStatementBuilderService.buildForResetPiRound(request);
			} else if (requestUri.contains("disablevote")) {
				if (request.getParameterMap().containsKey("lecturequestionsonly")
						|| request.getParameterMap().containsKey("preparationquestionsonly")) {
					return lectureQuestionsStatementBuilderService.buildForDisableVoteForAllQuestions(request);
				}

				return lectureQuestionsStatementBuilderService.buildForDisableVote(request);
			} else if (requestUri.contains("publish")) {
				if (request.getParameterMap().containsKey("lecturequestionsonly")
						|| request.getParameterMap().containsKey("preparationquestionsonly")) {
					return lectureQuestionsStatementBuilderService.buildForPublishAllQuestions(request);
				}

				return lectureQuestionsStatementBuilderService.buildForPublishQuestion(request);
			} else if (UriMatchService.doesUriMatchWithPattern(request, "/lecturerquestion/{questionId}/answer/{answerId}/image") ||
					UriMatchService.doesUriMatchWithPattern(request, "/lecturerquestion/{questionId}/answer/{answerId}/image/")) {
				return lectureQuestionsStatementBuilderService.buildForGetImage(request);
			} else if (requestUri.contains("publishstatistics")) {
				return lectureQuestionsStatementBuilderService.buildForPublishStatistics(request);
			} else if (requestUri.contains("publishcorrectanswer")) {
				return lectureQuestionsStatementBuilderService.buildForCorrectAnswer(request);
			}  else if (requestUri.contains("answer") && !requestUri.contains("answercount")) {
				String methodName = request.getMethod().toLowerCase();
				switch (methodName) {
					case "post":
						return lectureQuestionsStatementBuilderService.buildForSaveAnswerForQuestion(request);
					case "get":
						return lectureQuestionsStatementBuilderService.buildForGetAnswersForQuestion(request);
					case "put":
						return lectureQuestionsStatementBuilderService.buildForUpdateAnswerForQuestion(request);
					case "delete":
						Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
						if (pathVariables.isEmpty()) {
							return lectureQuestionsStatementBuilderService.buildForDeleteQuestionsAllAnswersForSession(request);
						} else if (pathVariables.containsKey("answerId")) {
							return lectureQuestionsStatementBuilderService.buildForDeleteAnswerForQuestion(request);
						}

						return lectureQuestionsStatementBuilderService.buildForDeleteAllAnswersForQuestion(request);
				}
			} else if (requestUri.contains("allroundanswercount")) {
				return lectureQuestionsStatementBuilderService.buildForGetAllRoundAnswersCountForQuestion(request);
			} else if (requestUri.contains("totalanswercount")) {
				return lectureQuestionsStatementBuilderService.buildForGetTotalAmountOfAnswersForQuestion(request);
			} else if (requestUri.contains("answerandabstentioncount")) {
				return lectureQuestionsStatementBuilderService.buildForGetTotalAmountOfAnswersAndAbstiantForQuestion(request);
			} else if (requestUri.contains("getFreetextAnswers")) {
				return lectureQuestionsStatementBuilderService.buildForGetFreeTextAnswers(request);
			} else if (requestUri.contains("lecturerquestion")) {
				String methodName = request.getMethod().toLowerCase();
				switch (methodName) {
					case "post":
						return lectureQuestionsStatementBuilderService.buildForAskedQuestion(request);
					case "get":
						Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
						if (pathVariables.isEmpty()) {
							return lectureQuestionsStatementBuilderService.buildForGetQuestions(request);
						}

						return lectureQuestionsStatementBuilderService.buildForGetQuestionViaId(request);
					case "put":
						return lectureQuestionsStatementBuilderService.buildForGetQuestionViaId(request);
					case "delete":
						return lectureQuestionsStatementBuilderService.buildForDeleteSkillQuestions(request);

				}
			}
			
			return null;
		}
	};

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		return StatementBuilder.createFromRequestWithIOOperations(request, this.block);
	}
}
