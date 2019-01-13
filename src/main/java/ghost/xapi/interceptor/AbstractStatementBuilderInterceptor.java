package ghost.xapi.interceptor;

import ghost.xapi.entities.Statement;
import ghost.xapi.factory.StatementBuilderFactory;
import ghost.xapi.log.XAPILogger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AbstractStatementBuilderInterceptor extends HandlerInterceptorAdapter {

	protected final static String XAPI_PATH = "returnxapi";
	protected final static int STATUS_CODE_SUCCESS = 200;

	@Autowired
	private StatementBuilderFactory statementBuilderFactory;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 *
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		this.prepareStatement(request, response, (HandlerMethod) handler);

		super.postHandle(request, response, handler, modelAndView);
	}

	/**
	 * @param request
	 * @param response
	 * @param handler
	 *
	 * @return boolean
	 * @throws Exception
	 */
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//		this.prepareStatement(request, response, (HandlerMethod) handler);
//
//		return super.preHandle(request, response, handler);
//	}

	/**
	 * @param request
	 * @param response
	 * @param handler
	 */
	private void prepareStatement(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
		if (response.getStatus() != STATUS_CODE_SUCCESS) {
			return;
		}

		if (request.getParameter(XAPI_PATH) != null) {
			this.printXApiJson(request, response, handler);
		} else {
			Statement statement = this.statementBuilderFactory.getStatementForHandler(handler, request);
		}


		// TODO implement thread connection to TLA
	}

	/**
	 * @param response
	 */
	private void printXApiJson(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String json = this.statementBuilderFactory.getStatementAsJSONStringForHandler((HandlerMethod) handler, request);

		try {
			// TODO remove
			response.getWriter().print(json);

			// Log json to file.
			XAPILogger.JSON.info(json);
			XAPILogger.ERROR.error("HAHAHAHAHAHAH");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
