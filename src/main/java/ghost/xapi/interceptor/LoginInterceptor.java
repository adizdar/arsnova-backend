package ghost.xapi.interceptor;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends AbstractStatementBuilderInterceptor {

	private static final Logger LOGGER = Logger.getLogger(LoginInterceptor.class);

	/**
	 * Store the Actor in the session.
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
// TODO go throught one interceptor here
		if (this.checkStatusCodeIsValid(response.getStatus())) {
			this.prepareStatement(request, response, (HandlerMethod) handler);
		}

		super.postHandle(request, response, handler, modelAndView);
	}

}
