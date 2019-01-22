package ghost.xapi.interceptor;

import ghost.xapi.entities.Statement;
import ghost.xapi.factory.StatementBuilderFactory;
import ghost.xapi.log.XAPILogger;
import ghost.xapi.services.XAPIConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractStatementBuilderInterceptor extends HandlerInterceptorAdapter {

	protected final static String XAPI_PATH = "returnxapi";
	protected final static int STATUS_CODE_SUCCESS = 200;

	protected RestTemplate restTemplate;

	@Autowired
	private StatementBuilderFactory statementBuilderFactory;

	@Autowired
	private XAPIConnectorService xapiConnectorService;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 */
	protected void prepareStatement(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
		Statement statement = this.statementBuilderFactory.getStatementForHandler(handler, request);

		// TODO support fo config mode, in dev mode it should always be logged
		if (request.getParameter(XAPI_PATH) != null) {
			this.printXApiJson(statement);
		}

		XAPILogger.LOGGER.info("CONNECT");
		this.xapiConnectorService.send(statement);
	}

	/**
	 * @param statement
	 */
	protected void printXApiJson(Statement statement) {
		XAPILogger.JSON.info(
				this.statementBuilderFactory.convertStatementToJson(statement)
		);
	}
}
