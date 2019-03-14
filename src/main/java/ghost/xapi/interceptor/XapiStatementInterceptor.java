package ghost.xapi.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class XapiStatementInterceptor extends AbstractStatementBuilderInterceptor {
	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @throws Exception
	 */
	@Override
	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			ModelAndView modelAndView
	) throws Exception {
		if (this.isXapiSupportActive && this.checkIfStatusCodeIsValid(response.getStatus())) {
			this.prepareStatement(request, response, (HandlerMethod) handler);
		}

		super.postHandle(request, response, handler, modelAndView);
	}

}
