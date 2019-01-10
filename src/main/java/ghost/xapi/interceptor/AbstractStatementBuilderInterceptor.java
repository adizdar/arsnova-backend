package ghost.xapi.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ghost.xapi.entities.Statement;
import ghost.xapi.factory.StatementBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		Statement statement = statementBuilderFactory.getStatementForHandler((HandlerMethod) handler, request);

		// TODO method
		if (request.getParameter(XAPI_PATH) != null) {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(statement);

			response.getWriter().print(json);
			response.getWriter().flush();
		}

		// TODO implement thread connection to TLA
		super.postHandle(request, response, handler, modelAndView);
	}
}
