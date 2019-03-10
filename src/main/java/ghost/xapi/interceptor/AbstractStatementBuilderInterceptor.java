package ghost.xapi.interceptor;

import ghost.xapi.entities.Statement;
import ghost.xapi.factory.StatementBuilderFactory;
import ghost.xapi.log.XAPILogger;
import ghost.xapi.services.XAPIConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractStatementBuilderInterceptor extends HandlerInterceptorAdapter {

	protected final static String XAPI_PATH = "returnxapi";

	@Autowired
	private StatementBuilderFactory statementBuilderFactory;

	@Autowired
	private XAPIConnectorService xapiConnectorService;

	@Value("${root-url}")
	private String rootUrl;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 */
	protected void prepareStatement(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
		Statement statement = this.statementBuilderFactory.getStatementForHandler(handler, request);
		// TODO support fo config mode, in dev mode it should always be logged
		if (request.getParameter(XAPI_PATH) != null || this.rootUrl.contains("localhost")) {
			this.printXApiJson(statement);
		}

		if (statement != null) {
			this.xapiConnectorService.send(statement);
		}
	}
// TODO remove unused interceptors
	/**
	 * @param statement
	 */
	protected void printXApiJson(Statement statement) {
		if (statement.getFailedStatementCreationException() != null) {
			//XAPILogger.ERROR.error(this.statementBuilderFactory.convertStatementToJson(statement));
		} else {
			XAPILogger.JSON.info(
					this.statementBuilderFactory.convertStatementToJson(statement)
			);
		}
	}

	/**
	 * @param statusCode
	 * @return
	 */
	protected boolean checkStatusCodeIsValid(int statusCode) {
		int firstDigit = Integer.parseInt(Integer.toString(statusCode).substring(0, 1));

		return firstDigit != 4 && firstDigit != 5;
	}
}
