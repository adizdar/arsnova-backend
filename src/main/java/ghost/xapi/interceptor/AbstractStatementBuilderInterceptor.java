package ghost.xapi.interceptor;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilderFactory;
import ghost.xapi.log.XAPILogger;
import ghost.xapi.client.TLAConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractStatementBuilderInterceptor extends HandlerInterceptorAdapter {

	@Value(value = "${xapi.support.activate}")
	protected boolean isXapiSupportActive;

	@Autowired
	private StatementBuilderFactory statementBuilderFactory;

	@Autowired
	private TLAConnectorService xapiConnectorService;

	@Value("${root-url}")
	private String rootUrl;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 */
	protected void prepareStatement(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
		Statement statement = this.statementBuilderFactory.getStatementForHandler(handler, request);

		if (this.rootUrl.contains("localhost") && statement != null) {
			if (this.rootUrl.contains("localhost")) {
				this.writeStatementToLog(statement);
			}

			this.xapiConnectorService.send(statement);
		}
	}

	/**
	 * @param statement
	 */
	protected void writeStatementToLog(Statement statement) {
		XAPILogger.JSON.info(
				this.statementBuilderFactory.convertStatementToJson(statement)
		);
	}

	/**
	 * @param statusCode
	 *
	 * @return
	 */
	protected boolean checkIfStatusCodeIsValid(int statusCode) {
		int firstDigit = Integer.parseInt(Integer.toString(statusCode).substring(0, 1));

		return firstDigit != 4 && firstDigit != 5;
	}
}
