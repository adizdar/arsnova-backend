package ghost.xapi.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ghost.xapi.entities.FailedStatementCreationException;
import ghost.xapi.entities.Statement;
import ghost.xapi.log.XAPILogger;
import ghost.xapi.statements.audienceQuestions.AudienceQuestionActionFactory;
import ghost.xapi.statements.authentication.LoginActionFactory;
import ghost.xapi.statements.feedback.CourseActionFactory;
import ghost.xapi.statements.lectureQuestions.LectureQuestionsActionFactory;
import ghost.xapi.statements.motd.MotdActionFactory;
import ghost.xapi.statements.session.SessionActionFactory;
import ghost.xapi.statements.statistics.StatisticsActionFactory;
import ghost.xapi.statements.user.UserActionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

@Component
public class StatementBuilderFactory {

	@Autowired
	private LoginActionFactory loginActionFactory;

	@Autowired
	private AudienceQuestionActionFactory audienceQuestionActionFactory;

	@Autowired
	private LectureQuestionsActionFactory lectureQuestionsActionFactory;

	@Autowired
	private SessionActionFactory sessionActionFactory;

	@Autowired
	private UserActionFactory userActionFactory;

	@Autowired
	private StatisticsActionFactory statisticsActionFactory;

	@Autowired
	private MotdActionFactory motdActionFactory;

	@Autowired
	private CourseActionFactory courseActionFactory;

	/**
	 * @param handler
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementForHandler(HandlerMethod handler, HttpServletRequest request) {
		try {
			String className = ClassUtils.getUserClass(handler.getBean().getClass()).getSimpleName().toLowerCase();
			Statement statement = null;
			switch (className) {
				case "logincontroller":
					statement = this.loginActionFactory.getStatementViaServiceName(request);
					break;
				case "audiencequestioncontroller":
					statement = this.audienceQuestionActionFactory.getStatementViaServiceName(request);
					break;
				case "lecturerquestioncontroller":
					statement = this.lectureQuestionsActionFactory.getStatementViaServiceName(request);
					break;
				case "sessioncontroller":
					statement = this.sessionActionFactory.getStatementViaServiceName(request);
					break;
				case "usercontroller":
					statement = this.userActionFactory.getStatementViaServiceName(request);
					break;
				case "statisticscontroller":
					statement = this.statisticsActionFactory.getStatementViaServiceName(request);
					break;
				case "motdcontroller":
					statement = this.motdActionFactory.getStatementViaServiceName(request);
					break;
				case "coursecontroller":
					statement = this.courseActionFactory.getStatementViaServiceName(request);
					break;
			}

			if (statement == null) {
				throw new NotRegisteredControllerException(
						"Controller " + className + " is unhandled for this action type. Maybe a new controller has been added?"
				);
			}

			// TODO move
			// Always set the caller uri, for easier tracking.
			statement.getActivity().setUri(request.getRequestURI());
			statement.getActivity().setRequestMethod(request.getMethod());

			return statement;
		} catch (Exception e) {
			XAPILogger.ERROR.error(e.getMessage());

			return new Statement(new FailedStatementCreationException(e.getMessage(), e.getCause()));
		}
	}

	/**
	 * @param statement
	 * @return String
	 */
	public String convertStatementToJson(Statement statement) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			return ow.writeValueAsString(statement);
		} catch (JsonProcessingException e) {
			XAPILogger.ERROR.error(e.getStackTrace());
			XAPILogger.ERROR.error(e.getMessage());
		}

		return null;
	}

}
