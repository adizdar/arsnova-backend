package ghost.xapi.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MultiReadHttpFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (this.checkIfUriIsBlacklisted((HttpServletRequest) servletRequest)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		if (!this.checkIfUriIsWhitelisted((HttpServletRequest) servletRequest)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		// Wrap the request in order to read the input stream multiple times.
		MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(
				(HttpServletRequest) servletRequest
		);

		filterChain.doFilter(multiReadRequest, servletResponse);
	}

	/**
	 * Because we can't specify the request method only the path.
	 *
	 * @param request
	 * @return boolean
	 */
	protected boolean checkIfUriIsBlacklisted(HttpServletRequest request) {
		String requestUri = request.getRequestURI().toLowerCase();
		String requestMethod = request.getMethod().toLowerCase();
		if (requestUri.equals("/motd/") || requestUri.equals("/motd")) {
			switch (requestMethod) {
				case "get":
					return true;
			}
		} else if (requestUri.contains("motd/userlist")) {
			switch (requestMethod) {
				case "get":
					return true;
			}
		}

		return false;
	}

	/**
	 * Because we can't specify the request method only the path.
	 *
	 * @param request
	 * @return boolean
	 */
	protected boolean checkIfUriIsWhitelisted(HttpServletRequest request) {
		String requestUri = request.getRequestURI().toLowerCase();
		String requestMethod = request.getMethod().toLowerCase();
		if (requestUri.equals("/session/") || requestUri.equals("/session")) {
			switch (requestMethod) {
				case "post":
					return true;
					default:
						return false;
			}
		}

		return true;
	}

}
