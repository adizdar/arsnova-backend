package ghost.xapi.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ghost.xapi.entities.FailedStatementCreationException;
import ghost.xapi.entities.Statement;
import ghost.xapi.log.XAPILogger;
import ghost.xapi.statments.audienceQuestions.AudienceQuestionActionFactory;
import ghost.xapi.statments.authentication.LoginActionFactory;
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

	/**
	 * @param handler
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementForHandler(HandlerMethod handler, HttpServletRequest request) {
		try {
			String className = ClassUtils.getUserClass(handler.getBean().getClass()).getSimpleName();
			switch (className) {
				case "LoginController":
					return this.loginActionFactory.getStatementViaServiceName(request);
				case "AudienceQuestionController":
					return this.audienceQuestionActionFactory.getStatementViaServiceName(request);

			}

			throw new NotRegisteredControllerException(
					"Controller is unhandled for this action type. Maybe a new controller has been added?"
			);
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
