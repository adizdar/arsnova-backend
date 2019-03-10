package ghost.xapi.interceptor;

import ghost.xapi.config.MultiReadHttpServletRequest;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class MultiReadHttpFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		// Wrap the request in order to read the input stream multiple times.
		MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(
				(HttpServletRequest) servletRequest
		);

		filterChain.doFilter(multiReadRequest, servletResponse);
	}

}
