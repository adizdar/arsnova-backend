package ghost.xapi.interceptor;

import ghost.xapi.entities.actor.Actor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutInterceptor extends AbstractStatementBuilderInterceptor {

	/**
	 * By logut we need to check before the api controller is called, otherwise the Actor can't be retrieved.
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return boolean
	 * @throws Exception
	 */
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//		// Check the session if the User is already logged in.
//		if (request.getSession().getAttribute(Actor.class.toString()) != null) {
//			return super.preHandle(request, response, handler);
//		}
//
//		return false;
//	}

}
