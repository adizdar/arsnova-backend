package ghost.xapi.interceptor;

import ghost.xapi.entities.actor.Actor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends AbstractStatementBuilderInterceptor {

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (response.getStatus() == STATUS_CODE_SUCCESS) {
			Actor actor = new Actor(request.getParameter("user"), request.getParameter("type"));
			request.getSession().setAttribute(actor.getClass().toString(), actor);

			super.postHandle(request, response, handler, modelAndView);
		}
	}
}
