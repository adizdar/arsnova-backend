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
public class MotdActionFactory {

	private AntPathMatcher patchMatcher = new AntPathMatcher();

	@Autowired
	private MotdStatementBuilderService motdStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		String requestUri = request.getRequestURI().toLowerCase();
		String requestMethod = request.getMethod().toLowerCase();

		if (this.doesUriMatchWithPattern(request, "/motd")
				|| this.doesUriMatchWithPattern(request, "/motd/")) {
			switch (requestMethod) {
				case "post":
					return this.motdStatementBuilderService.buildForPostNewtMotd(request);
				case "get":
					return this.motdStatementBuilderService.buildForGetMotd(request);
			}
		} else if (this.doesUriMatchWithPattern(request, "/motd/{motdkey}/")
				|| this.doesUriMatchWithPattern(request, "/motd/{motdkey}")) {
			switch (requestMethod) {
				case "put":
					return this.motdStatementBuilderService.buildForUpdateMotd(request);
				case "delete":
					return this.motdStatementBuilderService.buildForDeleteMotd(request);
			}
		} else if (requestUri.contains("userlist")) {
			switch (requestMethod) {
				case "get":
					return this.motdStatementBuilderService.buildForGetUserMotdList(request);
				case "post":
					return this.motdStatementBuilderService.buildForPostUserMotdList(request);
				case "put":
					return this.motdStatementBuilderService.buildForUpdateUserMotdList(request);
			}
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
