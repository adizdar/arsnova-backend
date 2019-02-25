package ghost.xapi.interceptor;

import ghost.xapi.entities.actor.Actor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

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
	@Override
	@PreAuthorize("isAuthenticated()")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		this.prepareStatement(request, response, (HandlerMethod) handler);

		return super.preHandle(request, response, handler);
	}

}
