package ghost.xapi.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ghost.xapi.entities.Statement;
import ghost.xapi.log.XAPILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
		String className = handler.getBean().getClass().getSimpleName();
		switch (className) {
			case "LoginController":
				return this.loginActionFactory.getStatementViaServiceName(
						request,
						this.getServiceNameFromURI(request.getRequestURI())
				);
			case "AudienceQuestionController":
				this.audienceQuestionActionFactory.getStatementViaServiceName(
						request,
						this.getServiceNameFromURI(request.getRequestURI())
				);

		}

		// This case should only happen if ARSNOVA registers a new Controller
		// TODO custom exception
		throw new NullPointerException();
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

	/**
	 * @param requestUri
	 * @return String
	 */
	private String getServiceNameFromURI(String requestUri) {
		if (requestUri.substring(0, requestUri.length() - 1).equals("/")) {
			requestUri = requestUri.substring(0, requestUri.length() - 1);
		}

		return requestUri.substring(requestUri.lastIndexOf("/") + 1, requestUri.length());
	}

}
