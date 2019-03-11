package ghost.xapi.statements.motd;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class SocketActionFactory {

	private AntPathMatcher patchMatcher = new AntPathMatcher();

	@Autowired
	private SocketStatementBuilderService socketStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String requestUri = request.getRequestURI().toLowerCase();

		if (requestUri.contains("register")) {
			return this.socketStatementBuilderService.buildForRegisterUser(request);
		} else if (requestUri.contains("activate")) {
			return this.socketStatementBuilderService.buildForActivateUser(request);
		} else if (requestUri.contains("resetpassword")) {
			return this.socketStatementBuilderService.buildForResetPassword(request);
		} else if (this.doesUriMatchWithPattern(request, "/{username}/")
				|| this.doesUriMatchWithPattern(request, "/{username}")) {
			return this.socketStatementBuilderService.buildForDeleteUser(request);
		}

		// This case should only happen if ARSNOVA registers a new action or we don't support the action
		// TODO custom exception
		throw new NullPointerException();
	}


	/**
	 * TODO move to abstract class
	 * @param request
	 * @param uriToMatch
	 * @return
	 */
	protected boolean doesUriMatchWithPattern(HttpServletRequest request, String uriToMatch) {
		String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		return bestMatchPattern.equals(uriToMatch);
	}

}
