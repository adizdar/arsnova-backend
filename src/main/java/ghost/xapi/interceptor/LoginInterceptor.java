package ghost.xapi.interceptor;

import ghost.xapi.entities.actor.Actor;
import org.apache.log4j.Logger;
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
		if (response.getStatus() == STATUS_CODE_SUCCESS) {
			// TODO probably only use the user service, this should be removed from the session...
			Actor actor = new Actor(request.getParameter("user"), request.getParameter("type"));
			request.getSession().setAttribute(Actor.class.getName(), actor);

			super.postHandle(request, response, handler, modelAndView);
		}
	}

}
