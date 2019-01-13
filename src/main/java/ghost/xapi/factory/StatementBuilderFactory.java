package ghost.xapi.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

@Component
public class StatementBuilderFactory {

	@Autowired
	public LoginActionFactory loginActionFactory;

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
		}

		// todo throw exception to log
		return null;
	}

	public String getStatementAsJSONStringForHandler(HandlerMethod handler, HttpServletRequest request) {
		Statement statement = this.getStatementForHandler(handler, request);
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			return ow.writeValueAsString(statement);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param requestUri
	 * @return String
	 */
	private String getServiceNameFromURI(String requestUri) {
		if (requestUri.substring(0, requestUri.length() - 1) == "/") {
			requestUri = requestUri.substring(0, requestUri.length() - 1);
		}

		return requestUri.substring(requestUri.lastIndexOf("/") + 1, requestUri.length());
	}

}
